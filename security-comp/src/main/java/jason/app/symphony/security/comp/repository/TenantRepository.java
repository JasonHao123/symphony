package jason.app.symphony.security.comp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import jason.app.symphony.security.comp.entity.Tenant;

@Repository
public interface TenantRepository extends CrudRepository<Tenant, Long> {

	Tenant findFirstByDomain(String host);

}
