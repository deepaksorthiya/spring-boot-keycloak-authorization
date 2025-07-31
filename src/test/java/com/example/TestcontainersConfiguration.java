package com.example;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    static String KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak:26.3.2";
    static String realmImportFile = "/realm-import.json";
    static String realmName = "quickstart";

    @Bean
    public KeycloakContainer keycloakContainer() {
        return new KeycloakContainer(KEYCLOAK_IMAGE)
                .withRealmImportFile(realmImportFile);
    }

    @Bean
    public DynamicPropertyRegistrar keycloakContainerProperties(KeycloakContainer keycloakContainer) {
        System.out.println("keycloakContainer: " + keycloakContainer.getAuthServerUrl());
        return (properties) -> {
            properties.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri", () -> keycloakContainer.getAuthServerUrl() + "/realms/" + realmName + "/protocol/openid-connect/certs");
            properties.add("auth.server.url", keycloakContainer::getAuthServerUrl);
        };
    }

}
