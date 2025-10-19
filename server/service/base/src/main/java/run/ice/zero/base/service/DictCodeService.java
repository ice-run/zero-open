package run.ice.zero.base.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import run.ice.zero.api.base.error.BaseError;
import run.ice.zero.api.base.model.code.DictCodeData;
import run.ice.zero.api.base.model.code.DictCodeSearch;
import run.ice.zero.api.base.model.code.DictCodeUpsert;
import run.ice.zero.base.constant.CacheConstant;
import run.ice.zero.base.entity.DictCode;
import run.ice.zero.base.repository.DictCodeRepository;
import run.ice.zero.common.error.AppException;
import run.ice.zero.common.helper.DataHelper;
import run.ice.zero.common.model.IdParam;
import run.ice.zero.common.model.PageData;
import run.ice.zero.common.model.PageParam;
import run.ice.zero.common.util.bean.BeanUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 数据字典服务
 *
 * @author DaoDao
 */
@Slf4j
@Service
@Transactional
public class DictCodeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private DictCodeRepository dictCodeRepository;

    @Resource
    private DataHelper dataHelper;

    /**
     * 数据字典分页查询
     *
     * @param pageParam 分页查询参数
     * @return 分页数据
     */
    public PageData<DictCodeData> dictCodeSearch(PageParam<DictCodeSearch> pageParam) {
        Integer page = pageParam.getPage();
        Integer size = pageParam.getSize();
        DictCodeSearch param = pageParam.getParam();
        List<PageParam.Match> matches = pageParam.getMatches();
        List<PageParam.Order> orders = pageParam.getOrders();
        ExampleMatcher matcher = dataHelper.matcher(matches);
        Sort sort = dataHelper.sort(orders);
        DictCode entity = new DictCode();
        BeanUtils.copyProperties(param, entity);
        Example<DictCode> example = Example.of(entity, matcher);
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<DictCode> dataPage = dictCodeRepository.findAll(example, pageable);
        Long total = dataPage.getTotalElements();
        List<DictCodeData> list = dataPage.getContent().stream().map(data -> {
            DictCodeData codeData = new DictCodeData();
            BeanUtils.copyProperties(data, codeData);
            return codeData;
        }).collect(Collectors.toList());
        return new PageData<>(page, size, total, list);
    }

    /**
     * 数据字典查询
     *
     * @param param 查询参数
     * @return 数据字典
     */
    public DictCodeData dictCodeSelect(IdParam param) {
        Long id = param.getId();
        String key = CacheConstant.DICT_CODE + id;
        String json = stringRedisTemplate.opsForValue().get(key);
        if (null == json || json.isEmpty()) {
            Optional<DictCode> optional = dictCodeRepository.findById(id);
            if (optional.isEmpty()) {
                throw new AppException(BaseError.DICT_CODE_NOT_EXIST, String.valueOf(id));
            }
            json = optional.get().toJson();
            stringRedisTemplate.opsForValue().set(key, json, Duration.ofMinutes(10L));
        }
        DictCode entity = new DictCode().ofJson(json);
        DictCodeData data = new DictCodeData();
        BeanUtils.copyProperties(entity, data);
        return data;
    }

    /**
     * 数据字典新增或更新
     *
     * @param param 新增或更新参数
     * @return 数据字典
     */
    public DictCodeData dictCodeUpsert(DictCodeUpsert param) {
        Long id = param.getId();
        String code = param.getCode();
        String key = param.getKey();
        DictCode entity;
        if (null == id) {
            entity = new DictCode();
            entity.setCode(code);
            entity.setKey(key);
            boolean b = dictCodeRepository.exists(Example.of(entity));
            if (b) {
                throw new AppException(BaseError.DICT_CODE_ALREADY_EXIST, code + ":" + key);
            }
        } else {
            Optional<DictCode> optional = dictCodeRepository.findById(id);
            if (optional.isEmpty()) {
                throw new AppException(BaseError.DICT_CODE_NOT_EXIST, String.valueOf(id));
            }
            entity = optional.get();
            DictCode model = new DictCode();
            model.setCode(code);
            model.setKey(key);
            Optional<DictCode> o = dictCodeRepository.findOne(Example.of(model));
            if (o.isPresent() && !o.get().getId().equals(id)) {
                throw new AppException(BaseError.DICT_CODE_ALREADY_EXIST, code + ":" + key);
            }
            entity.setUpdateTime(LocalDateTime.now());
        }
        BeanUtils.copyProperties(param, entity, BeanUtil.nullProperties(param));
        entity = dictCodeRepository.saveAndFlush(entity);

        id = entity.getId();
        stringRedisTemplate.delete(CacheConstant.DICT_CODE + id);

        DictCodeData data = new DictCodeData();
        BeanUtils.copyProperties(entity, data);
        return data;
    }

}
