package run.ice.zero.base.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import run.ice.zero.api.base.api.FileApi;
import run.ice.zero.api.base.error.BaseError;
import run.ice.zero.api.base.model.file.FileData;
import run.ice.zero.api.base.model.file.FileParam;
import run.ice.zero.base.service.FileService;
import run.ice.zero.common.error.AppException;
import run.ice.zero.common.model.Request;
import run.ice.zero.common.model.Response;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
public class FileController implements FileApi {

    @Resource
    private FileService fileService;

    @Override
    public Response<FileData> fileInfo(Request<FileParam> request) {
        FileData data = fileService.info(request.getParam());
        return new Response<>(data);
    }

    @Operation(summary = "upload 上传文件", description = "使用 multipart/form-data 上传文件，key = file")
    @PostMapping(path = "file-upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Response<FileData> fileUpload(@RequestPart(name = "file") MultipartFile multipartFile) {
        FileData data = fileService.upload(multipartFile);
        return new Response<>(data);
    }

    @Operation(summary = "download 下载文件", description = "http response 的 header 中包含文件名称， body 是文件数据流")
    @PostMapping(path = "file-download")
    public void fileDownload(@RequestBody @Valid Request<FileParam> request, HttpServletResponse response) {
        File file = fileService.download(request.getParam());
        fileService.output(response, file);
    }

    @Operation(summary = "view 预览文件", description = "http response 的 header 中包含文件名称， body 是文件数据流")
    @GetMapping(path = "file-view")
    public void fileView(@RequestParam(name = "id") String id, @RequestParam(name = "code") String code, HttpServletResponse response) {
        FileParam param = new FileParam();
        param.setId(id);
        param.setCode(code);
        FileData data = fileService.info(param);
        if (!code.equals(data.getCode())) {
            throw new AppException(BaseError.FILE_CODE_ERROR, id + " : " + code);
        }
        String name = URLEncoder.encode(data.getOrigin(), StandardCharsets.UTF_8);
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + name + "\"; filename*=utf-8' '" + name);
        File file = fileService.download(param);
        fileService.output(response, file);
    }

}
