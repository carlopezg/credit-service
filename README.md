# Credit Service

> Credit Service is a tool to evaluate the customer credit line request based on its characteristics.
> Customer's cash balance and monthly revenue are evaluated by Risk Management Rules to approve or reject the customer request.
>
> Restrictions:
> * Once a customer application request was accepted, subsequent user's requests will always return the same authorized credit line, regardless of the data received.
> * Only allow up to 3 successful requests every 2 minutes, subsequent request will return TOO_MANY_REQUEST.
> * Only allow one rejected request every 30 seconds, subsequent request will return TOO_MANY_REQUEST.
> * if the user reached 3 failures, will return CONFLICT status and message "A sales agent will contact you".

# Requirements

For build and running the application you need:

- [JDK 8](https://www.oracle.com/mx/java/technologies/javase/javase-jdk8-downloads.html)
- [Redis Server](https://redis.io/download)

# How it works

The application uses Spring Boot (Web, JPA, Redis, H2)

* Use a Rest API to receive the user request via http
* Use JPA to communicate with the database (H2 to facilitate server implementation)
* Redis is used to keep clustered data store as key-value pair to manage rate limiting restrictions

### Structure

* config: Initial settings
* controller: Http handler
* domain: Entities for database/datastore processing
* dto: POJOs for input/output
* enums: Custom types
* exception: Custom exceptions for exception handling
* handler: Http response exception handling
* repository: Interface to persist data (database or datastore)
* service: Business layer
* utils: Some project utilities
* resources: General project properties
* test: Unit tests

## Run application

    Configure redis connection (if required)

    Please modify resources/application.yml properties:
        - redis.server: ${YOUR_SERVER_HOST} (default localhost for this project)
        - redis.port: ${YOUR_SERVER_PORT} (commonly 6379, already defined)

Compile the project and run tests (take around 4-5 min for all test cases)

    ./gradlew clean build

And then run:

    ./gradlew bootRun