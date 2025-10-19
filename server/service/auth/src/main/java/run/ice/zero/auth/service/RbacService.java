package run.ice.zero.auth.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import run.ice.zero.api.auth.error.AuthError;
import run.ice.zero.api.auth.model.perm.PermData;
import run.ice.zero.api.auth.model.rbac.RolePermData;
import run.ice.zero.api.auth.model.rbac.RolePermUpsert;
import run.ice.zero.api.auth.model.rbac.UserRoleUpsert;
import run.ice.zero.api.auth.model.role.RoleData;
import run.ice.zero.auth.entity.*;
import run.ice.zero.auth.repository.*;
import run.ice.zero.common.error.AppException;
import run.ice.zero.common.model.IdParam;
import run.ice.zero.auth.helper.AuthHelper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class RbacService {

    @Resource
    private UserRepository userRepository;

    @Resource
    private RoleRepository roleRepository;

    @Resource
    private PermRepository permRepository;

    @Resource
    private UserRoleRepository userRoleRepository;

    @Resource
    private RolePermRepository rolePermRepository;

    @Resource
    private AuthHelper authHelper;

    public RolePermData rolePerm(String authorization) {
        String username = authHelper.username(authorization.replace("Bearer ", ""));
        Optional<User> optional = userRepository.findByUsername(username);
        if (optional.isEmpty()) {
            throw new AppException(AuthError.USER_NOT_EXIST, username);
        }
        User user = optional.get();
        Long userId = user.getId();
        List<Role> roleList = roleRepository.findAllByUserId(userId);
        List<Perm> permList = permRepository.findAllByUserId(userId);

        List<RoleData> roleDataList = new ArrayList<>();
        for (Role role : roleList) {
            RoleData roleData = new RoleData();
            BeanUtils.copyProperties(role, roleData);
            roleDataList.add(roleData);
        }

        List<PermData> permDataList = new ArrayList<>();
        for (Perm perm : permList) {
            PermData permData = new PermData();
            BeanUtils.copyProperties(perm, permData);
            permDataList.add(permData);
        }

        RolePermData data = new RolePermData();
        data.setRoleDataList(roleDataList);
        data.setPermDataList(permDataList);

        return data;
    }

    public List<RoleData> userRoleList(IdParam param) {
        Long userId = param.getId();
        List<Role> roleList = roleRepository.findAllByUserId(userId);
        List<RoleData> roleDataList = new ArrayList<>();
        for (Role role : roleList) {
            RoleData roleData = new RoleData();
            BeanUtils.copyProperties(role, roleData);
            roleDataList.add(roleData);
        }
        return roleDataList;
    }

    public List<PermData> rolePermList(IdParam param) {
        Long roleId = param.getId();
        List<Perm> permList = permRepository.findAllByRoleId(roleId);
        List<PermData> permDataList = new ArrayList<>();
        for (Perm perm : permList) {
            PermData permData = new PermData();
            BeanUtils.copyProperties(perm, permData);
            permDataList.add(permData);
        }
        return permDataList;
    }

    public void userRoleUpsert(UserRoleUpsert param) {
        Long userId = param.getUserId();
        Long roleId = param.getRoleId();
        Boolean valid = param.getValid();
        UserRole entity = new UserRole();
        entity.setUserId(userId);
        entity.setRoleId(roleId);
        Optional<UserRole> optional = userRoleRepository.findOne(Example.of(entity));
        if (optional.isPresent()) {
            entity = optional.get();
            entity.setUpdateTime(LocalDateTime.now());
        }
        entity.setValid(valid);
        userRoleRepository.saveAndFlush(entity);
    }

    public void rolePermUpsert(RolePermUpsert param) {
        Long roleId = param.getRoleId();
        Long permId = param.getPermId();
        Boolean valid = param.getValid();
        RolePerm entity = new RolePerm();
        entity.setRoleId(roleId);
        entity.setPermId(permId);
        Optional<RolePerm> optional = rolePermRepository.findOne(Example.of(entity));
        if (optional.isPresent()) {
            entity = optional.get();
            entity.setUpdateTime(LocalDateTime.now());
        }
        entity.setValid(valid);
        rolePermRepository.saveAndFlush(entity);
    }

}
