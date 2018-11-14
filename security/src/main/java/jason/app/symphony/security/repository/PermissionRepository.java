package jason.app.symphony.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jason.app.symphony.security.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
}
