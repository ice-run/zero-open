package run.ice.zero.base.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import run.ice.zero.base.entity.FileInfo;

/**
 * @author DaoDao
 */
@Repository
public interface FileInfoRepository extends JpaRepository<FileInfo, String>, JpaSpecificationExecutor<FileInfo> {

}
