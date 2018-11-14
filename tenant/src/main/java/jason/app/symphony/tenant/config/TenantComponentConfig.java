package jason.app.symphony.tenant.config;

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

import jason.app.symphony.tenant.service.TenantService;
import jason.app.symphony.tenant.service.impl.TenantServiceImpl;

@Configuration
@DependsOn("transactionManager")
@EnableJpaRepositories(entityManagerFactoryRef = "tenantEntityManager", transactionManagerRef = "transactionManager", basePackages = "jason.app.symphony.tenant.repository")
@EnableConfigurationProperties(TenantDatasourceProperties.class)
public class TenantComponentConfig {

	@Bean
	public TenantService tenantService() {
		return new TenantServiceImpl();
	}

	@Autowired
	private TenantDatasourceProperties tenantDatasourceProperties;

	@Bean(name = "tenantDataSource", initMethod = "init", destroyMethod = "close")
	public DataSource tenantDataSource() throws SQLException {
		XADataSource dataSource = null;
		if(tenantDatasourceProperties==null || tenantDatasourceProperties.getType()==null) {
			throw new InvalidParameterException("spring.tenant.datasource.type must be provided!");
		}else {
			switch(tenantDatasourceProperties.getType()) {
			case "mysql":
				dataSource = createMySQLDataSource(tenantDatasourceProperties);
				break;
			case "derby":
				dataSource = createDerbyDataSource(tenantDatasourceProperties);
				break;
			case "hsqldb":
				dataSource = createHsqlDBDataSource(tenantDatasourceProperties);
				break;
			}
		}

		 AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
		 xaDataSource.setXaDataSource(dataSource);
		 xaDataSource.setUniqueResourceName(tenantDatasourceProperties.getResourceName());
		 return xaDataSource;
	}
	

	private XADataSource createHsqlDBDataSource(TenantDatasourceProperties tenantDatasourceProperties2) throws SQLException {
		// TODO Auto-generated method stub
		JDBCXADataSource dataSource = new JDBCXADataSource();
		dataSource.setUrl(tenantDatasourceProperties2.getUrl());
		dataSource.setUser(tenantDatasourceProperties2.getUsername());
		dataSource.setPassword(tenantDatasourceProperties2.getPassword());
		
		return dataSource;
	}


	private XADataSource createDerbyDataSource(TenantDatasourceProperties tenantDatasourceProperties2) {
		
		return null;
	}


	private XADataSource createMySQLDataSource(TenantDatasourceProperties tenantDatasourceProperties2) {
		return null;
	}


	/**
	 * Entity manager definition.
	 * 
	 * @param builder
	 *            an EntityManagerFactoryBuilder.
	 * @return LocalContainerEntityManagerFactoryBean.
	 */
	@Bean(name = "tenantEntityManager")
	public LocalContainerEntityManagerFactoryBean tenantEntityManagerFactory(EntityManagerFactoryBuilder builder,@Qualifier("tenantDataSource") DataSource tenantDataSource) {
		return builder.dataSource(tenantDataSource).properties(hibernateProperties())
				.packages("jason.app.symphony.tenant.entity").persistenceUnit(tenantDatasourceProperties.getPersistenceUnitName()).build();
	}

	private Map<String, Object> hibernateProperties() {

		Resource resource = new ClassPathResource(tenantDatasourceProperties.getHibernateProperties());
		try {
			Properties properties = PropertiesLoaderUtils.loadProperties(resource);
			return properties.entrySet().stream()
					.collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue()));
		} catch (IOException e) {
			return new HashMap<String, Object>();
		}
	}

}
