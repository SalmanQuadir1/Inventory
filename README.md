# Medical Store Inventory Management System

A production-ready comprehensive, full-stack Medical Store Inventory Management application built using modern tech stack.

## Tech Stack
* **Language:** Java 17
* **Framework:** Spring Boot 3.2+
* **Database:** MySQL
* **Frontend:** Thymeleaf + Tailwind CSS + Alpine.js
* **Security:** Spring Security (RBAC & BCrypt)

## Architecture
The application uses a standard N-Tier MVC architecture:
* **Controllers** - Handle HTTP requests and UI routing.
* **Services** - Contain the core business logic (Purchase, Sale, Stocks calculation).
* **Repositories** - Spring Data JPA interfaces for database interaction.
* **Entities** - Hibernate ORM models mapping to MySQL tables.

## Quick Start
1. Ensure you have **Java 17+**, **Maven**, and **MySQL** installed.
2. In MySQL, create a database named `medical_store`.
3. Open `src/main/resources/application.properties` and verify your `spring.datasource.username` and `password`.
4. The database tables will be initialized automatically due to `spring.jpa.hibernate.ddl-auto=update`.
5. Run the application from root directory:
   ```bash
   mvn spring-boot:run
   ```
6. Access the application at `http://localhost:8080/`.

## Default Credentials
The `data.sql` script loads the following sample users with password `admin`:
* **Admin:** `admin` / `admin`
* **Pharmacist:** `pharma` / `admin`
* **Staff:** `staff` / `admin`

## Core Features
* **Role Based Auth:** 3 distinct roles with dynamic UI visibilities.
* **Dashboard Stats:** Aggregate key metrics like low stocks, today's revenue.
* **Medicine Registry:** Track drugs with batch, expiry, pricing, and stock alerts.
* **Purchasing (PO):** Add multi-item purchase receipts, auto-adds to inventory.
* **Sales (Billing):** Point-of-Sale UI, adds bills, deducts inventory automatically.
* **Reporting Module:** Auto-detects expired formulas and low stocks.
