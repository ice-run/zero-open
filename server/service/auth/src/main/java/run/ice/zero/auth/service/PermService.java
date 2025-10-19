package run.ice.zero.auth.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import run.ice.zero.api.auth.error.AuthError;
import run.ice.zero.api.auth.model.perm.PermData;
import run.ice.zero.api.auth.model.perm.PermSearch;
import run.ice.zero.api.auth.model.perm.PermUpsert;
import run.ice.zero.common.error.AppException;
import run.ice.zero.common.model.IdParam;
import run.ice.zero.common.model.PageData;
import run.ice.zero.common.model.PageParam;
import run.ice.zero.common.util.bean.BeanUtil;
import run.ice.zero.auth.constant.CacheConstant;
import run.ice.zero.auth.entity.Perm;
import run.ice.zero.auth.repository.PermRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class PermService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private PermRepository permRepository;

    public PermData permSelect(IdParam param) {
        Long id = param.getId();
        Perm model = perm(id);
        PermData data = new PermData();
        BeanUtils.copyProperties(model, data);
        return data;
    }

    private Perm perm(Long id) {
        String key = CacheConstant.PERM + id;
        String json = stringRedisTemplate.opsForValue().get(key);
        if (null != json) {
            return new Perm().ofJson(json);
        }
        Optional<Perm> optional = permRepository.findById(id);
        if (optional.isEmpty()) {
            throw new AppException(AuthError.PERM_NOT_EXIST, String.valueOf(id));
        }
        Perm perm = optional.get();
        stringRedisTemplate.opsForValue().set(key, perm.toJson(), Duration.ofHours(1L));
        return perm;
    }

    public PermData permUpsert(PermUpsert param) {
        Long id = param.getId();
        String code = param.getCode();
        String name = param.getName();
        Perm entity;
        if (null == id) {
            entity = new Perm();
            entity.setCode(code);
            boolean b = permRepository.exists(Example.of(entity));
            if (b) {
                throw new AppException(AuthError.PERM_CODE_ALREADY_EXIST, code);
            }
            entity = new Perm();
            entity.setName(name);
            b = permRepository.exists(Example.of(entity));
            if (b) {
                throw new AppException(AuthError.PERM_NAME_ALREADY_EXIST, name);
            }
            entity = new Perm();
        } else {
            Optional<Perm> optional = permRepository.findById(id);
            if (optional.isEmpty()) {
                throw new AppException(AuthError.PERM_NOT_EXIST, String.valueOf(id));
            }
            entity = optional.get();
            if (null != code && !code.isEmpty()) {
                Perm perm = new Perm();
                perm.setCode(code);
                Optional<Perm> o = permRepository.findOne(Example.of(perm));
                if (o.isPresent() && !o.get().getId().equals(id)) {
                    throw new AppException(AuthError.PERM_CODE_ALREADY_EXIST, code);
                }
            }
            if (null != name && !name.isEmpty()) {
                Perm perm = new Perm();
                perm.setName(name);
                Optional<Perm> o = permRepository.findOne(Example.of(perm));
                if (o.isPresent() && !o.get().getId().equals(id)) {
                    throw new AppException(AuthError.PERM_NAME_ALREADY_EXIST, name);
                }
            }
            entity.setUpdateTime(LocalDateTime.now());
        }
        BeanUtils.copyProperties(param, entity, BeanUtil.nullProperties(param));
        entity = permRepository.saveAndFlush(entity);

        String key = CacheConstant.PERM + id;
        stringRedisTemplate.delete(key);

        PermData data = new PermData();
        BeanUtils.copyProperties(entity, data);
        return data;
    }

    public PageData<PermData> permSearch(PageParam<PermSearch> pageParam) {
        Integer page = pageParam.getPage();
        Integer size = pageParam.getSize();
        Perm model = new Perm();
        PermSearch param = pageParam.getParam();
        BeanUtils.copyProperties(param, model);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<Perm> example = Example.of(model, matcher);
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Perm> dataPage = permRepository.findAll(example, pageable);
        Long total = dataPage.getTotalElements();
        List<Perm> dataList = dataPage.getContent();
        List<PermData> list = dataList.stream().map(source -> {
            PermData target = new PermData();
            BeanUtils.copyProperties(source, target);
            return target;
        }).toList();
        return new PageData<>(page, size, total, list);
    }

}
