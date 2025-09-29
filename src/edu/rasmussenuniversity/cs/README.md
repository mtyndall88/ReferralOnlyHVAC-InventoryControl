# Referral Only HVAC Inventory Control System

![Java](https://img.shields.io/badge/Java-17-blue) 
![MySQL](https://img.shields.io/badge/MySQL-8.0-orange) 
![IDE-Eclipse](https://img.shields.io/badge/IDE-Eclipse-purple) 
![License](https://img.shields.io/badge/License-MIT-green) 
![GitHub last commit](https://img.shields.io/github/last-commit/mtyndall88/referral-hvac-inventory) 
![Repo Size](https://img.shields.io/github/repo-size/mtyndall88/referral-hvac-inventory)

## 📌 Overview
The **Referral Only HVAC Inventory Control System** is a Java-based application built to streamline inventory management for Referral Only HVAC.  

- Developed in **Java** using **Eclipse IDE**  
- Database built on **MySQL 8.0**  
- Console-based, menu-driven interface  
- Uses **DAO (Data Access Object)** and **Singleton** design patterns  
- Version-controlled with **GitHub**  

---

## 🔐 Features
- **Authentication & Roles**
  - Register with unique username & strong password (validated with regex) (≥12 chars, uppercase, lowercase, number, special char).
  - Role-based access control (`ADMIN`, `USER`, `VIEWER`).

- **Product Management**
  - Add, update, search, and deactivate products (SKU-based).
  - Paginated view of active products.

- **Stock Control**
  - Atomic stock adjustments with reason + responsible user.
  - Full stock movement history retained.

- **Reports**
  - Low-stock report.
  - Audit log of product changes.

- **Logging**
  - Tracks system events, warnings, and errors.

---

## 🗂️ Project Structure
```text
COP3805C_25_SUMMER_T2/
│── src/edu/rasmussenuniversity/cs/
│   ├── Main.java          # Console entry point, menu loop
│   ├── Auth.java          # Registration & authentication
│   ├── User.java          # Authenticated user session
│   ├── Product.java       # DAO class for products
│   ├── Invoice.java       # DAO class for invoices
│   ├── Reports.java       # DAO class for reports
│   ├── DBConnection.java  # Singleton DB connection
│   ├── AppLogger.java     # Logging utility
│   ├── GUIExample.java    # Optional user interface demo
│── sql/
│   ├── schema.sql         # Database schema
│── README.md
```
---

## ⚙️ Installation & Setup

### 1. Clone Repository
```bash
git clone https://github.com/mtyndall88/referral-hvac-inventory.git
cd referral-hvac-inventory
```
### 2. Database Setup
```sql
CREATE DATABASE referral_inventory;
```
Then import schema:
```bash
mysql -u root -p referral_inventory < sql/schema.sql
```
### 3. Configure DB Connection
Edit DBConnection.java:
```java
private static final String URL = "jdbc:mysql://localhost:3306/referral_inventory";
private static final String USER = "root";
private static final String PASSWORD = "yourpassword";
```
### 4. Run Program
```bash
javac -d bin src/edu/rasmussenuniversity/cs/*.java
java -cp bin edu.rasmussenuniversity.cs.Main
```

---

## 🖥️ Usage
- On startup, register a new account or login.  
- Navigate using the menu options:  
  - **P** → Product Management  
  - **S** → Stock Adjustments  
  - **R** → Reports  
  - **Q** → Quit  

Role restrictions apply (e.g., `VIEWER` cannot edit products or stock).  

---

## ✅ Development Plan
- **Week 1** - Proposal, DB setup, GitHub repo
- **Week 2** - Product CRUD features
- **Week 3** - Logging system
- **Week 4** - Menu navigation & simulated reports
- **Week 5** - Database integration
- **Week 6** - Final testing & delivery

---

## 📖 References
- OWASP Authentication Standard: https://github.com/OWASP/ASVS/blob/master/4.0/en/0x11-V2-Authentication.md
- MySQL 8.0 Reference Manual: https://dev.mysql.com/doc/refman/8.0/en/atomic-ddl.html
- Java SE JDBC Documentation: https://docs.oracle.com/javase/tutorial/jdbc/basics/index.html
- Pro Git (Apress): https://git-scm.com/book/en/v2/Git-Basics-Recording-Changes-to-the-Repository
- Microservices Definition (Fowler & Lewis): https://martinfowler.com/articles/microservices.html
