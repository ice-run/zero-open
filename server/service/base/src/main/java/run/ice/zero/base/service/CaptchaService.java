package run.ice.zero.base.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import run.ice.zero.api.base.error.BaseError;
import run.ice.zero.api.base.model.captcha.CaptchaData;
import run.ice.zero.api.base.model.captcha.CaptchaParam;
import run.ice.zero.base.config.AppConfig;
import run.ice.zero.base.constant.CacheConstant;
import run.ice.zero.base.util.CaptchaUtil;
import run.ice.zero.common.error.AppException;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;

/**
 * @author DaoDao
 */
@Slf4j
@Service
public class CaptchaService {

    @Resource
    private AppConfig appConfig;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public CaptchaData captchaCode() {
        String id = UUID.randomUUID().toString();
        String code;
        String image;

        code = CaptchaUtil.getRandomCode();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(CaptchaUtil.genCaptcha(code), "jpg", outputStream);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new AppException(BaseError.GENERATE_CAPTCHA_ERROR, e);
        }
        image = "data:image/jpeg;base64," + new String(Base64.getEncoder().encode(outputStream.toByteArray())).replaceAll("[\\s*\t\n\r]", "");

        if (code.isEmpty()) {
            throw new AppException(BaseError.GENERATE_CAPTCHA_ERROR);
        }
        // 将验证码与验证码对应的id存入到redis中，用来验证
        String key = CacheConstant.CAPTCHA + id;
        stringRedisTemplate.opsForValue().set(key, code, Duration.ofMinutes(3L));
        CaptchaData data = new CaptchaData();
        data.setId(id);
        data.setImage(image);
        return data;
    }

    public void captchaCheck(CaptchaParam param) {

        String id = param.getId();
        String code = param.getCode();
        /*
         * 开发和测试环境为了方便自动化测试，动态配置了验证码万能密钥方案
         */
        if (appConfig.getCaptchaMasterKeyEnabled() && appConfig.getCaptchaMasterKeyValue().equals(code)) {
            return;
        }
        if (id.isEmpty() || code.isEmpty()) {
            throw new AppException(BaseError.CAPTCHA_NOT_NULL, param.toJson());
        }
        String key = CacheConstant.CAPTCHA + id;
        String value = stringRedisTemplate.opsForValue().get(key);
        if (null == value) {
            throw new AppException(BaseError.CAPTCHA_EXPIRE, param.toJson());
        }
        if (!value.equalsIgnoreCase(code)) {
            stringRedisTemplate.delete(key);
            throw new AppException(BaseError.CAPTCHA_ERROR, param.toJson());
        }
        stringRedisTemplate.delete(key);

    }

}
