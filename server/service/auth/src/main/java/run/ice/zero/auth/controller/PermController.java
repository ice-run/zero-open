package run.ice.zero.auth.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RestController;
import run.ice.zero.api.auth.api.PermApi;
import run.ice.zero.api.auth.model.perm.PermData;
import run.ice.zero.api.auth.model.perm.PermSearch;
import run.ice.zero.api.auth.model.perm.PermUpsert;
import run.ice.zero.common.model.*;
import run.ice.zero.auth.service.PermService;

/**
 * @author DaoDao
 */
@RestController
public class PermController implements PermApi {

    @Resource
    private PermService permService;

    @Override
    public Response<PermData> permSelect(Request<IdParam> request) {
        PermData data = permService.permSelect(request.getParam());
        return new Response<>(data);
    }

    @Override
    public Response<PermData> permUpsert(Request<PermUpsert> request) {
        PermData data = permService.permUpsert(request.getParam());
        return new Response<>(data);
    }

    @Override
    public Response<PageData<PermData>> permSearch(Request<PageParam<PermSearch>> request) {
        PageData<PermData> data = permService.permSearch(request.getParam());
        return new Response<>(data);
    }

}
