package run.ice.zero.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import run.ice.zero.auth.entity.Perm;

import java.util.List;

/**
 * @author DaoDao
 */
@Repository
public interface PermRepository extends JpaRepository<Perm, Long>, JpaSpecificationExecutor<Perm> {

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
            left join RolePerm as rp on r.id = rp.roleId
            left join Perm as p on rp.permId = p.id
            where u.id = :userId
            and ur.valid = true
            and r.valid = true
            and rp.valid = true
            and p.valid = true
            """)
    List<Perm> findAllByUserId(@Param("userId") Long userId);

    /**
     * 查询角色的权限
     *
     * @param roleId Long
     * @return List
     */
    @Query("""
            select p from Role r
            left join RolePerm as rp on r.id = rp.roleId
            left join Perm as p on rp.permId = p.id
            where rp.valid = true
            and p.valid = true
            and r.id = :roleId
            """)
    List<Perm> findAllByRoleId(@Param("roleId") Long roleId);

}
