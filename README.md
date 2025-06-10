# ThreadVine - E-Commerce Platform

ThreadVine is a robust e-commerce platform built with Spring Boot, designed to provide a seamless shopping experience for clothing and fashion items. The application features user authentication, product management, shopping cart functionality, and order processing.

## Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Database Schema](#database-schema)
- [API Documentation](#api-documentation)
- [Authentication](#authentication)
- [Setup and Installation](#setup-and-installation)
- [Environment Variables](#environment-variables)
- [Contributing](#contributing)

## Features

- **User Management**: Registration, authentication, and profile management
- **Product Catalog**: Browse, search, and view detailed product information
- **Shopping Cart**: Add products, update quantities, and manage cart items
- **Order Processing**: Place orders, track order status, and view order history
- **Comments and Ratings**: Leave reviews and ratings for products
- **Role-Based Access Control**: Different permissions for users, admins, and sellers

## Technology Stack

- **Java 17**: Core programming language
- **Spring Boot 3.2.5**: Application framework
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Database access and ORM
- **PostgreSQL**: Relational database
- **JWT (JSON Web Tokens)**: Stateless authentication
- **Lombok**: Reduces boilerplate code
- **MapStruct**: Object mapping between entities and DTOs
- **Swagger/OpenAPI**: API documentation
- **Maven**: Build and dependency management

## Project Structure

The application follows a standard layered architecture:

```
src/main/java/com/threadvine/
├── config/             # Configuration classes (Security, JWT, etc.)
├── controller/         # REST API controllers
├── dto/                # Data Transfer Objects
├── exceptions/         # Custom exception classes
├── io/                 # Input/Output objects for requests/responses
├── mappers/            # MapStruct mappers for entity-DTO conversion
├── model/              # Entity classes (JPA)
├── records/            # Java record classes
├── repositories/       # Spring Data JPA repositories
├── service/            # Business logic services
│   └── impl/           # Service implementations
└── util/               # Utility classes
```

## Database Schema

The application uses the following entity relationships:

- **User**: Core user entity with authentication details
  - One-to-One relationship with Cart
  - One-to-Many relationship with Order
  - One-to-Many relationship with Comment

- **Product**: Represents items for sale
  - One-to-Many relationship with Comment
  - One-to-Many relationship with CartItem
  - One-to-Many relationship with OrderItem

- **Cart**: User's shopping cart
  - One-to-One relationship with User
  - One-to-Many relationship with CartItem

- **CartItem**: Items in a user's cart
  - Many-to-One relationship with Cart
  - Many-to-One relationship with Product

- **Order**: Represents a customer order
  - Many-to-One relationship with User
  - One-to-Many relationship with OrderItem

- **OrderItem**: Items in an order
  - Many-to-One relationship with Order
  - Many-to-One relationship with Product

- **Comment**: User reviews and ratings for products
  - Many-to-One relationship with User
  - Many-to-One relationship with Product

All entities extend BaseEntity, which provides:
- UUID primary key
- Audit fields (createdAt, updatedAt, createdBy, updatedBy)

## API Documentation

The API is documented using Swagger/OpenAPI. When running the application, access the documentation at:
- Swagger UI: `/swagger-ui/index.html`
- OpenAPI JSON: `/v3/api-docs`

### Main API Endpoints

#### Authentication
- `POST /api/v1/auth/register`: Register a new user
- `POST /api/v1/auth/login`: Authenticate and get JWT token
- `POST /api/v1/auth/logout`: Invalidate JWT token
- `POST /api/v1/auth/refresh-token`: Get a new JWT token
- `POST /api/v1/auth/change-password`: Change user password

#### Products
- `GET /api/v1/products`: Get all products
- `GET /api/v1/products/{id}`: Get product by ID
- `POST /api/v1/products`: Create a new product (Admin/Seller)
- `PUT /api/v1/products/{id}`: Update a product (Admin/Seller)
- `DELETE /api/v1/products/{id}`: Delete a product (Admin)

#### Shopping Cart
- `GET /api/v1/cart`: Get user's cart
- `POST /api/v1/cart/add`: Add product to cart
- `DELETE /api/v1/cart`: Clear cart

#### Orders
- `POST /api/v1/orders`: Create a new order
- `GET /api/v1/orders`: Get all orders (Admin/Seller)
- `GET /api/v1/orders/{orderId}`: Get order by ID
- `GET /api/v1/orders/user`: Get current user's orders
- `PUT /api/v1/orders/{orderId}/status`: Update order status (Admin)

## Authentication

The application uses JWT (JSON Web Token) for authentication:

1. **Token Generation**: When a user logs in, a JWT token is generated
2. **Token Usage**: The token must be included in the Authorization header for protected endpoints
3. **Token Format**: `Authorization: Bearer {token}`
4. **Token Expiration**: Tokens expire after 24 hours (configurable)
5. **Token Refresh**: Users can refresh their token without re-authentication
6. **Token Blacklisting**: Tokens are blacklisted on logout for security

User roles include:
- USER: Basic user permissions
- ADMIN: Full administrative access
- SELLER: Product management permissions
- BUYER: Standard shopping permissions
- SALES_REP: Sales representative permissions

## Setup and Installation

### Prerequisites
- Java 17 or higher
- PostgreSQL database
- Maven

### Steps
1. Clone the repository
   ```
   git clone https://github.com/yourusername/threadvine.git
   cd threadvine
   ```

2. Create a `.env` file in the project root with the required environment variables (see below)

3. Build the application
   ```
   mvn clean install
   ```

4. Run the application
   ```
   mvn spring-boot:run
   ```

5. Access the application at `http://localhost:9090`

## Environment Variables

The application requires the following environment variables:

- `DB_URL`: PostgreSQL database URL
- `DB_USER_NAME`: Database username
- `DB_PASSWORD`: Database password
- `JWT_SECRET`: Secret key for JWT token generation
- `JWT_EXPIRATION`: Token expiration time in milliseconds

These can be set in a `.env` file in the project root directory.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a new Pull Request