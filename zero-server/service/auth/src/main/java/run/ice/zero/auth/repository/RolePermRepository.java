package run.ice.zero.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import run.ice.zero.auth.entity.RolePerm;

@Repository
public interface RolePermRepository extends JpaRepository<RolePerm, Long>, JpaSpecificationExecutor<RolePerm> {

}
