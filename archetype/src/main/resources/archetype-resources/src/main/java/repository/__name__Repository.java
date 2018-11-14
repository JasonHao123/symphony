package ${package}.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ${package}.entity.${name};

@Repository
public interface ${name}Repository extends JpaRepository<${name}, Long> {
}
