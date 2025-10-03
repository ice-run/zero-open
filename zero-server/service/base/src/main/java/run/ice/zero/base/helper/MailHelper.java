package run.ice.zero.base.helper;

import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import run.ice.zero.api.base.model.mail.MailParam;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * @author DaoDao
 */
@Slf4j
@Component
public class MailHelper {

    @Value("${spring.mail.username:daodao@ice.run}")
    private String from;

    @Resource
    private JavaMailSender javaMailSender;

    @Async
    public void sendMail(MailParam mailParam) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        String[] to = mailParam.getTo();
        String[] bcc = mailParam.getBcc();
        String[] cc = mailParam.getCc();
        String replyTo = mailParam.getReplyTo();
        String subject = mailParam.getSubject();
        String text = mailParam.getText();
        Integer priority = mailParam.getPriority();
        LocalDateTime sentDate = mailParam.getSentDate();
        try {
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setTo(to);
            if (null != bcc) {
                mimeMessageHelper.setBcc(bcc);
            }
            if (null != cc) {
                mimeMessageHelper.setCc(cc);
            }
            if (null != replyTo && !replyTo.isEmpty()) {
                mimeMessageHelper.setReplyTo(replyTo);
            }
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(text);
            if (null != priority) {
                mimeMessageHelper.setPriority(priority);
            }
            if (null != sentDate) {
                mimeMessageHelper.setSentDate(Date.from(sentDate.atZone(ZoneOffset.systemDefault()).toInstant()));
            }

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

    }

}
