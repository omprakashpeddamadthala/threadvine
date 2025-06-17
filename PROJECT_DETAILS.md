# ThreadVine - Detailed Project Description

This document provides a detailed breakdown of the ThreadVine e-commerce platform, including its architecture, packages, classes, and methods.

## Table of Contents

- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Detailed Class and Method Analysis](#detailed-class-and-method-analysis)
  - [Root Package (`com.threadvine`)](#root-package-comthreadvine)
  - [Configuration (`com.threadvine.config`)](#configuration-comthreadvineconfig)
  - [Controllers (`com.threadvine.contoller`)](#controllers-comthreadvinecontoller)
  - [Data Transfer Objects (`com.threadvine.dto`)](#data-transfer-objects-comthreadvinedto)
  - [Email Handling (`com.threadvine.email`)](#email-handling-comthreadvineemail)
  - [Custom Exceptions (`com.threadvine.exceptions`)](#custom-exceptions-comthreadvineexceptions)
  - [Input/Output Objects (`com.threadvine.io`)](#inputoutput-objects-comthreadvineio)
  - [Mappers (`com.threadvine.mappers`)](#mappers-comthreadvinemappers)
  - [Models/Entities (`com.threadvine.model`)](#modelsentities-comthreadvinemodel)
  - [Records (`com.threadvine.records`)](#records-comthreadvinerecords)
  - [Repositories (`com.threadvine.repositories`)](#repositories-comthreadvinerepositories)
  - [Services (`com.threadvine.service` and `com.threadvine.service.impl`)](#services-comthreadvineservice-and-comthreadvineserviceimpl)
  - [Utilities (`com.threadvine.util`)](#utilities-comthreadvineutil)

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
- **SendGrid**: Email delivery service
- **Maven**: Build and dependency management
- **Dotenv**: Environment variable management

## Project Structure

The application follows a standard layered architecture:

```
src/main/java/com/threadvine/
├── ThreadvineApplication.java # Main application class
├── config/                    # Configuration classes (Security, JWT, JPA, OpenAPI)
├── controller/                # REST API controllers for various resources
├── dto/                       # Data Transfer Objects for API communication
├── email/                     # Email sending services and components
├── exceptions/                # Custom exception classes for error handling
├── io/                        # Input/Output objects, primarily for auth requests/responses
├── mappers/                   # MapStruct mappers for entity-DTO conversion
├── model/                     # JPA Entity classes representing database tables
├── records/                   # Java record classes for concise data carriers
├── repositories/              # Spring Data JPA repositories for database interaction
├── service/                   # Business logic service interfaces
│   └── impl/                  # Service implementations
└── util/                      # Utility classes (e.g., JwtUtil)
```

## Detailed Class and Method Analysis

This section provides an overview of each package and its key classes and methods.

### Root Package (`com.threadvine`)

-   **`ThreadvineApplication.java`**:
    -   The main entry point for the Spring Boot application.
    -   Uses `SpringApplication.run()` to start the application.
    -   Statically loads environment variables from a `.env` file using `Dotenv` and sets them as system properties.

### Configuration (`com.threadvine.config`)

-   **`JpaConfig.java`**:
    -   Enables JPA auditing (`@EnableJpaAuditing`).
    -   `auditorAware()`: Provides an `AuditorAware<String>` bean that uses `AuditorAwareImpl` to supply the current user's email for auditing purposes (populating `createdBy` and `updatedBy` fields in entities).
-   **`JwtAuthenticationFilter.java`**:
    -   A `OncePerRequestFilter` that handles JWT-based authentication.
    -   `doFilterInternal()`: Extracts the JWT token from the "Authorization" header, validates it, and sets the authentication in the `SecurityContextHolder` if valid.
    -   `shouldSkipFilter()`: Defines paths (e.g., `/login`, `/register`, Swagger paths) that should bypass this filter.
    -   `extractToken()`: Helper method to get the "Bearer" token from the request header.
-   **`OpenApiConfig.java`**:
    -   Configures OpenAPI (Swagger) documentation.
    -   `myOpenAPI()`: Defines the `OpenAPI` bean with application information (title, version, contact, license), server details, and security schemes (Bearer Authentication for JWT).
-   **`SecurityConfig.java`**:
    -   Main security configuration class (`@EnableMethodSecurity`).
    -   `securityFilterChain()`: Configures the `SecurityFilterChain`. Disables CSRF, defines authorization rules for HTTP requests (e.g., permit all for `/api/v1/auth/**`, `/images/**`, GET on `/api/v1/products/**`, Swagger paths; authenticate others). Configures session management to be stateless. Adds the `JwtAuthenticationFilter` before the `UsernamePasswordAuthenticationFilter`.
    -   `passwordEncoder()`: Provides a `PasswordEncoder` bean (delegating password encoder).
    -   `authenticationManager()`: Provides an `AuthenticationManager` bean.
    -   `jwtAuthenticationFilter()`: Provides the `JwtAuthenticationFilter` bean, injecting the `AuthenticationService`.

### Controllers (`com.threadvine.contoller`)

-   **`AuthController.java`**:
    -   Handles all authentication-related requests under `/api/v1/auth`.
    -   `login()`: Authenticates a user with email and password, returns a JWT.
    -   `register()`: Registers a new user.
    -   `changePassword()`: Allows an authenticated user to change their password.
    -   `logout()`: Invalidates the user's JWT by adding it to a blacklist.
    -   `refreshToken()`: Generates a new JWT using a valid current token and blacklists the old one.
-   **`CartController.java`**:
    -   Manages shopping cart operations under `/api/v1/cart`. Requires authentication.
    -   `addToCart()`: Adds a product to the authenticated user's cart.
    -   `getCart()`: Retrieves the authenticated user's shopping cart.
    -   `clearCart()`: Removes all items from the authenticated user's cart.
-   **`CommentController.java`**:
    -   Handles product comments under `/api/v1/comments`.
    -   `addComment()`: Allows an authenticated user to add a comment to a specific product.
    -   `getCommentsByProductId()`: Retrieves all comments for a specific product (publicly accessible).
-   **`ErrorController.java`**:
    -   A global exception handler (`@RestControllerAdvice`) for the application.
    -   Handles various specific exceptions (`AccessDeniedException`, `IllegalArgumentException`, `IllegalStateException`, `InsufficientQuantityException`, `BadCredentialsException`, `ProductNotFoundException`, `MethodArgumentNotValidException`) and a generic `Exception`.
    -   Returns a standardized `ApiErrorResponse` for each caught exception.
-   **`OrderController.java`**:
    -   Manages orders under `/api/v1/orders`.
    -   `createOrder()`: Creates a new order for the authenticated user from their cart.
    -   `getAllOrders()`: Retrieves all orders (Admin/Seller role required).
    -   `getOrderByOrderId()`: Retrieves a specific order by its ID (accessible to authenticated users who own the order or Admins/Sellers).
    -   `getUserOrders()`: Retrieves all orders for the currently authenticated user.
    -   `updateOrderStatus()`: Updates the status of an order (Admin role required).
-   **`ProductController.java`**:
    -   Manages products under `/api/v1/products`.
    -   `createProduct()`: Creates a new product, supports image upload (Admin/Seller role required).
    -   `updateProduct()`: Updates an existing product, supports image upload (Admin/Seller role required).
    -   `deleteProduct()`: Deletes a product (Admin/Seller role required).
    -   `getProductById()`: Retrieves a specific product by ID (publicly accessible).
    -   `getAllProducts()`: Retrieves a list of all available products (publicly accessible).

### Data Transfer Objects (`com.threadvine.dto`)

-   **`ApiErrorResponse.java`**: Structure for returning error details in API responses. Includes HTTP status, a general message, and a list of specific field errors.
-   **`CartDTO.java`**: Represents a cart, including its ID, user ID, and a list of `CartItemDTO`.
-   **`CartItemDTO.java`**: Represents an item in a cart, including its ID, product ID, and quantity. Includes validation (`@Positive`).
-   **`CommentDTO.java`**: Represents a comment, including ID, content, score (1-5 validation), and user ID. Includes validation (`@NotBlank`, `@Min`, `@Max`).
-   **`OrderDTO.java`**: Represents an order, including ID, user ID, shipping address, phone number, status, creation date, and a list of `OrderItemDTO`. Includes validation (`@NotBlank`).
-   **`OrderItemDTO.java`**: Represents an item in an order, including ID, order ID, product ID, quantity, and price. Includes validation (`@Positive`).
-   **`ProductDTO.java`**: Represents a product for detailed views or creation/update. Includes ID, name, description, price, quantity, optional image URL, and optional list of comments. Includes validation (`@NotBlank`, `@Positive`, `@PositiveOrZero`).
-   **`ProductListDTO.java`**: A simpler representation of a product for listings, including ID, name, description, price, quantity, and optional image URL.

### Email Handling (`com.threadvine.email`)

-   **`EmailSender.java`**: Interface defining the contract for email sending services.
    -   `send(String to, String subject, String content)`: Method to send an email.
    -   `getEmailType()`: Returns the `EmailLog.EmailType` this sender handles.
-   **`ErrorNotificationEmailSender.java`**, **`OrderConfirmationEmailSender.java`**, **`PasswordResetEmailSender.java`**, **`RegistrationEmailSender.java`**:
    -   Concrete implementations of `EmailSender` for specific email types.
    -   Each uses `SendGridClient` to dispatch emails.
-   **`SendGridClient.java`**:
    -   Client component responsible for interacting with the SendGrid API.
    -   `sendEmail()`: Constructs and sends an email using the SendGrid library. Reads API key and sender details from application properties (`@Value`).

### Custom Exceptions (`com.threadvine.exceptions`)

-   **`CartNotFoundException.java`**: Thrown when a cart is not found.
-   **`InsufficientQuantityException.java`**: Thrown when there isn't enough product quantity to fulfill a request.
-   **`ProductNotFoundException.java`**: Thrown when a product is not found.
-   **`UserNotFoundException.java`**: Thrown when a user is not found.
    *(All are `RuntimeException` subclasses)*

### Input/Output Objects (`com.threadvine.io`)

-   **`AuthResponse.java`**: DTO for authentication responses, containing the user's email and the JWT token.
-   **`ChangePasswordRequest.java`**: DTO for password change requests, containing current and new passwords.
-   **`LoginRequest.java`**: DTO for login requests, containing email and password.

### Mappers (`com.threadvine.mappers`)

-   MapStruct interfaces for converting between JPA entities and DTOs.
-   **`CartMapper.java`**: Maps `Cart`/`CartItem` entities to/from `CartDTO`/`CartItemDTO`.
-   **`CommentMapper.java`**: Maps `Comment` entity to/from `CommentDTO`.
-   **`OrderMapper.java`**: Maps `Order`/`OrderItem` entities to/from `OrderDTO`/`OrderItemDTO`.
-   **`ProductMapper.java`**: Maps `Product` entity to/from `ProductDTO`/`ProductListDTO`. Uses `CommentMapper` for nested comment mapping.

### Models/Entities (`com.threadvine.model`)

-   JPA entities representing the database schema.
-   **`BaseEntity.java`**: Abstract base class for all entities.
    -   Provides `id` (UUID, auto-generated on pre-persist), `createdAt`, `updatedAt`, `createdBy`, `updatedBy` (audited fields).
    -   Implements `equals()` and `hashCode()` based on the `id` field.
-   **`Cart.java`**: Represents a user's shopping cart.
    -   `@OneToOne` with `User`.
    -   `@OneToMany` with `CartItem` (cascade all, orphan removal).
-   **`CartItem.java`**: Represents an item in a cart.
    -   `@ManyToOne` with `Cart`.
    -   `@ManyToOne` with `Product`.
    -   `quantity`: Integer.
-   **`Comment.java`**: Represents a user's comment on a product.
    -   `content`: String.
    -   `score`: Integer.
    -   `@ManyToOne` with `Product`.
    -   `@ManyToOne` with `User`.
-   **`EmailLog.java`**: Entity for logging email sending attempts.
    -   `emailType`: Enum (`REGISTRATION_EMAIL`, `FORGOT_PASSWORD_EMAIL`, etc.).
    -   `recipient`, `subject`, `content` (long text).
    -   `sent` (boolean), `sentAt` (timestamp).
    -   `errorMessage` (long text).
-   **`Order.java`**: Represents a customer's order.
    -   `@ManyToOne` with `User`.
    -   `address`, `phoneNumber`: String.
    -   `status`: Enum (`OrderStatus`: PENDING, DELIVERING, DELIVERED, CANCELLED).
    -   `createdAt`: Timestamp.
    -   `@OneToMany` with `OrderItem` (cascade all, orphan removal).
-   **`OrderItem.java`**: Represents an individual item within an order.
    -   `@ManyToOne` with `Order`.
    -   `@ManyToOne` with `Product`.
    -   `quantity`: Integer.
    -   `price`: BigDecimal (price at the time of order).
-   **`Product.java`**: Represents a product available for sale.
    -   `name`, `description`: String.
    -   `price`: BigDecimal.
    -   `quantity`: Integer (stock).
    -   `imageUrl`: String.
    -   `@OneToMany` with `Comment` (cascade all, orphan removal).
-   **`User.java`**: Represents a user of the application. Implements Spring Security's `UserDetails`.
    -   `email` (unique identifier, used as username).
    -   `password` (encoded).
    -   `role`: Enum (`Role`: USER, ADMIN, SELLER, BUYER, SALES_REP).
    -   `@OneToOne` with `Cart` (cascade all).
    -   Overrides `UserDetails` methods (`getAuthorities()`, `getUsername()`, `isAccountNonExpired()`, etc.).

### Records (`com.threadvine.records`)

-   **`RegisterRequest.java`**: A Java record used as a DTO for user registration, holding `email`, `password`, and `User.Role`.

### Repositories (`com.threadvine.repositories`)

-   Spring Data JPA repository interfaces extending `JpaRepository` for database operations.
-   **`CartRepository.java`**: For `Cart` entity. Includes `findByUserId()`.
-   **`CommentRepository.java`**: For `Comment` entity. Includes `findByProductId()`.
-   **`EmailLogRepository.java`**: For `EmailLog` entity. Includes `findByEmailType()` and `findByRecipient()`.
-   **`OrderRepository.java`**: For `Order` entity. Includes `findByUserId()`.
-   **`ProductRepository.java`**: For `Product` entity. Includes `existsByName()`.
-   **`UserRepository.java`**: For `User` entity. Includes `findByEmail()`.

### Services (`com.threadvine.service` and `com.threadvine.service.impl`)

-   Service layer containing business logic. Interfaces are in `com.threadvine.service`, implementations in `com.threadvine.service.impl`.
-   **`AuthenticationService.java` (interface) / `AuthenticationServiceImpl.java`**:
    -   `loadUserByUsername()`: Loads user details for Spring Security.
    -   `generateToken()`: Creates a JWT for a user.
    -   `validateToken()`: Validates a JWT against user details and blacklist.
    -   `extractUsername()`: Extracts username from a JWT.
-   **`AuditorAwareImpl.java` (implementation of `AuditorAware<String>`)**:
    -   `getCurrentAuditor()`: Returns the email of the currently authenticated user for JPA auditing.
-   **`CartService.java` (interface) / `CartServiceImpl.java`**:
    -   `addToCart()`: Adds a product to a user's cart, checks stock.
    -   `getCartByUserId()`: Retrieves a user's cart.
    -   `clearCart()`: Clears all items from a user's cart.
-   **`CommentService.java` (interface) / `CommentServiceImpl.java`**:
    -   `addComment()`: Adds a comment to a product by a user.
    -   `getCommentsByProductId()`: Retrieves all comments for a product.
-   **`EmailService.java` (interface) / `EmailServiceImpl.java`**:
    -   `sendRegistrationEmail()`, `sendPasswordResetEmail()`, `sendOrderConfirmationEmail()`, `sendErrorEmail()`: Methods to send specific types of emails asynchronously. They use `EmailTemplateService` and the appropriate `EmailSender`.
    -   `logEmail()`: Logs email sending attempts to the database.
-   **`EmailTemplateService.java`**:
    -   `buildRegistrationEmailTemplate()`, `buildPasswordResetEmailTemplate()`, `buildOrderConfirmationEmailTemplate()`, `buildErrorEmailTemplate()`: Constructs HTML content for various email types.
-   **`OrderService.java` (interface) / `OrderServiceImpl.java`**:
    -   `createOrder()`: Creates an order from a user's cart, updates product quantities, clears the cart, and sends a confirmation email. Transactional.
    -   `getAllOrders()`: Retrieves all orders.
    -   `getOrderByOrderId()`: Retrieves an order by its ID.
    -   `getUserOrders()`: Retrieves orders for a specific user.
    -   `updateOrderStatus()`: Updates the status of an order.
-   **`ProductService.java` (interface) / `ProductServiceImpl.java`**:
    -   `createProduct()`: Creates a new product, handles image file saving.
    -   `updateProduct()`: Updates an existing product, handles image file saving.
    -   `deleteProduct()`: Deletes a product.
    -   `getProductById()`: Retrieves a product by ID.
    -   `getAllProducts()`: Retrieves all products.
    -   `saveProductImage()`: Private helper to save uploaded product images to the filesystem.
-   **`TokenBlackListService.java`**:
    -   `addTokenToBlackList()`: Adds a token to an in-memory set of blacklisted tokens.
    -   `isTokenIsBlockListed()`: Checks if a token is in the blacklist.
-   **`UserService.java` (interface) / `UserServiceImpl.java`**:
    -   `registerUser()`: Registers a new user, generates a temporary password, encodes it, and sends a registration email. Validates if user already exists and if the role is valid.
    -   `changePassword()`: Changes a user's password after verifying the current one.
    -   `getUserByEmail()`: Retrieves a user by their email.
    -   `generateRandomPassword()`: Private helper to create a random temporary password.

### Utilities (`com.threadvine.util`)

-   **`JwtUtil.java`**:
    -   Utility class for handling JWT operations.
    -   `extractClaim()`, `extractAllClaims()`: Extracts claims from a token.
    -   `createToken()`: Generates a new JWT.
    -   `isTokenExpired()`: Checks if a token is expired.
    -   `extractExpiration()`, `extractUsername()`: Extracts specific fields from a token. Reads JWT secret from application properties.
