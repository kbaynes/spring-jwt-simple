# Simple Spring JWT
This application demonstrates the use of JSON Web Tokens (JWT) in Spring Boot. This application does not have a GUI, so it must be exercised via the AppTests class, or run and accessed via PostMan or cURL (or similar). The application authenticates against a database.

For the simplest demonstration, git clone the repo and run the Maven package command to run the tests in AppTests
Mac/Linux : _./mvnw package_ (or _mvn package_ if Maven is installed)
Windows: _mvnw package_ (or _mvn package_ if Maven is installed)

You should see *[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0* printed in the output.

## PostMan Testing
Git clone the repo and run the application, then open the *Simple-Spring-JWT.postman_collection.json* file in PostMan.
By default, the PostMan requests default to localhost:8080. The *LoginAs* requests will authenticate to the application and will receive a JSON Web Token as a return, and then the following script runs to set a PostMan variable. This will allow you to then simply run *Test <role> Access* requests using the returned JWT, without having to set a variable manually.

```
var data = JSON.parse(responseBody);
postman.setEnvironmentVariable("accessToken", data.accessToken);
```

The *Login As Owner* and *Login As Admin* requests should then allow calls to succeed to: *Test Admin Access*, *Test User Access*, and *Test Customer Access*. The *Login as Customer* should succeed for *Test Customer Access* and receive 403 Forbidden for all other *Test <role> Access* calls. Similary, the *Login as User* should succeed for *Test User Access* and receive 403 Forbidden for all other *Test <role> Access* calls.

## H2 Database

When the application runs, it start an H2 in memory database, which is accessible at http://localhost:8080/h2-console. Login using the following settings:
Driver Class = org.h2.Driver
JDBC URL = jdbc:h2:mem:testdb
User Name = sa
Password = <blank>

For this console to work properly, the WebSecurityConfig class contains the line: *http.headers().frameOptions().disable();*. This line should be removed in production.

The tables are then searchable and updateable.

The tables are initially populated using the Spring Boot mechanism of the *<app-root>/src/main/resources/import.sql* file, which is automatically run on application startup. Internal to the application, data is accessed via JPA/Hibernate in the Role and User classes.

## Authentication


http://localhost:8080/login

http://localhost:8080/logout

http://localhost:8080/public

http://localhost:8080/admin


