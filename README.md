# Account Management System
The Account Management System is a RESTful API built to manage account data, including creating, retrieving, updating, and deleting account records. The system leverages Spring Boot, Spring Data JPA, and integrates with external APIs for enhanced account data, such as location information using Zippopotam.

## Features
* Account Creation: Allows for new account creation with basic details.
* Account Retrieval: Supports fetching account details by account ID.
* Account Update: Provides secure updates to account details based on account status and location verification.
* Account Deletion: Removes accounts by ID.
* Location Validation: Validates account location using Zippopotam API based on country and postal code.
* Unique Email Validation: Ensures that email addresses are unique across accounts.
* Exception Handling: Catches and manages various exceptions, including unique constraint violations.

## Getting Started
Prerequisites:
* Java 17+
* Maven 3.6+
* MariaDB or MySQL database
* Git
* Installation

### Clone the repository:
```
git clone https://github.com/your-username/account-management-system.git
cd account-management-system
```

### Configure Database: TBF

### Build the project:

```
mvn clean install
```

### Run the application:

```
mvn spring-boot:run
```

## Configuration
Edit the src/main/resources/application.properties to configure:

### Database connection:

properties
```
spring.datasource.url=jdbc:mariadb://localhost:3306/account_db
spring.datasource.username=<your-username>
spring.datasource.password=<your-password>
```

Enable/Disable API Security: Set app.enable-api-security to true or false to control API security enforcement.

Usage
The following endpoints are available:

### Create Account:

POST /accounts
Description: Create a new account.
Request Body: AccountDTO with name, email, age, country, and postal code.
Get Account by ID:

GET /accounts/{id}
Description: Retrieve account information by ID.
Update Account:

PUT /accounts/update
Description: Update account information by matching email, country, and postal code. The account must have "Active" status to be updated.
Delete Account:

DELETE /accounts/{id}
Description: Delete an account by ID.
Exception Handling
Invalid or Duplicate Email: Returns HTTP 400 with a detailed error message.
Account Not Found: Returns HTTP 404.
Invalid Account Status: Returns HTTP 403 with a message indicating that the account must be "Active" to be updated.
Running Tests

#### Run the tests with Maven:

```
mvn test
```

The tests include unit tests for service logic and integration tests for the repository and API endpoints.
