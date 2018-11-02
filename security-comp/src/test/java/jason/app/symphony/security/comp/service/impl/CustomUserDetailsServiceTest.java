package jason.app.symphony.security.comp.service.impl;

import java.io.File;

import javax.sql.DataSource;

import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import jason.app.symphony.security.comp.SecurityApplication;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = SecurityApplication.class,
    properties = "greeting = Hello foo")
public class CustomUserDetailsServiceTest extends CamelSpringTestSupport{
	private IDatabaseTester databaseTester;
	 
	@Autowired
	private DataSource securityDataSource;
	
	@Autowired
	private AbstractApplicationContext applicationContext;

	
	@Before
	public void setup() throws Exception {
		databaseTester = new DataSourceDatabaseTester(securityDataSource);
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
	public void testLoadUserSuccess() {
		UserDetailsService customUserDetailsService = applicationContext.getBean(UserDetailsService.class);
		UserDetails user = customUserDetailsService.loadUserByUsername("jason");
		assertNotNull(user);
		assertEquals("6adfb183a4a2c94a2f92dab5ade762a47889a5a1", user.getPassword());
	}
	
	@Test
	public void testLoadUserNotFound() {
		try {
		UserDetailsService customUserDetailsService = applicationContext.getBean(UserDetailsService.class);
		UserDetails user = customUserDetailsService.loadUserByUsername("anybody");
		fail("should throw UsernameNotFoundException");
		}catch(Exception e) {
			assertEquals(UsernameNotFoundException.class, e.getClass());
		}
		
	}

	@Test
	public void testLoginSuccess() {
		DaoAuthenticationProvider authenticationManager = applicationContext.getBean(DaoAuthenticationProvider.class);
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("jason", "helloworld");
		Authentication auth = authenticationManager.authenticate(token);
		assertEquals(true, auth.isAuthenticated());
	}
	
	@Test
	public void testLoginFail() {
		try {
			DaoAuthenticationProvider authenticationManager = applicationContext.getBean(DaoAuthenticationProvider.class);
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("jason", "wrongpassword");
			Authentication auth = authenticationManager.authenticate(token);
			fail("should throw BadCredentialsException");
		}catch(Exception e) {
			assertEquals(BadCredentialsException.class, e.getClass());
		}
	}
	
	@Override
	protected AbstractApplicationContext createApplicationContext() {
		
		return applicationContext;
	}
}
