package jason.app.symphony.module;

import java.io.File;
import java.util.List;

import javax.sql.DataSource;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringRunner;
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
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import jason.app.symphony.module.config.ModuleComponentConfig;
import jason.app.symphony.module.model.Module;

@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes= {MainConfig.class,ModuleComponentConfig.class})
@EnableRouteCoverage
public class ModuleComponentTest extends CamelTestSupport {

	private IDatabaseTester databaseTester;
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private ApplicationContext context;
	
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
	
    @Override
    protected CamelContext createCamelContext() throws Exception {
    		return new SpringCamelContext(context);
    }
	
    @Test
    public void testModule() throws Exception {
		List<Module> result = (List<Module>) this.template.requestBody("direct:start","hello");
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("jason",result.get(0).getName());
		assertEquals("hello",result.get(1).getName());
	    MockEndpoint mock = getMockEndpoint("mock:result");
	    mock.expectedMinimumMessageCount(1); 
	    assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("direct:start")
                  .to("module://list")
                  .to("mock:result");
            }
        };
    }
}
