package run.ice.zero.base.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import run.ice.zero.api.base.error.BaseError;
import run.ice.zero.api.base.model.mail.MailParam;
import run.ice.zero.base.helper.MailHelper;
import run.ice.zero.common.error.AppException;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * @author DaoDao
 */
@Slf4j
@Service
public class MailService {

    @Resource
    private MailHelper mailHelper;

    public void mailSend(MailParam mailParam) {
        String[] to = mailParam.getTo();
        Arrays.stream(to).spliterator().forEachRemaining(this::check);
        String[] bcc = mailParam.getBcc();
        if (null != bcc) {
            Arrays.stream(bcc).spliterator().forEachRemaining(this::check);
        }
        String[] cc = mailParam.getCc();
        if (null != cc) {
            Arrays.stream(cc).spliterator().forEachRemaining(this::check);
        }
        mailHelper.sendMail(mailParam);
    }

    private void check(String mail) {
        if (null == mail || mail.isEmpty()) {
            throw new AppException(BaseError.MAIL_REGULAR_ERROR, mail);
        }
        String regex = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        boolean matches = Pattern.compile(regex).matcher(mail).matches();
        if (!matches) {
            throw new AppException(BaseError.MAIL_REGULAR_ERROR, mail);
        }
    }

}
