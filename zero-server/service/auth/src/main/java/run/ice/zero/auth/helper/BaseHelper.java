package run.ice.zero.auth.helper;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.ice.zero.api.auth.error.AuthError;
import run.ice.zero.api.base.api.CaptchaApi;
import run.ice.zero.api.base.api.FileApi;
import run.ice.zero.api.base.helper.FileHelper;
import run.ice.zero.api.base.model.captcha.CaptchaParam;
import run.ice.zero.api.base.model.file.FileData;
import run.ice.zero.api.base.model.file.FileParam;
import run.ice.zero.common.error.AppException;
import run.ice.zero.common.model.Ok;
import run.ice.zero.common.model.Request;
import run.ice.zero.common.model.Response;

/**
 * @author DaoDao
 */
@Slf4j
@Component
public class BaseHelper {

    @Resource
    private CaptchaApi captchaApi;

    @Resource
    private FileApi fileApi;

    @Resource
    private FileHelper fileHelper;

    public void captchaCheck(CaptchaParam param) {
        Request<CaptchaParam> request = new Request<>(param);
        Response<Ok> response;
        try {
            response = captchaApi.captchaCheck(request);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new AppException(AuthError.INVOKE_BASE_ERROR, e.getMessage());
        }
        if (!response.isOk()) {
            throw new AppException(response);
        }
    }

    public FileData fileInfo(FileParam param) {
        String code = fileHelper.code(param.getId());
        param.setCode(code);
        Request<FileParam> request = new Request<>(param);
        Response<FileData> response;
        try {
            response = fileApi.fileInfo(request);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new AppException(AuthError.INVOKE_BASE_ERROR, e.getMessage());
        }
        if (!response.isOk()) {
            throw new AppException(response);
        }
        return response.getData();
    }

}
