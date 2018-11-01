/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jason.app.symphony.touchid.route;

import javax.servlet.http.HttpSession;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.common.HttpMessage;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import jason.app.symphony.security.comp.model.LoginRequest;
import jason.app.symphony.security.comp.model.LoginResponse;

/**
 * A simple Camel REST DSL route with Swagger API documentation.
 * 
 */
@Component
public class TouchIDRouter extends RouteBuilder {

    @Override
    public void configure() throws Exception {

       rest("/configure").description("Login service")
      .consumes("application/json")
      .produces("application/json")
      .get().description("perform login")
     // .type(LoginRequest.class).description("login request")
     // .outType(LoginResponse.class).description("login response")
      .responseMessage()
      	.code(200).message("User successfully logged in returned")
      	.code(403).message("Access denied!").endResponseMessage()
      .route().to("log:debug").process(new Processor() {
		
		@Override
		public void process(Exchange exchange) throws Exception {
			// TODO Auto-generated method stub
			Authentication principal = SecurityContextHolder.getContext().getAuthentication() ;
			String username = "";
			if(principal.getPrincipal() instanceof UserDetails) {
				UserDetails currentUser = (UserDetails) ((Authentication) principal).getPrincipal();
				username = currentUser.getUsername();
			}else if(principal.getPrincipal() instanceof String) {
				username = (String) principal.getPrincipal();
			}
			exchange.getOut().setBody(username);
		}
	});
       
    }

}
