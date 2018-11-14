package ${package}.config;

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

import ${package}.service.${name}Service;
import ${package}.service.impl.${name}ServiceImpl;

@Configuration
@DependsOn("transactionManager")
@EnableJpaRepositories(entityManagerFactoryRef = "${scheme}EntityManager", transactionManagerRef = "transactionManager", basePackages = "${package}.repository")
@EnableConfigurationProperties(${name}DatasourceProperties.class)
public class ${name}ComponentConfig {

	@Bean
	public ${name}Service ${scheme}Service() {
		return new ${name}ServiceImpl();
	}

	@Autowired
	private ${name}DatasourceProperties ${scheme}DatasourceProperties;

	@Bean(name = "${scheme}DataSource", initMethod = "init", destroyMethod = "close")
	public DataSource ${scheme}DataSource() throws SQLException {
		XADataSource dataSource = null;
		if(${scheme}DatasourceProperties==null || ${scheme}DatasourceProperties.getType()==null) {
			throw new InvalidParameterException("spring.${scheme}.datasource.type must be provided!");
		}else {
			switch(${scheme}DatasourceProperties.getType()) {
			case "mysql":
				dataSource = createMySQLDataSource(${scheme}DatasourceProperties);
				break;
			case "derby":
				dataSource = createDerbyDataSource(${scheme}DatasourceProperties);
				break;
			case "hsqldb":
				dataSource = createHsqlDBDataSource(${scheme}DatasourceProperties);
				break;
			}
		}

		 AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
		 xaDataSource.setXaDataSource(dataSource);
		 xaDataSource.setUniqueResourceName(${scheme}DatasourceProperties.getResourceName());
		 return xaDataSource;
	}
	

	private XADataSource createHsqlDBDataSource(${name}DatasourceProperties props) throws SQLException {
		// TODO Auto-generated method stub
		JDBCXADataSource dataSource = new JDBCXADataSource();
		dataSource.setUrl(props.getUrl());
		dataSource.setUser(props.getUsername());
		dataSource.setPassword(props.getPassword());
		
		return dataSource;
	}


	private XADataSource createDerbyDataSource(${name}DatasourceProperties props) {
		
		return null;
	}


	private XADataSource createMySQLDataSource(${name}DatasourceProperties props) {
		return null;
	}


	/**
	 * Entity manager definition.
	 * 
	 * @param builder
	 *            an EntityManagerFactoryBuilder.
	 * @return LocalContainerEntityManagerFactoryBean.
	 */
	@Bean(name = "${scheme}EntityManager")
	public LocalContainerEntityManagerFactoryBean ${scheme}EntityManagerFactory(EntityManagerFactoryBuilder builder,@Qualifier("${scheme}DataSource") DataSource ${scheme}DataSource) {
		return builder.dataSource(${scheme}DataSource).properties(hibernateProperties())
				.packages("${package}.entity").persistenceUnit(${scheme}DatasourceProperties.getPersistenceUnitName()).build();
	}

	private Map<String, Object> hibernateProperties() {

		Resource resource = new ClassPathResource(${scheme}DatasourceProperties.getHibernateProperties());
		try {
			Properties properties = PropertiesLoaderUtils.loadProperties(resource);
			return properties.entrySet().stream()
					.collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue()));
		} catch (IOException e) {
			return new HashMap<String, Object>();
		}
	}

}
