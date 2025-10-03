package run.ice.zero.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import run.ice.zero.auth.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * @author DaoDao
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    /**
     * 查询 User
     *
     * @param username String
     * @return User
     */
    Optional<User> findByUsername(String username);

    /**
     * 查询角色的用户
     *
     * @param roleId Long
     * @return List
     */
    @Query("""
            select u from User u
            left join UserRole as ur on u.id = ur.userId
            left join Role as r on ur.roleId = r.id
            where ur.valid = true
            and u.valid = true
            and r.id = :roleId
            """)
    List<User> findAllByRoleId(@Param("roleId") Long roleId);

}
