package jason.app.symphony.module.config;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.sql.DataSource;
import javax.sql.XADataSource;

import org.hsqldb.jdbc.pool.JDBCXADataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import com.atomikos.jdbc.AtomikosDataSourceBean;

import jason.app.symphony.module.service.ModuleService;
import jason.app.symphony.module.service.impl.ModuleServiceImpl;

@Configuration
@DependsOn("transactionManager")
@EnableJpaRepositories(entityManagerFactoryRef = "moduleEntityManager", transactionManagerRef = "transactionManager", basePackages = "jason.app.symphony.module.repository")
@EnableConfigurationProperties(ModuleDatasourceProperties.class)
public class ModuleComponentConfig {

	@Bean
	public ModuleService moduleService() {
		return new ModuleServiceImpl();
	}

	@Autowired
	private ModuleDatasourceProperties moduleDatasourceProperties;

	@Bean(name = "moduleDataSource", initMethod = "init", destroyMethod = "close")
	public DataSource moduleDataSource() throws SQLException {
		XADataSource dataSource = null;
		if(moduleDatasourceProperties==null || moduleDatasourceProperties.getType()==null) {
			throw new InvalidParameterException("spring.module.datasource.type must be provided!");
		}else {
			switch(moduleDatasourceProperties.getType()) {
			case "mysql":
				dataSource = createMySQLDataSource(moduleDatasourceProperties);
				break;
			case "derby":
				dataSource = createDerbyDataSource(moduleDatasourceProperties);
				break;
			case "hsqldb":
				dataSource = createHsqlDBDataSource(moduleDatasourceProperties);
				break;
			}
		}

		 AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
		 xaDataSource.setXaDataSource(dataSource);
		 xaDataSource.setUniqueResourceName(moduleDatasourceProperties.getResourceName());
		 return xaDataSource;
	}
	

	private XADataSource createHsqlDBDataSource(ModuleDatasourceProperties props) throws SQLException {
		// TODO Auto-generated method stub
		JDBCXADataSource dataSource = new JDBCXADataSource();
		dataSource.setUrl(props.getUrl());
		dataSource.setUser(props.getUsername());
		dataSource.setPassword(props.getPassword());
		
		return dataSource;
	}


	private XADataSource createDerbyDataSource(ModuleDatasourceProperties props) {
		
		return null;
	}


	private XADataSource createMySQLDataSource(ModuleDatasourceProperties props) {
		return null;
	}


	/**
	 * Entity manager definition.
	 * 
	 * @param builder
	 *            an EntityManagerFactoryBuilder.
	 * @return LocalContainerEntityManagerFactoryBean.
	 */
	@Bean(name = "moduleEntityManager")
	public LocalContainerEntityManagerFactoryBean moduleEntityManagerFactory(EntityManagerFactoryBuilder builder,@Qualifier("moduleDataSource") DataSource moduleDataSource) {
		return builder.dataSource(moduleDataSource).properties(hibernateProperties())
				.packages("jason.app.symphony.module.entity").persistenceUnit(moduleDatasourceProperties.getPersistenceUnitName()).build();
	}

	private Map<String, Object> hibernateProperties() {

		Resource resource = new ClassPathResource(moduleDatasourceProperties.getHibernateProperties());
		try {
			Properties properties = PropertiesLoaderUtils.loadProperties(resource);
			return properties.entrySet().stream()
					.collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue()));
		} catch (IOException e) {
			return new HashMap<String, Object>();
		}
	}

}
