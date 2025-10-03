package run.ice.zero.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import run.ice.zero.auth.entity.Permission;

import java.util.List;

/**
 * @author DaoDao
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {

    /**
     * 查询用户的权限
     *
     * @param userId Long
     * @return List
     */
    @Query("""
            select p from User u
            left join UserRole as ur on u.id = ur.userId
            left join Role as r on ur.roleId = r.id
            left join RolePermission as rp on r.id = rp.roleId
            left join Permission as p on rp.permissionId = p.id
            where u.id = :userId
            and ur.valid = true
            and r.valid = true
            and rp.valid = true
            and p.valid = true
            """)
    List<Permission> findAllByUserId(@Param("userId") Long userId);

    /**
     * 查询角色的权限
     *
     * @param roleId Long
     * @return List
     */
    @Query("""
            select p from Role r
            left join RolePermission as rp on r.id = rp.roleId
            left join Permission as p on rp.permissionId = p.id
            where rp.valid = true
            and p.valid = true
            and r.id = :roleId
            """)
    List<Permission> findAllByRoleId(@Param("roleId") Long roleId);

}
