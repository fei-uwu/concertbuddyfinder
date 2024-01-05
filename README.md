Setup
1. Add in your `TOPIC_ARN`, `ACCESS_KEY`, `SECRET_ACCESS_KEY`, and `REGION` in `src/main/java/.../snslambda/SendEmailSns.java` to create the SNS Topic
2. Add in your `FROM` email in `src/main/java/.../snslambda/SendEmailLambda.java` to create the Lambda function processing the SNS request
2. Add in your database url, port, database name, and username & password in `src/resources/application.properties`
3. Add in your `USER_MICROSERVICE_URL` and `CONCERT_MICROSERVICE_URL` in `src/main/java/.../finder/FinderService.java`
Run project using ```mvn spring-boot:run```

Visit documentation located in: ```localhost:8090/api```
