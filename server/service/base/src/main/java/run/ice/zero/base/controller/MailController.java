package run.ice.zero.base.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import run.ice.zero.api.base.api.MailApi;
import run.ice.zero.api.base.model.mail.MailParam;
import run.ice.zero.base.service.MailService;
import run.ice.zero.common.model.Ok;
import run.ice.zero.common.model.Request;
import run.ice.zero.common.model.Response;

/**
 * @author DaoDao
 */
@RestController
public class MailController implements MailApi {

    @Resource
    private MailService mailService;

    @Override
    public Response<Ok> mailSend(@RequestBody @Valid Request<MailParam> request) {
        mailService.mailSend(request.getParam());
        return Response.ok();
    }

}
