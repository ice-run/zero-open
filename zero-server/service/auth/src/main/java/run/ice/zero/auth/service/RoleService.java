package run.ice.zero.auth.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import run.ice.zero.api.auth.error.AuthError;
import run.ice.zero.api.auth.model.role.RoleData;
import run.ice.zero.api.auth.model.role.RoleSearch;
import run.ice.zero.api.auth.model.role.RoleUpsert;
import run.ice.zero.common.error.AppException;
import run.ice.zero.common.model.IdParam;
import run.ice.zero.common.model.PageData;
import run.ice.zero.common.model.PageParam;
import run.ice.zero.common.util.bean.BeanUtil;
import run.ice.zero.auth.constant.CacheConstant;
import run.ice.zero.auth.entity.Role;
import run.ice.zero.auth.repository.RoleRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class RoleService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RoleRepository roleRepository;

    public RoleData roleSelect(IdParam param) {
        Long id = param.getId();
        Role model = role(id);
        RoleData data = new RoleData();
        BeanUtils.copyProperties(model, data);
        return data;
    }

    private Role role(Long id) {
        String key = CacheConstant.ROLE + id;
        String json = stringRedisTemplate.opsForValue().get(key);
        if (null != json) {
            return new Role().ofJson(json);
        }
        Optional<Role> optional = roleRepository.findById(id);
        if (optional.isEmpty()) {
            throw new AppException(AuthError.ROLE_NOT_EXIST, String.valueOf(id));
        }
        Role model = optional.get();
        stringRedisTemplate.opsForValue().set(key, model.toJson(), Duration.ofHours(1L));
        return model;
    }

    public RoleData roleUpsert(RoleUpsert param) {
        Long id = param.getId();
        String code = param.getCode();
        String name = param.getName();
        Role entity;
        if (null == id) {
            entity = new Role();
            entity.setCode(code);
            boolean b = roleRepository.exists(Example.of(entity));
            if (b) {
                throw new AppException(AuthError.ROLE_CODE_ALREADY_EXIST, code);
            }
            entity = new Role();
            entity.setName(name);
            b = roleRepository.exists(Example.of(entity));
            if (b) {
                throw new AppException(AuthError.ROLE_NAME_ALREADY_EXIST, name);
            }
            entity = new Role();
        } else {
            Optional<Role> optional = roleRepository.findById(id);
            if (optional.isEmpty()) {
                throw new AppException(AuthError.ROLE_NOT_EXIST, String.valueOf(id));
            }
            entity = optional.get();
            if (null != code && !code.isEmpty()) {
                Role role = new Role();
                role.setCode(code);
                Optional<Role> o = roleRepository.findOne(Example.of(role));
                if (o.isPresent() && !o.get().getId().equals(id)) {
                    throw new AppException(AuthError.ROLE_CODE_ALREADY_EXIST, code);
                }
            }
            if (null != name && !name.isEmpty()) {
                Role role = new Role();
                role.setName(name);
                Optional<Role> o = roleRepository.findOne(Example.of(role));
                if (o.isPresent() && !o.get().getId().equals(id)) {
                    throw new AppException(AuthError.ROLE_NAME_ALREADY_EXIST, name);
                }
            }
            entity.setUpdateTime(LocalDateTime.now());
        }
        BeanUtils.copyProperties(param, entity, BeanUtil.nullProperties(param));
        entity = roleRepository.saveAndFlush(entity);

        String key = CacheConstant.ROLE + id;
        stringRedisTemplate.delete(key);

        RoleData data = new RoleData();
        BeanUtils.copyProperties(entity, data);
        return data;
    }

    public PageData<RoleData> roleSearch(PageParam<RoleSearch> pageParam) {
        Integer page = pageParam.getPage();
        Integer size = pageParam.getSize();
        Role model = new Role();
        RoleSearch param = pageParam.getParam();
        BeanUtils.copyProperties(param, model);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<Role> example = Example.of(model, matcher);
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Role> dataPage = roleRepository.findAll(example, pageable);
        Long total = dataPage.getTotalElements();
        List<Role> dataList = dataPage.getContent();
        List<RoleData> list = dataList.stream().map(source -> {
            RoleData target = new RoleData();
            BeanUtils.copyProperties(source, target);
            return target;
        }).toList();
        return new PageData<>(page, size, total, list);
    }

}
