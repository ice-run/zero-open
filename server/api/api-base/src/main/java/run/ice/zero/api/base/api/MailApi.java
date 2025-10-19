package run.ice.zero.api.base.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import run.ice.zero.api.base.model.mail.MailParam;
import run.ice.zero.common.constant.AppConstant;
import run.ice.zero.common.model.Ok;
import run.ice.zero.common.model.Request;
import run.ice.zero.common.model.Response;

/**
 * @author DaoDao
 */
@Tag(name = "MailApi", description = "邮件")
@HttpExchange(url = AppConstant.API)
public interface MailApi {

    @Operation(summary = "发送邮件", description = "发送邮件")
    @PostMapping(value = "mail-send")
    Response<Ok> mailSend(@RequestBody @Valid Request<MailParam> request);

}
