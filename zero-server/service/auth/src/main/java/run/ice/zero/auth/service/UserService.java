package run.ice.zero.auth.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import run.ice.zero.api.auth.error.AuthError;
import run.ice.zero.api.auth.model.user.UserData;
import run.ice.zero.api.auth.model.user.UserSearch;
import run.ice.zero.api.auth.model.user.UserUpsert;
import run.ice.zero.common.error.AppError;
import run.ice.zero.common.error.AppException;
import run.ice.zero.common.model.IdParam;
import run.ice.zero.common.model.PageData;
import run.ice.zero.common.model.PageParam;
import run.ice.zero.common.util.bean.BeanUtil;
import run.ice.zero.auth.constant.CacheConstant;
import run.ice.zero.auth.entity.User;
import run.ice.zero.auth.helper.AuthHelper;
import run.ice.zero.auth.repository.UserRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author DaoDao
 */
@Slf4j
@Service
@Transactional
public class UserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserRepository userRepository;

    @Resource
    private AuthHelper authHelper;

    @Resource
    private PasswordEncoder passwordEncoder;

    public UserData userInfo(String authorization) {
        if (null == authorization || authorization.isEmpty()) {
            throw new AppException(AppError.TOKEN_ERROR);
        }
        String username = authHelper.username(authorization.replace("Bearer ", ""));
        Optional<User> optional = userRepository.findByUsername(username);
        if (optional.isEmpty()) {
            throw new AppException(AuthError.USER_NOT_EXIST, username);
        }
        User user = optional.get();
        UserData data = new UserData();
        BeanUtils.copyProperties(user, data);
        return data;
    }

    public UserData userSelect(IdParam param) {
        Long id = param.getId();
        User model = user(id);
        UserData userData = new UserData();
        BeanUtils.copyProperties(model, userData);
        return userData;
    }

    public User user(Long id) {
        String key = CacheConstant.USER + id;
        String json = stringRedisTemplate.opsForValue().get(key);
        if (null != json) {
            return new User().ofJson(json);
        }
        Optional<User> optional = userRepository.findById(id);
        if (optional.isEmpty()) {
            throw new AppException(AuthError.USER_NOT_EXIST, String.valueOf(id));
        }
        User user = optional.get();
        stringRedisTemplate.opsForValue().set(key, user.toJson(), Duration.ofHours(1L));
        return user;
    }

    public UserData userUpsert(UserUpsert param) {
        Long id = param.getId();
        String username = param.getUsername();
        User entity;
        if (null == id) {
            if (null == username || username.isEmpty()) {
                throw new AppException(AuthError.USERNAME_NOT_NULL);
            }
            Optional<User> optional = userRepository.findByUsername(username);
            if (optional.isPresent()) {
                throw new AppException(AuthError.USERNAME_ALREADY_EXIST, username);
            }
            entity = new User();
            entity.setPassword(passwordEncoder.encode(username));
        } else {
            Optional<User> optional = userRepository.findById(id);
            if (optional.isEmpty()) {
                throw new AppException(AuthError.USER_NOT_EXIST, String.valueOf(id));
            }
            entity = optional.get();
            if (null != username && !username.isEmpty()) {
                Optional<User> o = userRepository.findByUsername(username);
                if (o.isPresent()) {
                    User user = o.get();
                    if (null != user.getId() && !user.getId().equals(id)) {
                        throw new AppException(AuthError.USERNAME_ALREADY_EXIST, username);
                    }
                }
            }
            entity.setUpdateTime(LocalDateTime.now());
        }

        BeanUtils.copyProperties(param, entity, BeanUtil.nullProperties(param));
        entity = userRepository.saveAndFlush(entity);

        String key = CacheConstant.USER + id;
        stringRedisTemplate.delete(key);

        UserData data = new UserData();
        BeanUtils.copyProperties(entity, data);
        return data;
    }

    public PageData<UserData> userSearch(PageParam<UserSearch> pageParam) {
        Integer page = pageParam.getPage();
        Integer size = pageParam.getSize();
        User model = new User();
        UserSearch param = pageParam.getParam();
        BeanUtils.copyProperties(param, model);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("username", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<User> example = Example.of(model, matcher);
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<User> dataPage = userRepository.findAll(example, pageable);
        Long total = dataPage.getTotalElements();
        List<User> dataList = dataPage.getContent();
        List<UserData> list = dataList.stream().map(source -> {
            UserData target = new UserData();
            BeanUtils.copyProperties(source, target);
            return target;
        }).toList();
        return new PageData<>(page, size, total, list);
    }

}
