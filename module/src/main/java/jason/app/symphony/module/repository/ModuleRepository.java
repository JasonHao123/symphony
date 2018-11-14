package jason.app.symphony.module.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jason.app.symphony.module.entity.Module;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
}
