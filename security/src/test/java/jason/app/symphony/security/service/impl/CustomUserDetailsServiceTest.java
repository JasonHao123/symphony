package jason.app.symphony.security.service.impl;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import javax.sql.DataSource;

import org.apache.camel.test.spring.EnableRouteCoverage;
import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import jason.app.symphony.security.MainConfig;
import jason.app.symphony.security.config.SecurityComponentConfig;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes= {MainConfig.class,SecurityComponentConfig.class})
@EnableRouteCoverage
public class CustomUserDetailsServiceTest {
	private IDatabaseTester databaseTester;
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Before
	public void setup() throws Exception {
		databaseTester = new DataSourceDatabaseTester(dataSource);
		IDataSet dataSet = new FlatXmlDataSetBuilder().build(new File("src/test/resources/dataset.xml"));
        databaseTester.setDataSet( dataSet );
        databaseTester.onSetup();
	}
	
	@After
	public void tearDown() throws Exception
    {
        databaseTester.onTearDown();
    }
	
	@Test
	public void testLoadUserFound() {
		
		UserDetails user = userDetailsService.loadUserByUsername("jason");
		assertNotNull(user);
	}

}
