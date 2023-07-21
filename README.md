
# CUSTOMER API
The Customer API is a RESTful web service that provides endpoints for managing customer data. This API allows users to retrieve, add, update customer records. It is designed to be efficient, secure, and scalable, making it suitable for various applications.
There are various methods to retrieve records like from clientId or CustomerId.

# Table of contents
Endpoints

Pagination

Error Handling

Usage Examples

Technologies Used

Contributing

# Endpoints
The Customer API supports the following endpoints:

GET /customers/v1/get-customers: Retrieve a list of all customers in a given page.

GET /customers/v1//get-customer-by-id: Retrieve customer details by their unique identifier customer id.

POST /customers/v1//add-customer-by-kafka: Add or Update a new customer to the database using kafka.

POST /customers/v1/save-or-update-customers: Add or Update customer without using kafka.

GET /customers/v1/get-customers-by-client-id: Get a list of customers record from the database or cache specifying to the same client.

GET /customers/v1/load-redis: Load all the data from database to the redis cache.

# Pagination
The GET /customers/v1/get-customers endpoint supports pagination to efficiently retrieve large sets of customer data. By providing the page parameter, users can request a specific page of customer records. Each page contains a predefined number of customers (configurable via CUSTOMERS_PER_PAGE).

# ErrorHandling
The API returns meaningful error responses with appropriate status codes when an error occurs. Error responses include a standardized JSON format with an error message and status code.

# UsageExample
RETRIEVING ALL CUSTOMER

localhost:8080/customers/v1/get-customers

Pass the page number in requestBody

{
    "pageNumber": 2
}

# Technologies Used
he Customer API is built using the following technologies:

Spring Boot: Provides a powerful framework for building web applications with ease and efficiency.

Redis: A high-performance, in-memory data store used for caching customer data to reduce database queries and improve response times.

Kafka: A distributed streaming platform used for processing real-time customer events and data.

Caching: Redis is utilized as a caching mechanism to store frequently accessed customer data and improve API response times.

# Contributing
Contributions to the Customer API are welcome! If you find any bugs, have suggestions for improvements, or want to add new features, please create a pull request or open an issue.

