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

## Multi-Store & Warehouse Management
The application features a robust **Multistore Architecture** designed for businesses with multiple physical locations:
*   **Warehouse Isolation:** Inventory, Sales, and Purchases are logically partitioned by `Warehouse`.
*   **User Assignment:** Administrators can assign operators (Pharmacists/Staff) to **multiple warehouses**.
*   **Automatic Filtering:** Once logged in, the system dynamically filters all views (Product list, POS, Reports) to show only data associated with the user's assigned locations.
*   **Stock Tracking:** Real-time stock levels are maintained per warehouse, allowing for accurate local inventory management.

## Optimized User Flow
If you are new to the system, follow this typical operational flow:

### 1. Administrative Setup (Admin Only)
*   **Configure Warehouses:** Go to `Warehouses` to define your physical store locations.
*   **Manage Users:** Create accounts for your team and assign them to specific `Warehouses`. This controls their data visibility.
*   **Suppliers:** Add your medicine providers in the `Suppliers` module.

### 2. Inventory & Procurement
*   **Product Registry:** Add medicines with batches, expiry dates, and categories.
*   **Purchase Orders:** Record new stock arrivals via `Purchases`. Select the target warehouse to automatically increment local stock levels.
*   **Excel Import:** Use the bulk upload feature for rapid initial inventory seeding.

### 3. Daily Operations (POS)
*   **Point of Sale:** Open the `POS` terminal. Search for products (filtered by your warehouse) and generate instant bills.
*   **Customer Management:** Track regular customers and their outstanding balances for credit sales.

### 4. Intelligence & Reporting
*   **Intelligence Center:** Monitor total portfolio valuation and health metrics (Low Stock/Expired).
*   **Enterprise Export:** Download professional **JasperReports Excel** sheets for auditing, sales registers, or customer ledgers.

## Quick Start
1. Ensure you have **Java 17+**, **Maven**, and **MySQL** installed.
2. In MySQL, create a database named `medical_store`.
3. Open `src/main/resources/application.properties` and verify your `spring.datasource.username` and `password`.
4. The database tables will be initialized automatically due to `spring.jpa.hibernate.ddl-auto=update`.
5. Run the application from root directory:
   ```bash
   mvn clean spring-boot:run
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
