package jason.app.symphony.security.comp;

import java.io.File;

import javax.sql.DataSource;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.CamelSpringTestSupport;
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
import org.springframework.context.support.AbstractApplicationContext;

import jason.app.symphony.security.comp.model.LoginRequest;
import jason.app.symphony.security.comp.model.LoginRequestBody;
import jason.app.symphony.security.comp.model.LoginResponse;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = SecurityApplication.class,
    properties = "greeting = Hello foo")
@EnableRouteCoverage
public class SecurityComponentTest extends CamelSpringTestSupport {
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
    public void testLoginSuccess() throws Exception {
    		LoginRequestBody requestBody = new LoginRequestBody();
    		requestBody.setUsername("jason");
    		requestBody.setPassword("helloworld");
    		LoginRequest request = new LoginRequest();
    		request.setBody(requestBody);
    		LoginResponse result = template.requestBody("direct:login", request, LoginResponse.class);
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);
        assertEquals(true, result.getBody().isAuthenticated());
        assertMockEndpointsSatisfied();
    }
    
    @Test
    public void testLoginFail() throws Exception {
    		LoginRequestBody requestBody = new LoginRequestBody();
    		requestBody.setUsername("jason");
    		requestBody.setPassword("wrongpassword");
    		LoginRequest request = new LoginRequest();
    		request.setBody(requestBody);
    		LoginResponse result = template.requestBody("direct:login", request, LoginResponse.class);
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);
        assertEquals(false, result.getBody().isAuthenticated());
        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("direct:login")
                  .to("security://login")
                  .to("mock:result");
            }
        };
    }

	@Override
	protected AbstractApplicationContext createApplicationContext() {
		
		return applicationContext;
	}

}
