package run.ice.zero.auth.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import run.ice.zero.api.auth.error.AuthError;
import run.ice.zero.api.auth.model.permission.PermissionData;
import run.ice.zero.api.auth.model.rbac.RolePermissionData;
import run.ice.zero.api.auth.model.rbac.RolePermissionUpsert;
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
    private PermissionRepository permissionRepository;

    @Resource
    private UserRoleRepository userRoleRepository;

    @Resource
    private RolePermissionRepository rolePermissionRepository;

    @Resource
    private AuthHelper authHelper;

    public RolePermissionData rolePermission(String authorization) {
        String username = authHelper.username(authorization.replace("Bearer ", ""));
        Optional<User> optional = userRepository.findByUsername(username);
        if (optional.isEmpty()) {
            throw new AppException(AuthError.USER_NOT_EXIST, username);
        }
        User user = optional.get();
        Long userId = user.getId();
        List<Role> roleList = roleRepository.findAllByUserId(userId);
        List<Permission> permissionList = permissionRepository.findAllByUserId(userId);

        List<RoleData> roleDataList = new ArrayList<>();
        for (Role role : roleList) {
            RoleData roleData = new RoleData();
            BeanUtils.copyProperties(role, roleData);
            roleDataList.add(roleData);
        }

        List<PermissionData> permissionDataList = new ArrayList<>();
        for (Permission permission : permissionList) {
            PermissionData permissionData = new PermissionData();
            BeanUtils.copyProperties(permission, permissionData);
            permissionDataList.add(permissionData);
        }

        RolePermissionData data = new RolePermissionData();
        data.setRoleDataList(roleDataList);
        data.setPermissionDataList(permissionDataList);

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

    public List<PermissionData> rolePermissionList(IdParam param) {
        Long roleId = param.getId();
        List<Permission> permissionList = permissionRepository.findAllByRoleId(roleId);
        List<PermissionData> permissionDataList = new ArrayList<>();
        for (Permission permission : permissionList) {
            PermissionData permissionData = new PermissionData();
            BeanUtils.copyProperties(permission, permissionData);
            permissionDataList.add(permissionData);
        }
        return permissionDataList;
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

    public void rolePermissionUpsert(RolePermissionUpsert param) {
        Long roleId = param.getRoleId();
        Long permissionId = param.getPermissionId();
        Boolean valid = param.getValid();
        RolePermission entity = new RolePermission();
        entity.setRoleId(roleId);
        entity.setPermissionId(permissionId);
        Optional<RolePermission> optional = rolePermissionRepository.findOne(Example.of(entity));
        if (optional.isPresent()) {
            entity = optional.get();
            entity.setUpdateTime(LocalDateTime.now());
        }
        entity.setValid(valid);
        rolePermissionRepository.saveAndFlush(entity);
    }

}
