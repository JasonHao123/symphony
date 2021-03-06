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
package jason.app.symphony.security.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import jason.app.symphony.security.comp.model.LoginRequest;
import jason.app.symphony.security.comp.model.LoginResponse;

/**
 * A simple Camel REST DSL route with Swagger API documentation.
 * 
 */
@Component
public class LoginRouter extends RouteBuilder {

    @Override
    public void configure() throws Exception {

       rest("/login").description("Login service")
      .consumes("application/json")
      .produces("application/json")
      .post().description("perform login")
      .type(LoginRequest.class).description("login request")
      .outType(LoginResponse.class).description("login response")
      .responseMessage()
      	.code(200).message("User successfully logged in returned")
      	.code(403).message("Access denied!").endResponseMessage()
      .to("direct-vm:login");
            
       from("direct-vm:login")
       .to("security://login")
       .to("audit://log");
       
    }

}
