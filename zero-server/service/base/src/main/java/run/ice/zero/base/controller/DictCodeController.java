package run.ice.zero.base.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RestController;
import run.ice.zero.api.base.api.DictCodeApi;
import run.ice.zero.api.base.model.code.DictCodeData;
import run.ice.zero.api.base.model.code.DictCodeSearch;
import run.ice.zero.api.base.model.code.DictCodeUpsert;
import run.ice.zero.base.service.DictCodeService;
import run.ice.zero.common.model.*;

/**
 * @author DaoDao
 */
@RestController
public class DictCodeController implements DictCodeApi {

    @Resource
    private DictCodeService dictCodeService;

    @Override
    public Response<DictCodeData> dictCodeSelect(Request<IdParam> request) {
        DictCodeData data = dictCodeService.dictCodeSelect(request.getParam());
        return new Response<>(data);
    }

    @Override
    public Response<DictCodeData> dictCodeUpsert(Request<DictCodeUpsert> request) {
        DictCodeData data = dictCodeService.dictCodeUpsert(request.getParam());
        return new Response<>(data);
    }

    @Override
    public Response<PageData<DictCodeData>> dictCodeSearch(Request<PageParam<DictCodeSearch>> request) {
        PageData<DictCodeData> data = dictCodeService.dictCodeSearch(request.getParam());
        return new Response<>(data);
    }

}
