package jason.app.symphony.security.comp.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.AclPermissionCacheOptimizer;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
import org.springframework.security.acls.domain.ConsoleAuditLogger;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.domain.EhCacheBasedAclCache;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jason.app.symphony.security.comp.dao.AclDao;
import jason.app.symphony.security.comp.dao.UserDao;
import jason.app.symphony.security.comp.dao.impl.AclDaoJpa;
import jason.app.symphony.security.comp.dao.impl.UserDaoJpa;
import jason.app.symphony.security.comp.service.SecurityComponentService;
import jason.app.symphony.security.comp.service.impl.CustomUserDetailsService;
import jason.app.symphony.security.comp.service.impl.JpaMutableAclService;
import jason.app.symphony.security.comp.service.impl.SecurityComponentServiceImpl;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
//@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@EntityScan(basePackages = "jason.app.symphony.security.comp.entity")
@EnableTransactionManagement
public class SecurityComponentConfig {
	
	@Bean
	public SecurityComponentService securityComponentService() {
		SecurityComponentServiceImpl impl = new SecurityComponentServiceImpl();
		return impl;
		
	}
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Value("${app.datasource.securityDataSource.hibernate.hbm2ddl}") String hbm2ddl,
			@Value("${app.datasource.securityDataSource.hibernate.dialect}") String dialect) {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(securityDataSource());
		em.setPackagesToScan(new String[] { "jason.app.symphony.security.comp.entity" });

		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		Properties properties = new Properties();
		properties.setProperty("hibernate.hbm2ddl.auto", hbm2ddl);
		properties.setProperty("hibernate.dialect", dialect);
		em.setJpaProperties(properties);

		return em;
	}

	@Bean
	@Primary
	@ConfigurationProperties("app.datasource.securityDataSource")
	public DataSourceProperties securityDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	@Primary
	@ConfigurationProperties("app.datasource.securityDataSource.configuration")
	public DataSource securityDataSource() {
		return securityDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
	}

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf);

		return transactionManager;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	@Bean
	public UserDetailsService customUserDetailsService() {
		return new CustomUserDetailsService();
	}

	@Bean
	public AclDao aclDao() {
		return new AclDaoJpa();
	}

	@Bean
	public UserDao userDao() {
		return new UserDaoJpa();
	}

	@Bean
	public MutableAclService aclService() {
		JpaMutableAclService aclService = new JpaMutableAclService();
		return aclService;
	}

	@Bean
	public MethodSecurityExpressionHandler createExpressionHandler() {
		DefaultMethodSecurityExpressionHandler expressionHandler = defaultMethodSecurityExpressionHandler();
		expressionHandler.setPermissionEvaluator(new AclPermissionEvaluator(aclService()));
		expressionHandler.setPermissionCacheOptimizer(new AclPermissionCacheOptimizer(aclService()));
		return expressionHandler;
	}

	@Bean
	public EhCacheBasedAclCache aclCache() {
		return new EhCacheBasedAclCache(aclEhCacheFactoryBean().getObject(), permissionGrantingStrategy(),
				aclAuthorizationStrategy());
	}

	@Bean
	public EhCacheFactoryBean aclEhCacheFactoryBean() {
		EhCacheFactoryBean ehCacheFactoryBean = new EhCacheFactoryBean();
		ehCacheFactoryBean.setCacheManager(aclCacheManager().getObject());
		ehCacheFactoryBean.setCacheName("aclCache");
		return ehCacheFactoryBean;
	}

	@Bean
	public EhCacheManagerFactoryBean aclCacheManager() {
		EhCacheManagerFactoryBean factory = new EhCacheManagerFactoryBean();
	//	factory.setCacheManagerName("aclCacheManager"+new Date());
		return factory;
	}

	@Bean
	public DefaultPermissionGrantingStrategy permissionGrantingStrategy() {
		ConsoleAuditLogger consoleAuditLogger = new ConsoleAuditLogger();
		return new DefaultPermissionGrantingStrategy(consoleAuditLogger);
	}

	@Bean
	public AclAuthorizationStrategy aclAuthorizationStrategy() {
		return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"));
	}

	@Bean
	public LookupStrategy lookupStrategy() {
		return new BasicLookupStrategy(securityDataSource(), aclCache(), aclAuthorizationStrategy(),
				new ConsoleAuditLogger());
	}

	@Bean
	public DefaultMethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler() {
		return new DefaultMethodSecurityExpressionHandler();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new ShaPasswordEncoder();
	}
	
	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService,PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(passwordEncoder);
		provider.setUserDetailsService(userDetailsService);
		return provider;
	}
	
}
