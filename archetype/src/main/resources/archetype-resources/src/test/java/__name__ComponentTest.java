## ------------------------------------------------------------------------
## Licensed to the Apache Software Foundation (ASF) under one or more
## contributor license agreements.  See the NOTICE file distributed with
## this work for additional information regarding copyright ownership.
## The ASF licenses this file to You under the Apache License, Version 2.0
## (the "License"); you may not use this file except in compliance with
## the License.  You may obtain a copy of the License at
##
## http://www.apache.org/licenses/LICENSE-2.0
##
## Unless required by applicable law or agreed to in writing, software
## distributed under the License is distributed on an "AS IS" BASIS,
## WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
## See the License for the specific language governing permissions and
## limitations under the License.
## ------------------------------------------------------------------------
package ${package};

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

import ${package}.config.${name}ComponentConfig;
import ${package}.model.${name};

@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes= {MainConfig.class,${name}ComponentConfig.class})
@EnableRouteCoverage
public class ${name}ComponentTest extends CamelTestSupport {

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
    public void test${name}() throws Exception {
		List<Tenant> result = (List<Tenant>) this.template.requestBody("direct:start","hello");
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
                  .to("${scheme}://list")
                  .to("mock:result");
            }
        };
    }
}
