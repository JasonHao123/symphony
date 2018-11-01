package jason.app.symphony.security.comp;

import java.io.File;

import javax.sql.DataSource;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import jason.app.symphony.security.comp.config.SecurityComponentConfig;
import jason.app.symphony.security.comp.model.LoginRequest;
import jason.app.symphony.security.comp.model.LoginRequestBody;
import jason.app.symphony.security.comp.model.LoginResponse;


public class SecurityComponentTest extends CamelSpringTestSupport {
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
    public void testsecurity() throws Exception {
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
		System.setProperty("app.datasource.securityDataSource.hibernate.hbm2ddl", "create");
		System.setProperty("app.datasource.securityDataSource.hibernate.dialect", "org.hibernate.dialect.H2Dialect");

		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SecurityComponentConfig.class);


		return context;
	}
}
