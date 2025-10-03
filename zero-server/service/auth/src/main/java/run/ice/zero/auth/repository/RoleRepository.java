package run.ice.zero.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import run.ice.zero.auth.entity.Role;

import java.util.List;

/**
 * @author DaoDao
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {

    /**
     * 查询用户的角色
     *
     * @param userId Long
     * @return List
     */
    @Query("""
            select r from User u
            left join UserRole as ur on u.id = ur.userId
            left join Role as r on ur.roleId = r.id
            where u.id = :userId
            and ur.valid = true
            and r.valid = true
            """)
    List<Role> findAllByUserId(@Param("userId") Long userId);

    /**
     * 查询权限的角色
     *
     * @param permissionId Long
     * @return List
     */
    @Query("""
            select r from Role r
            left join RolePermission as rp on r.id = rp.roleId
            left join Permission as p on rp.permissionId = p.id
            where rp.valid = true
            and r.valid = true
            and p.id = :permissionId
            """)
    List<Role> findAllByPermissionId(@Param("permissionId") Long permissionId);

}
