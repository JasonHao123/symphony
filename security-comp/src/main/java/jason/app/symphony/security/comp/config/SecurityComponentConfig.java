package jason.app.symphony.security.comp.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
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

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
		entityManagerFactoryRef = "securityEntityManager", 
		transactionManagerRef = "securityTransactionManager", 
		basePackages = "jason.app.symphony.security.comp.entity"
)
public class SecurityComponentConfig {
	
	/**
	 * Security datasource definition.
	 * 
	 * @return datasource.
	 */
	@Bean(name="securityDataSource")
	@Primary
	@ConfigurationProperties(prefix = "spring.security.datasource")
	public DataSource securityDataSource() {
		return DataSourceBuilder
					.create()
					.build();
	}

	/**
	 * Entity manager definition. 
	 *  
	 * @param builder an EntityManagerFactoryBuilder.
	 * @return LocalContainerEntityManagerFactoryBean.
	 */
	@Bean(name = "securityEntityManager")
	@Primary
	public LocalContainerEntityManagerFactoryBean securityEntityManagerFactory(EntityManagerFactoryBuilder builder,@Qualifier("securityDataSource") DataSource securityDataSource) {
		return builder
					.dataSource(securityDataSource)
					.properties(hibernateProperties())
					.packages("jason.app.symphony.security.comp.entity")
					.persistenceUnit("securityPU")
					.build();
	}

	/**
	 * @param entityManagerFactory
	 * @return
	 */
	@Bean(name = "securityTransactionManager")
	@Primary
	public PlatformTransactionManager securityTransactionManager(@Qualifier("securityEntityManager") EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}

	private Map<String, Object> hibernateProperties() {

		Resource resource = new ClassPathResource("security-hibernate.properties");
		try {
			Properties properties = PropertiesLoaderUtils.loadProperties(resource);
			return properties.entrySet().stream()
											.collect(Collectors.toMap(
														e -> e.getKey().toString(),
														e -> e.getValue())
													);
		} catch (IOException e) {
			return new HashMap<String, Object>();
		}
	}
	
	@Bean
	public AffirmativeBased accessDecisionManager() {
		List<AccessDecisionVoter<? extends Object>> voters = new ArrayList<AccessDecisionVoter<? extends Object>>();
		voters.add(new RoleVoter());
		AffirmativeBased accessDecisionManager = new AffirmativeBased(voters);
		accessDecisionManager.setAllowIfAllAbstainDecisions(true);
		return accessDecisionManager;
	}
	
	@Bean
	public SecurityComponentService securityComponentService() {
		SecurityComponentServiceImpl impl = new SecurityComponentServiceImpl();
		return impl;
		
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
	public LookupStrategy lookupStrategy(@Qualifier("securityDataSource") DataSource securityDataSource) {
		return new BasicLookupStrategy(securityDataSource, aclCache(), aclAuthorizationStrategy(),
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
