[![Java Maven Build Test](https://github.com/deepaksorthiya/spring-boot-keycloak-authorization/actions/workflows/maven-build.yml/badge.svg)](https://github.com/deepaksorthiya/spring-boot-keycloak-authorization/actions/workflows/maven-build.yml)
[![Docker Hub badge][dockerhub-badge]][dockerhub]

[dockerhub-badge]: https://img.shields.io/docker/pulls/deepaksorthiya/spring-boot-keycloak-authorization

[dockerhub]: https://hub.docker.com/repository/docker/deepaksorthiya/spring-boot-keycloak-authorization

---

### ** Spring Boot Security Keycloak Policy ABAC Authorization **

---

# Getting Started

### Requirements:

```
Git: 2.51+
Spring Boot: 4.0.2
Maven: 3.9+
Java: 25
Docker Desktop: Tested on 4.50.0
Keycloak: 26.5+
```

### Clone this repository:

```bash
git clone https://github.com/deepaksorthiya/spring-boot-keycloak-authorization.git
cd spring-boot-keycloak-authorization
```

### Build Project:

```bash
./mvnw clean package
```

### Start Keycloak Server Using Docker:

```bash
docker compose up
```

### Run Project:

```bash
./mvnw spring-boot:run
```

OR

```bash
java -jar .\target\spring-boot-keycloak-authorization-0.0.1-SNAPSHOT.jar
```

## Run using Docker(Optional):

### Build Docker Image(docker should be running):

```bash
./mvnw clean spring-boot:build-image -DskipTests
```

OR

```bash
docker build -t deepaksorthiya/spring-boot-keycloak-authorization:0.0.1-SNAPSHOT . 
```

### Run Using Docker

```bash
docker run --name spring-boot-keycloak-authorization -p 8080:8080 deepaksorthiya/spring-boot-keycloak-authorization:0.0.1-SNAPSHOT
```

### Testing

There are 2 endpoints exposed by the service:

* http://localhost:8080/ - can be invoked by any authenticated user
* http://localhost:8080/protected/premium - can be invoked by users with the `user_premium` role

To invoke the protected endpoints using a bearer token, your client needs to obtain an OAuth2 access token from a
Keycloak server.
In this example, we are going to obtain tokens using the resource owner password grant type so that the client can act
on behalf of any user available from
the realm.

You should be able to obtain tokens for any of these users:

| Username | Password | Roles        |
|----------|----------|--------------|
| jdoe     | jdoe     | user_premium |
| alice    | alice    | user         |

To obtain the bearer token, run for instance the following command when on Linux (please make sure to have `curl` and
`jq` packages available in your linux distribution):

```bash
curl -X POST http://localhost:8180/realms/quickstart/protocol/openid-connect/token \
-H 'content-type: application/x-www-form-urlencoded' \
-d 'client_id=authz-servlet&client_secret=secret' \
-d 'username=jdoe&password=jdoe&grant_type=password' | jq --raw-output '.access_token'
```

You can use the same command to obtain tokens on behalf of user `alice`, just make sure to change both `username` and
`password` request parameters.

After running the command above, you can now access the `http://localhost:8080/protected/premium` endpoint
because the user `jdoe` has the `user_premium` role.

```shell
curl http://localhost:8080/protected/premium \
  -H "Authorization: Bearer "$access_token
```

As a result, you will see the following response from the service:

```
Hello, jdoe!
```

Accessing Protected Resources using Requesting Party Token (RPT)
---------------------

Another approach to access resources protected by a policy enforcer is using a RPT as a bearer token, instead of a
regular access token.
The RPT is an access token with all permissions granted by the server, basically, an access token containing all
permissions granted by the server.

To obtain an RPT, you must first exchange an OAuth2 Access Token for a RPT by invoking the token endpoint at the
Keycloak server:

```bash
export rpt=$(curl -X POST \
 http://localhost:8180/realms/quickstart/protocol/openid-connect/token \
 -H "Authorization: Bearer "$access_token \
 --data "grant_type=urn:ietf:params:oauth:grant-type:uma-ticket" \
 --data "audience=authz-servlet" \
  --data "permission=Premium Resource" | jq --raw-output '.access_token' \
 )
```

The command above is trying to obtain permissions from the server in the format of a RPT. Note that the request is
specifying the resource we want
to obtain permissions, in this case, `Premium Resource`.

As an alternative, you can also obtain permissions for any resource protected by your application. For that, execute the
command below:

```bash
export rpt=$(curl -X POST \
 http://localhost:8180/realms/quickstart/protocol/openid-connect/token \
 -H "Authorization: Bearer "$access_token \
 --data "grant_type=urn:ietf:params:oauth:grant-type:uma-ticket" \
 --data "audience=authz-servlet" | jq --raw-output '.access_token' \
 )
```

After executing any of the commands above, you should get a response similar to the following:

```bash
{
    "access_token": "${rpt}",
}
``` 

To finally invoke the resource protected by the application, replace the ``${rpt}`` variable below with the value of the
``access_token`` claim from the response above and execute the following command:

```bash
curl http://localhost:8080/protected/premium \
    -H "Authorization: Bearer ${rpt}"
```

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.4.3/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.4.3/maven-plugin/build-image.html)
* [Spring Web](https://docs.spring.io/spring-boot/3.4.3/reference/web/servlet.html)
* [Spring Security](https://docs.spring.io/spring-boot/3.4.3/reference/web/spring-security.html)
* [OAuth2 Resource Server](https://docs.spring.io/spring-boot/3.4.3/reference/web/spring-security.html#web.security.oauth2.server)

### Guides

The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the
parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.

