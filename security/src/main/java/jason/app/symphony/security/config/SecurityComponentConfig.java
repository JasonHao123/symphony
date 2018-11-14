package jason.app.symphony.security.config;

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
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.mysql.jdbc.jdbc2.optional.MysqlXADataSource;

import jason.app.symphony.security.service.SecurityService;
import jason.app.symphony.security.service.impl.CustomUserDetailsService;
import jason.app.symphony.security.service.impl.SecurityServiceImpl;

@Configuration
@DependsOn("transactionManager")
@EnableJpaRepositories(entityManagerFactoryRef = "securityEntityManager", transactionManagerRef = "transactionManager", basePackages = "jason.app.symphony.security.repository")
@EnableConfigurationProperties(SecurityDatasourceProperties.class)
public class SecurityComponentConfig {

	@Bean
	public CustomUserDetailsService customUserDetailsService() {
		return new CustomUserDetailsService();
	}
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new ShaPasswordEncoder();
	}
	
	@Bean
	public SecurityService securityService() {
		return new SecurityServiceImpl();
	}

	@Autowired
	private SecurityDatasourceProperties securityDatasourceProperties;

	@Bean(name = "securityDataSource", initMethod = "init", destroyMethod = "close")
	public DataSource securityDataSource() throws SQLException {
		XADataSource dataSource = null;
		if(securityDatasourceProperties==null || securityDatasourceProperties.getType()==null) {
			throw new InvalidParameterException("spring.security.datasource.type must be provided!");
		}else {
			switch(securityDatasourceProperties.getType()) {
			case "mysql":
				dataSource = createMySQLDataSource(securityDatasourceProperties);
				break;
			case "derby":
				dataSource = createDerbyDataSource(securityDatasourceProperties);
				break;
			case "hsqldb":
				dataSource = createHsqlDBDataSource(securityDatasourceProperties);
				break;
			}
		}

		 AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
		 xaDataSource.setXaDataSource(dataSource);
		 xaDataSource.setUniqueResourceName(securityDatasourceProperties.getResourceName());
		 return xaDataSource;
	}
	

	private XADataSource createHsqlDBDataSource(SecurityDatasourceProperties props) throws SQLException {
		// TODO Auto-generated method stub
		JDBCXADataSource dataSource = new JDBCXADataSource();
		dataSource.setUrl(props.getUrl());
		dataSource.setUser(props.getUsername());
		dataSource.setPassword(props.getPassword());
		
		return dataSource;
	}


	private XADataSource createDerbyDataSource(SecurityDatasourceProperties props) {
		
		return null;
	}


	private XADataSource createMySQLDataSource(SecurityDatasourceProperties props) {
		 MysqlXADataSource mysqlXaDataSource = new MysqlXADataSource();
		 mysqlXaDataSource.setUrl(props.getUrl());
		 mysqlXaDataSource.setPinGlobalTxToPhysicalConnection(true);
		 mysqlXaDataSource.setPassword(props.getPassword());
		 mysqlXaDataSource.setUser(props.getUsername());

		return mysqlXaDataSource;
	}


	/**
	 * Entity manager definition.
	 * 
	 * @param builder
	 *            an EntityManagerFactoryBuilder.
	 * @return LocalContainerEntityManagerFactoryBean.
	 */
	@Bean(name = "securityEntityManager")
	public LocalContainerEntityManagerFactoryBean securityEntityManagerFactory(EntityManagerFactoryBuilder builder,@Qualifier("securityDataSource") DataSource securityDataSource) {
		return builder.dataSource(securityDataSource).properties(hibernateProperties())
				.packages("jason.app.symphony.security.entity").persistenceUnit(securityDatasourceProperties.getPersistenceUnitName()).build();
	}

	private Map<String, Object> hibernateProperties() {

		Resource resource = new ClassPathResource(securityDatasourceProperties.getHibernateProperties());
		try {
			Properties properties = PropertiesLoaderUtils.loadProperties(resource);
			return properties.entrySet().stream()
					.collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue()));
		} catch (IOException e) {
			return new HashMap<String, Object>();
		}
	}

}
