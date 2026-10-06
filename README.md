# Customer API

A Spring Boot REST API for managing customer profiles. The application supports retrieving and updating customer information, validation, error handling, PostgreSQL persistence, and Caffeine caching.

## Technologies

* Java 17
* Spring Boot 4.1.1
* Spring Data JPA / Hibernate
* PostgreSQL
* Caffeine / Spring Cache
* Maven
* JUnit / Mockito

## Setup

### Prerequisites

* Java 17
* Maven
* PostgreSQL

Create a PostgreSQL database named:

```text
customerDB
```

Configure the database credentials as environment variables:

```text
DB_USERNAME=<your-username>
DB_PASSWORD=<your-password>
```

The application connects to:

```text
jdbc:postgresql://localhost:5433/customerDB
```

Run the application from IntelliJ IDEA or Maven.

## API

### Get Customer

```http
GET /api/v1/customer/{id}
```

Example:

```http
GET /api/v1/customer/1
```

Response:

```json
{
  "id": 1,
  "name": "Joni",
  "email": "joni@gmail.com",
  "photo": "joni.jpg"
}
```

Returns `404 Not Found` if the customer does not exist.

### Update Customer

```http
PUT /api/v1/customer/{id}
```

Request:

```json
{
  "name": "Joni New",
  "email": "joni.new@gmail.com",
  "photo": "joni-new.jpg"
}
```

Response:

```json
{
  "updated": true
}
```

If the submitted data is unchanged:

```json
{
  "updated": false
}
```

## Validation & Error Handling

The update request validates:

* Name is required
* Email is required and must be valid
* Photo is required

The API returns:

* `400 Bad Request` — invalid request
* `404 Not Found` — customer does not exist
* `409 Conflict` — duplicate customer data

Exceptions are handled centrally using `@RestControllerAdvice`.

## Caching

Customer profiles are cached using Caffeine.

* `@Cacheable` is used when retrieving a customer.
* `@CacheEvict` removes the cached customer after an update.
* The cache is limited to 100 entries.

## Testing & Coverage

Tests cover customer retrieval, updates, validation, error handling, duplicate detection, and cache consistency.

Current coverage:

* `CustomerService`: **100% line coverage**
* `CustomerController`: **100% line coverage**

## Design Decisions

* DTOs separate API requests/responses from the database entity.
* PostgreSQL is used for persistence.
* Caffeine is used for lightweight in-memory caching.
* Database credentials are provided through environment variables and are not committed to the repository.
