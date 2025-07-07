# Bookstore API

## ✨ Project Overview

Welcome to Bookstore, my personal project designed to explore and implement a variety of `Java` ecosystem technologies in one practical application. The project is a fully functional bookstore API built with `Spring Boot` and backed by modern practices like `JWT Authentication`, `Spring Security`, and `Docker`. It was developed not only to solve the core needs of a typical e-commerce application but also as a platform to test and integrate new tech skills.

With this project, I’ve created a simple yet powerful system for handling user authentication, book management, and order processing, all wrapped in a containerized solution for easy deployment.

## 🛠️ Technological Foundations

- [Java 17 ](https://www.oracle.com/ua/java/): The latest long-term support version of Java, ensuring compatibility and stability.
- [Spring Boot 3.5.0 ](https://spring.io/projects/spring-boot): Simplifies the development of production-ready applications by integrating common configuration and setup.
- [Spring Data JPA 3.5.0 ](https://spring.io/projects/spring-data-jpa): Provides data persistence capabilities, using Java Persistence API to connect seamlessly with the MySQL database.
- [Spring Security 6.5.0 ](https://spring.io/projects/spring-security): Handles authentication and authorization, ensuring secure access control.
- [MySQL 8.0.33 ](https://www.mysql.com): The database engine chosen for data storage and retrieval.
- [Docker 3.4.1 ](https://www.docker.com): Containerizes the application for consistent and efficient deployment across environments.
- [JUnit 5.12.2 ](https://junit.org): Used for running unit and integration tests.
- [Swagger 2.8.6 ](https://swagger.io): Automated API documentation for better collaboration and interaction with the API.
- [MapStruct 1.5.5 ](https://mapstruct.org): Used to map between DTOs and entities, promoting cleaner data flow.
- [AWS (EC2, ECR, RDS) ](https://aws.amazon.com): Deployed the application on AWS to allow real-world scaling and access.

## 🔧 Key Functionalities
#### The Bookstore API encompasses a range of features that create a fully functional online store:

##### 1. User Registration & Authentication:
- New users can register via `POST /auth/registration`, and existing users can log in with
  `POST /auth/login`.
- Upon successful login, a JWT token is issued for secure, role-based access to further resources.
- User roles are defined as `Admin` and `Customer`, with admins having more privileges.
##### 2. Book Management:
- Admins can create, update, and delete books through `POST /books`, `PUT /books/{id}`,
  and `DELETE /books/{id}`.
- Customers can search for books `GET /books`, `GET /search`, view details of a single book
  `GET /books/{id}`, and filter books using parameters.
##### 3. Category Management:
- Admins can manage categories via CRUD operations `POST /categories`, `GET /categories`,
  `PUT /categories/{id}`, `DELETE /categories/{id}`.
- Customers can view all categories `GET /categories` or search for books within a specific category (e.g., sci-fi books).
##### 4. Shopping Cart:
- A shopping cart is automatically assigned to each user upon registration.
- Users can view, add, modify, or delete items in their cart using `GET /cart`, `POST /cart`,
  `PUT /cart/items/{id}`, and `DELETE /cart/items/{id}`.
##### 5. Order Management:
- Customers can view and place orders `GET /orders`, `POST /orders`.
- Orders are linked to items in the cart, and users can view detailed information about their orders or specific order items.
- Admins can update order statuses, such as changing from “PENDING” to “DELIVERED”
  `PUT /orders/{id}`.

## ⚙️ Setting Up the Project Locally

##### To get started, follow these simple steps to build and run the Bookstore API on your local machine:

##### Before proceeding, ensure you have the following installed:
_____________________
> Java 17

>Maven

>MySQL

>Docker
______________________
##### Steps:

1. Clone the repository:

```sh
git clone https://github.com/sergiikonov/book-store
```
2. Configure Environment Variables:
> Create a `.env` file (a template is included as `.env.origin`).
3. Build and Run:
```sh
mvn clean install
docker-compose build
docker-compose up
```
4. Access Swagger UI Locally:
```sh
http://localhost:8088/swagger-ui/index.html#/
```

## 🌐 Production Version
#### You can also access the live version of the Bookstore API hosted on AWS:

### [Bookstore API](http://ec2-16-171-58-223.eu-north-1.compute.amazonaws.com/swagger-ui/index.html#/)

## 📭 Postman Collection
### I’ve also included a Postman collection for testing the API. Use this to quickly test the various endpoints:

- Locally: Run the app as described above, and send requests.
- On Prod: Replace ``http://localhost:8080`` with the production URL ``http://ec2-16-171-58-223.eu-north-1.compute.amazonaws.com/swagger-ui/index.html#/`` for your API requests.

#### [Download Postman Collection](https://sergii-838248.postman.co/workspace/Sergii's-Workspace~cc6177e6-e3a2-4ef1-8b2a-04bc61c70757/collection/43376300-dfdee9d0-7d5a-43d4-8c35-ca7e848cd6e4?action=share&creator=43376300)

## 🤝 Challenges and How I Overcame Them
##### Liquibase Setup:
[]()
>Setting up Liquibase and getting familiar with it was quite challenging. At first, everything seemed to work, but I kept running into errors during database migrations. After hours of troubleshooting and testing configurations, I realized that the root cause was incorrect structure in the migration files. Eventually, after properly setting up the correct versions and carefully configuring the changelog — everything started working smoothly.
##### Swagger and Browser Compatibility:
[]()
>The biggest headache came from Swagger. I spent hours trying to get it to work, but no matter what I did, it simply wouldn’t load. After countless attempts and running it in different browsers, I discovered that Swagger just didn’t work in Safari! In Chrome, it worked perfectly, but finding this out took a lot of time. So, if you're facing issues with Swagger, make sure to check your browser!
##### Spring Security Configuration:
[]()
>Configuring Spring Security turned out to be one of the biggest hurdles. At first glance, integrating JWT for authentication seemed straightforward, but in practice, there were plenty of unexpected issues, especially with security settings for certain routes. Once I started working with user roles and access configurations, I had to dive deep into CSRF and CORS settings. It turned out that the default configurations weren’t well-suited for modern SPA applications, so I spent a considerable amount of time adjusting the security filters to ensure proper access control.
## 🏁 Conclusion
The Bookstore API project allowed me to apply and consolidate my knowledge of modern Java technologies. By building this API, I learned how to integrate Spring Boot, Docker, AWS, and MapStruct in a real-world application. I hope this repository can be helpful to you if you're building a similar project or just want to explore how these technologies work together.

## 📧 Contact
##### Feel free to reach out with any questions or feedback:

Project Lead: Sergii Konovalov
Email: sergii2konovalov@gmail.com