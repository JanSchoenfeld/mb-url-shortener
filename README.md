# mb-url-shortener

This is my project for a coding challenge by Mercedes-Benz Mobility AG.

This project uses Spring Boot 3, PostgreSQL, flyway, docker, docker-compose and Swagger UI for documentation.

## Getting started

Run the following commands to clone the application, create and start docker images of the application and a
corresponding PostgreSQL database:

```
git clone https://github.com/JanSchoenfeld/mb-url-shortener.git
cd mb-url-shortener
./run.sh
```

Before you execute ``./run.sh`` make sure port ``8080`` and ``5432`` are available - docker-compose will expose app and
db on these ports.

You can stop the application and database by executing

```
docker-compose down
```

Note that for the sake of this being a showcase, any data written to the database will disappear after you tear down the docker containers.
However, you are still free to host your own instance of a PostgreSQL database and configure the application accordingly
to persist the data even after application shut down.

## Documentation

If the application runs successfully you can visit ``https://localhost:8080/api-docs`` to explore the documentation of
the created endpoints for yourself.
Browser redirect is best tested through the browser though.

## Configuring the application

Besides database and flyway properties there are only a few other configurations we need to shortly touch on:

```
validation.character-limit=6                  # sets the character limit for the generated urls, can be changed by setting this property to another number, if not provided the default value will be 6.
analytics.ttl.cron.expression=0 0 0 * * ?     # configures the cron job for resetting the daily clicks, set to 12am midnight. uses current timezone.
springdoc.swagger-ui.path=/api-docs           # defines a custom swagger-ui path
```

## Further notes

- In this applications context I mainly reference three types of URL data:
  - ``original``: represents the provided URL that the resulting shorted URL should redirect to, e.g. ``https://example.org/somePath``
  - ``shorted``: represents the Base62 encoded part of the URL, e.g. for the resulting URL ``http://localhost:8080/AJH5s2`` shorted would describe the String ``AJH5s2`` and not the entire URL
  - ``target``: represents a requested representation of ``shorted``, e.g. a user provides ``https://example.org/somePath`` with the ``target`` ``click``, the endpoint would attempt to create the resource with ``shorted`` = ``click``
- Test coverage has been limited to core functionality of the services, if this was an application that was meant to be
  deployed in a productive environment, tests for important functionalities such as database calls, controller logic and utility classes
  would have to be added.
- Security features have been omitted because they weren't required. For productive use we could implement
  authentication and authorization features depending on the requirements using Spring Security.
