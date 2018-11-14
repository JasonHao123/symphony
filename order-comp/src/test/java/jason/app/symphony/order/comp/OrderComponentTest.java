package jason.app.symphony.order.comp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import jason.app.symphony.commons.http.model.SymphonyUser;
import jason.app.symphony.order.comp.model.OrderListRequest;
import jason.app.symphony.order.comp.model.OrderListRequestBody;
import jason.app.symphony.order.comp.model.OrderListResponse;


@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = OrderApplication.class,
    properties = "greeting = Hello foo")
@EnableRouteCoverage
public class OrderComponentTest extends CamelSpringTestSupport {
	 private IDatabaseTester databaseTester;
	 
		
	@Autowired
	private DataSource orderDataSource;
	
	@Autowired
	private AbstractApplicationContext applicationContext;
	
	@BeforeClass
	public static void setupOnce() {
        
    		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
		SymphonyUser user = new SymphonyUser("anonymous","","anonymous",false,false,false,false,authorities);
		user.setSchema("1");
		SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken("hello", user, authorities));

	}

	@Before
	public void setup() throws Exception {
		databaseTester = new DataSourceDatabaseTester(orderDataSource);
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
    public void testListOrderFound() throws Exception {
    		OrderListRequestBody requestBody = new OrderListRequestBody();
    		requestBody.setPageSize(10);
    		requestBody.setPageNo(1);
    		requestBody.setFromDate("2018-01-01");
    		OrderListRequest request = new OrderListRequest();
    		request.setBody(requestBody);
    		OrderListResponse result = template.requestBody("direct:list", request, OrderListResponse.class);
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);
        assertEquals(1, result.getBody().getOrders().size());
        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("direct:list")
                .setProperty("CUST_ID",simple("jason"))
                  .to("order://list")
                  .to("mock:result");
            }
        };
    }

	@Override
	protected AbstractApplicationContext createApplicationContext() {
		
		return applicationContext;
	}
}

