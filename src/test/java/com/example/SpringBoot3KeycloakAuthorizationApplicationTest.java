package com.example;/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@link SpringBoot3KeycloakAuthorizationApplication}.
 *
 * @author Deepak Katariya
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class SpringBoot3KeycloakAuthorizationApplicationTest {


    @Autowired
    MockMvc mvc;

    @Autowired
    KeycloakContainer keycloakContainer;

    @Test
    void testValidBearerToken() throws Exception {
        this.mvc.perform(get("/").with(bearerTokenFor("alice")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello, alice!")));
    }

    @Test
    void testOnlyPremiumUsers() throws Exception {
        this.mvc.perform(get("/protected/premium").with(bearerTokenFor("jdoe")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello, jdoe!")));

        this.mvc.perform(get("/protected/premium").with(bearerTokenFor("alice")))
                .andExpect(status().isForbidden());
    }

    @Test
    void testInvalidBearerToken() throws Exception {
        this.mvc.perform(get("/"))
                .andExpect(status().isForbidden());
    }

    private RequestPostProcessor bearerTokenFor(String username) {
        String token = getToken(username, username);

        return request -> {
            request.addHeader("Authorization", "Bearer " + token);
            return request;
        };
    }

    public String getToken(String username, String password) {
        Keycloak keycloak = Keycloak.getInstance(
                keycloakContainer.getAuthServerUrl(),
                "quickstart",
                username,
                password,
                "authz-servlet",
                "secret");
        return keycloak.tokenManager().getAccessTokenString();
    }
}
