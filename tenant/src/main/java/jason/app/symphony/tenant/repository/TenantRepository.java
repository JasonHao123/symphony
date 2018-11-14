package jason.app.symphony.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jason.app.symphony.tenant.entity.Tenant;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
}
