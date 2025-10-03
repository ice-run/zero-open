package run.ice.zero.auth.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import run.ice.zero.api.auth.error.AuthError;
import run.ice.zero.api.auth.model.permission.PermissionData;
import run.ice.zero.api.auth.model.permission.PermissionSearch;
import run.ice.zero.api.auth.model.permission.PermissionUpsert;
import run.ice.zero.common.error.AppException;
import run.ice.zero.common.model.IdParam;
import run.ice.zero.common.model.PageData;
import run.ice.zero.common.model.PageParam;
import run.ice.zero.common.util.bean.BeanUtil;
import run.ice.zero.auth.constant.CacheConstant;
import run.ice.zero.auth.entity.Permission;
import run.ice.zero.auth.repository.PermissionRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class PermissionService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private PermissionRepository permissionRepository;

    public PermissionData permissionSelect(IdParam param) {
        Long id = param.getId();
        Permission model = permission(id);
        PermissionData data = new PermissionData();
        BeanUtils.copyProperties(model, data);
        return data;
    }

    private Permission permission(Long id) {
        String key = CacheConstant.PERMISSION + id;
        String json = stringRedisTemplate.opsForValue().get(key);
        if (null != json) {
            return new Permission().ofJson(json);
        }
        Optional<Permission> optional = permissionRepository.findById(id);
        if (optional.isEmpty()) {
            throw new AppException(AuthError.PERMISSION_NOT_EXIST, String.valueOf(id));
        }
        Permission permission = optional.get();
        stringRedisTemplate.opsForValue().set(key, permission.toJson(), Duration.ofHours(1L));
        return permission;
    }

    public PermissionData permissionUpsert(PermissionUpsert param) {
        Long id = param.getId();
        String code = param.getCode();
        String name = param.getName();
        Permission entity;
        if (null == id) {
            entity = new Permission();
            entity.setCode(code);
            boolean b = permissionRepository.exists(Example.of(entity));
            if (b) {
                throw new AppException(AuthError.PERMISSION_CODE_ALREADY_EXIST, code);
            }
            entity = new Permission();
            entity.setName(name);
            b = permissionRepository.exists(Example.of(entity));
            if (b) {
                throw new AppException(AuthError.PERMISSION_NAME_ALREADY_EXIST, name);
            }
            entity = new Permission();
        } else {
            Optional<Permission> optional = permissionRepository.findById(id);
            if (optional.isEmpty()) {
                throw new AppException(AuthError.PERMISSION_NOT_EXIST, String.valueOf(id));
            }
            entity = optional.get();
            if (null != code && !code.isEmpty()) {
                Permission permission = new Permission();
                permission.setCode(code);
                Optional<Permission> o = permissionRepository.findOne(Example.of(permission));
                if (o.isPresent() && !o.get().getId().equals(id)) {
                    throw new AppException(AuthError.PERMISSION_CODE_ALREADY_EXIST, code);
                }
            }
            if (null != name && !name.isEmpty()) {
                Permission permission = new Permission();
                permission.setName(name);
                Optional<Permission> o = permissionRepository.findOne(Example.of(permission));
                if (o.isPresent() && !o.get().getId().equals(id)) {
                    throw new AppException(AuthError.PERMISSION_NAME_ALREADY_EXIST, name);
                }
            }
            entity.setUpdateTime(LocalDateTime.now());
        }
        BeanUtils.copyProperties(param, entity, BeanUtil.nullProperties(param));
        entity = permissionRepository.saveAndFlush(entity);

        String key = CacheConstant.PERMISSION + id;
        stringRedisTemplate.delete(key);

        PermissionData data = new PermissionData();
        BeanUtils.copyProperties(entity, data);
        return data;
    }

    public PageData<PermissionData> permissionSearch(PageParam<PermissionSearch> pageParam) {
        Integer page = pageParam.getPage();
        Integer size = pageParam.getSize();
        Permission model = new Permission();
        PermissionSearch param = pageParam.getParam();
        BeanUtils.copyProperties(param, model);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<Permission> example = Example.of(model, matcher);
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Permission> dataPage = permissionRepository.findAll(example, pageable);
        Long total = dataPage.getTotalElements();
        List<Permission> dataList = dataPage.getContent();
        List<PermissionData> list = dataList.stream().map(source -> {
            PermissionData target = new PermissionData();
            BeanUtils.copyProperties(source, target);
            return target;
        }).toList();
        return new PageData<>(page, size, total, list);
    }

}
