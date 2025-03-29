/*
 * Copyright 2002-2018 the original author or authors.
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
package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.logging.logback.LogbackLoggingSystemProperties;
import org.springframework.util.ClassUtils;

/**
 * OAuth resource application.
 *
 * @author Josh Cummings
 */
@SpringBootApplication
public class SpringBoot3KeycloakAuthorizationApplication {

    private static final boolean JBOSS_LOGGING_PRESENT = ClassUtils.isPresent("org.jboss.logging.Logger",
            LogbackLoggingSystemProperties.class.getClassLoader());

    static {
        if (JBOSS_LOGGING_PRESENT) {
            System.setProperty("org.jboss.logging.provider", "slf4j");
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBoot3KeycloakAuthorizationApplication.class, args);
    }

}
