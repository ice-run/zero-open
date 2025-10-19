package run.ice.zero.auth.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import run.ice.zero.api.auth.error.AuthError;
import run.ice.zero.api.auth.model.group.GroupData;
import run.ice.zero.api.auth.model.group.GroupSearch;
import run.ice.zero.api.auth.model.group.GroupUpsert;
import run.ice.zero.auth.constant.CacheConstant;
import run.ice.zero.auth.entity.Group;
import run.ice.zero.auth.repository.GroupRepository;
import run.ice.zero.auth.repository.UserRepository;
import run.ice.zero.common.error.AppException;
import run.ice.zero.common.model.IdParam;
import run.ice.zero.common.model.PageData;
import run.ice.zero.common.model.PageParam;
import run.ice.zero.common.util.bean.BeanUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class GroupService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private GroupRepository groupRepository;

    @Resource
    private UserRepository userRepository;

    public GroupData groupSelect(IdParam param) {
        Long id = param.getId();
        Group model = group(id);
        GroupData data = new GroupData();
        BeanUtils.copyProperties(model, data);
        return data;
    }

    private Group group(Long id) {
        String key = CacheConstant.GROUP + id;
        String json = stringRedisTemplate.opsForValue().get(key);
        if (null != json) {
            return new Group().ofJson(json);
        }
        Optional<Group> optional = groupRepository.findById(id);
        if (optional.isEmpty()) {
            throw new AppException(AuthError.GROUP_NOT_EXIST, String.valueOf(id));
        }
        Group model = optional.get();
        stringRedisTemplate.opsForValue().set(key, model.toJson(), Duration.ofHours(1L));
        return model;
    }

    public GroupData groupUpsert(GroupUpsert param) {
        Long id = param.getId();
        Long parentId = param.getParentId();
        if (null != parentId) {
            groupRepository.findById(parentId).orElseThrow(() -> new AppException(AuthError.GROUP_NOT_EXIST, String.valueOf(parentId)));
        }
        Long adminId = param.getAdminId();
        if (null != adminId) {
            userRepository.findById(adminId).orElseThrow(() -> new AppException(AuthError.USER_ID_NOT_EXIST, String.valueOf(adminId)));
        }
        String name = param.getName();
        Group entity;
        if (null == id) {
            entity = new Group();
            entity.setName(name);
            boolean b = groupRepository.exists(Example.of(entity));
            if (b) {
                throw new AppException(AuthError.GROUP_NAME_ALREADY_EXIST, name);
            }
            entity = new Group();
        } else {
            Optional<Group> optional = groupRepository.findById(id);
            if (optional.isEmpty()) {
                throw new AppException(AuthError.GROUP_NOT_EXIST, String.valueOf(id));
            }
            entity = optional.get();
            if (null != name && !name.isEmpty()) {
                Group group = new Group();
                group.setName(name);
                Optional<Group> o = groupRepository.findOne(Example.of(group));
                if (o.isPresent() && !o.get().getId().equals(id)) {
                    throw new AppException(AuthError.GROUP_NAME_ALREADY_EXIST, name);
                }
            }
            entity.setUpdateTime(LocalDateTime.now());
        }
        BeanUtils.copyProperties(param, entity, BeanUtil.nullProperties(param));
        entity = groupRepository.saveAndFlush(entity);

        String key = CacheConstant.GROUP + id;
        stringRedisTemplate.delete(key);

        GroupData data = new GroupData();
        BeanUtils.copyProperties(entity, data);
        return data;
    }

    public PageData<GroupData> groupSearch(PageParam<GroupSearch> pageParam) {
        Integer page = pageParam.getPage();
        Integer size = pageParam.getSize();
        Group model = new Group();
        GroupSearch param = pageParam.getParam();
        BeanUtils.copyProperties(param, model);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<Group> example = Example.of(model, matcher);
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Group> dataPage = groupRepository.findAll(example, pageable);
        Long total = dataPage.getTotalElements();
        List<Group> dataList = dataPage.getContent();
        List<GroupData> list = dataList.stream().map(source -> {
            GroupData target = new GroupData();
            BeanUtils.copyProperties(source, target);
            return target;
        }).toList();
        return new PageData<>(page, size, total, list);
    }

}
