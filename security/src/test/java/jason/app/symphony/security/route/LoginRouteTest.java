package jason.app.symphony.security.route;

import static org.junit.Assert.assertEquals;

import java.io.File;

import javax.sql.DataSource;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringBootRunner;
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
import org.springframework.boot.test.context.SpringBootTest;

import jason.app.symphony.security.SecurityApplication;
import jason.app.symphony.security.comp.model.LoginRequest;
import jason.app.symphony.security.comp.model.LoginRequestBody;
import jason.app.symphony.security.comp.model.LoginResponse;
import jason.app.symphony.security.comp.model.LoginResponseBody;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = SecurityApplication.class,
    properties = "greeting = Hello foo")
@EnableRouteCoverage
//@MockEndpoints("security:login") 
public class LoginRouteTest {
	
	 private IDatabaseTester databaseTester;
	 
	 @Autowired
	 private DataSource securityDataSource;
	 
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

    @Autowired
    ProducerTemplate template;

//    @EndpointInject(uri = "mock:security//:login")
//    private MockEndpoint mock;

    @Test
    public void shouldLoginSuccess() throws Exception {
		LoginResponse response = new LoginResponse();
		LoginResponseBody body = new LoginResponseBody();
		body.setAuthenticated(true);
		response.setBody(body); 	
		LoginRequestBody requestBody = new LoginRequestBody();
		requestBody.setUsername("jason");
		requestBody.setPassword("helloworld");
		LoginRequest request = new LoginRequest();
		request.setBody(requestBody);
		LoginResponse result = template.requestBody("direct-vm:login", request, LoginResponse.class);
		assertEquals(true, result.getBody().isAuthenticated());

    }
    
    
    @Test
    public void shouldLoginFail() throws Exception {

		LoginRequestBody requestBody = new LoginRequestBody();
		requestBody.setUsername("jason");
		requestBody.setPassword("test");
		LoginRequest request = new LoginRequest();
		request.setBody(requestBody);
		LoginResponse result = template.requestBody("direct-vm:login", request, LoginResponse.class);
		assertEquals(false, result.getBody().isAuthenticated());

    }
    

}
