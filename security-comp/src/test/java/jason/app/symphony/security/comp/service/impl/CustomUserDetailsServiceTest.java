package jason.app.symphony.security.comp.service.impl;

import java.io.File;

import javax.sql.DataSource;

import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import jason.app.symphony.security.comp.config.SecurityComponentConfig;

@Ignore
public class CustomUserDetailsServiceTest extends CamelSpringTestSupport{
	private IDatabaseTester databaseTester;
	 
	@Before
	public void setup() throws Exception {
		DataSource dataSource = (DataSource) applicationContext.getBean("securityDataSource");
		databaseTester = new DataSourceDatabaseTester(dataSource);
		IDataSet dataSet = new FlatXmlDataSetBuilder().build(new File("src/test/resources/dataset.xml"));
        databaseTester.setDataSet( dataSet );
	// will call default setUpOperation
        databaseTester.onSetup();
	}
	
	@After
	public void tearDown() throws Exception
    {
	// will call default tearDownOperation
        databaseTester.onTearDown();
    }
	@Test
	public void testLoadUser() {
		UserDetailsService customUserDetailsService = applicationContext.getBean(UserDetailsService.class);
		UserDetails user = customUserDetailsService.loadUserByUsername("jason");
		assertNotNull(user);
		assertEquals("6adfb183a4a2c94a2f92dab5ade762a47889a5a1", user.getPassword());
	}

	@Test
	public void testLogin() {
		DaoAuthenticationProvider authenticationManager = applicationContext.getBean(DaoAuthenticationProvider.class);
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("jason", "helloworld");
		Authentication auth = authenticationManager.authenticate(token);
		assertEquals(true, auth.isAuthenticated());
	}
	
	@Override
	protected AbstractApplicationContext createApplicationContext() {
		System.setProperty("app.datasource.securityDataSource.hibernate.hbm2ddl", "create");
		System.setProperty("app.datasource.securityDataSource.hibernate.dialect", "org.hibernate.dialect.H2Dialect");

		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SecurityComponentConfig.class);


		return context;
	}
}
