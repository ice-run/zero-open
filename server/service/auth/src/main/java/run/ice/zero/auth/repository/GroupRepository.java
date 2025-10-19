package run.ice.zero.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import run.ice.zero.auth.entity.Group;

@Repository
public interface GroupRepository  extends JpaRepository<Group, Long>, JpaSpecificationExecutor<Group> {

}
