package run.ice.zero.api.base.helper;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import run.ice.zero.api.base.error.BaseError;
import run.ice.zero.api.base.model.file.FileData;
import run.ice.zero.api.base.model.file.FileParam;
import run.ice.zero.common.constant.AppConstant;
import run.ice.zero.common.error.AppError;
import run.ice.zero.common.error.AppException;
import run.ice.zero.common.model.Request;
import run.ice.zero.common.model.Response;
import run.ice.zero.common.util.io.FileUtil;
import run.ice.zero.common.util.math.RadixUtil;
import run.ice.zero.common.util.security.HashUtil;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class FileHelper {

    @Resource
    private RestTemplate restTemplate;

    @Value("${zero.service.zero.base:base.zero}")
    private String base;

    public String code(String id) {
        if (null == id || id.isEmpty()) {
            return null;
        }
        if (!id.matches("^\\d+$")) {
            return null;
        }
        return RadixUtil.convert(id, 10, 62);
    }

    /**
     * @param file File
     * @return FileData
     */
    public FileData upload(File file) {
        String hash = HashUtil.sha256(file);
        FileSystemResource fileResource = new FileSystemResource(file);
        String url = baseService() + "/" + AppConstant.API + "/" + "file-upload";
        ParameterizedTypeReference<Response<FileData>> typeReference = new ParameterizedTypeReference<>() {
        };
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set(AppConstant.X_HASH, hash);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileResource);
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(body, headers);
        ResponseEntity<Response<FileData>> responseEntity;
        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, typeReference);
        } catch (Exception e) {
            log.error("url: {}", url);
            log.error(e.getMessage(), e);
            throw new AppException(BaseError.INVOKE_FILE_ERROR, e.getMessage());
        }
        HttpStatusCode httpStatusCode = responseEntity.getStatusCode();
        if (!httpStatusCode.is2xxSuccessful()) {
            log.error("url: {}", url);
            throw new AppException(AppError.HTTP_STATUS_IS_NOT_2XX, httpStatusCode.toString());
        }
        Response<FileData> response = responseEntity.getBody();
        if (null == response) {
            throw new AppException(AppError.HTTP_RESPONSE_IS_NULL);
        } else if (!AppError.OK.code.equals(response.getCode())) {
            throw new AppException(response.getCode(), response.getMessage());
        }
        // boolean b = file.delete();
        return response.getData();
    }

    /**
     * @param param FileParam
     * @return File
     */
    public File download(FileParam param) {
        String id = param.getId();
        String code = param.getCode();
        if (null == code || code.isEmpty()) {
            code = code(id);
        }
        if (null == code) {
            throw new AppException(BaseError.FILE_NOT_EXIST);
        }
        param.setCode(code);
        Request<FileParam> request = new Request<>(param);
        String url = baseService() + "/" + AppConstant.API + "/" + "file-download";
        ParameterizedTypeReference<byte[]> typeReference = new ParameterizedTypeReference<>() {
        };
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Request<FileParam>> httpEntity = new HttpEntity<>(request, headers);
        ResponseEntity<byte[]> responseEntity;
        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, typeReference);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new AppException(BaseError.INVOKE_FILE_ERROR, e.getMessage());
        }
        /*
         * http status == 200 ?
         */
        HttpStatusCode httpStatusCode = responseEntity.getStatusCode();
        if (!httpStatusCode.is2xxSuccessful()) {
            throw new AppException(AppError.HTTP_STATUS_IS_NOT_2XX, httpStatusCode.toString());
        }
        /*
         * header Content-Disposition empty ?
         */
        HttpHeaders httpHeaders = responseEntity.getHeaders();
        ContentDisposition contentDisposition = httpHeaders.getContentDisposition();
        if (!contentDisposition.isAttachment()) {
            throw new AppException(BaseError.FILE_NOT_EXIST);
        }
        /*
         * body empty ?
         */
        byte[] bytes = responseEntity.getBody();
        if (null == bytes || bytes.length == 0) {
            throw new AppException(BaseError.FILE_NOT_EXIST, param.toJson());
        }
        String fileName = responseEntity.getHeaders().getContentDisposition().getFilename();
        String filePath = "/tmp/" + param.getId() + "/" + fileName;
        File file = new File(filePath);
        if (!file.getParentFile().isDirectory()) {
            boolean b = file.getParentFile().mkdirs();
            assert b;
        }
        try (FileOutputStream fos = new FileOutputStream(file); BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            bos.write(responseEntity.getBody());
            bos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return file;
    }

    public File transfer(MultipartFile multipartFile) {
        LocalDateTime localDateTime = LocalDateTime.now();
        String path = localDateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd/HHmmss")) + String.format("%09d", localDateTime.getNano());
        String directory = File.separator + "tmp" + File.separator + path;
        File dir = new File(directory);
        if (!dir.exists() || !dir.isDirectory()) {
            boolean b = dir.mkdirs();
            assert b;
        }
        String originalFilename = multipartFile.getOriginalFilename();
        assert originalFilename != null;
        String fileName = originalFilename.replace("/", "_");
        File file = new File(new File(directory).getAbsolutePath() + File.separator + fileName);
        try {
            multipartFile.transferTo(file);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
        return file;
    }

    public void output(HttpServletResponse response, File file) {
        response.setHeader(AppConstant.X_HASH, HashUtil.sha256(file));
        response.setContentType(FileUtil.contentType(file));
        String fileName = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8);
        if (response.getHeader(HttpHeaders.CONTENT_DISPOSITION) == null) {
            response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"; filename*=utf-8' '" + fileName);
        }

        try (FileInputStream fis = new FileInputStream(file); BufferedInputStream bis = new BufferedInputStream(fis); OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private String baseService() {
        return (base.matches("^https?://.*") ? base : "http://" + base);
    }

}
