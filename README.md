# 🏨 Ocean View Resort - Room Reservation System

> A comprehensive online reservation management system for Ocean View Resort, Galle, Sri Lanka

---

## About This Project

This web-based application streamlines the room booking process for Ocean View Resort, enabling efficient management of guest reservations, billing, and customer service operations.

### Academic Information

| Detail | Information |
|--------|------------|
| **Course Code** | CIS6003 - Advanced Programming |
| **Institution** | Cardiff Metropolitan University |
| **Developer** | D.I.Samaraweera |
| **Academic Year** | Final Year, First Semester, 2025 |

---

## Technology Stack

| Category | Technology |
|----------|-----------|
| **Language** | Java 11 |
| **Server** | Apache Tomcat 9.0 |
| **Database** | MySQL 8.0 |
| **Build Tool** | Maven |
| **Testing** | JUnit 4 |
| **Frontend** | JSP, HTML, CSS, JavaScript |
| **Version Control** | Git & GitHub |
| **CI/CD** | GitHub Actions |

---

## System Architecture

This application follows a **3-Tier Architecture**:
┌─────────────────────────────────┐
│ PRESENTATION LAYER │
│ JSP + HTML + CSS + JavaScript │
├─────────────────────────────────┤
│ BUSINESS LOGIC LAYER │
│ Services + Validation │
├─────────────────────────────────┤
│ DATA ACCESS LAYER │
│ DAO + JDBC + MySQL │
└─────────────────────────────────┘

text


### Design Patterns Implemented

| Pattern | Purpose |
|---------|---------|
| **MVC** | Separates data, logic, and presentation |
| **Singleton** | Ensures single database connection instance |
| **DAO** | Abstracts database operations |
| **Observer** | Handles email/SMS notifications |
| **Factory** | Creates different room type objects |

---

## System Features

### Core Functionalities
1. **User Authentication** - Secure login/logout with session management
2. **Reservation Management** - Create, view, modify, cancel bookings
3. **Billing System** - Automated bill calculation and invoice printing
4. **Reports** - Daily reservations, revenue, occupancy reports
5. **Help Section** - User guide for staff members

### Additional Features
- Dashboard with real-time statistics
- Room availability calendar
- Email/SMS notifications (simulated)
- Search and filter reservations
- Guest history tracking
- Responsive purple-themed UI

```
## Directory Structure
OceanViewResort/
├── .github/
│ └── workflows/
│ └── ci.yml
├── src/
│ ├── main/
│ │ ├── java/com/oceanview/
│ │ │ ├── model/
│ │ │ ├── dao/
│ │ │ ├── service/
│ │ │ ├── controller/
│ │ │ ├── filter/
│ │ │ └── listener/
│ │ └── webapp/
│ │ ├── WEB-INF/
│ │ ├── css/
│ │ ├── js/
│ │ ├── images/
│ │ └── *.jsp
│ └── test/
│ └── java/com/oceanview/
├── .gitignore
├── README.md
├── pom.xml
└── database_setup.sql

text


---

## Database Implementation

### Advanced Database Features

**Stored Procedures:**
- `GenerateReservationNumber` - Creates unique booking IDs
- `CalculateBill` - Computes total charges
- `GetReservationDetails` - Retrieves complete booking info

**MySQL Functions:**
- `GetAvailableRoomCount` - Returns number of vacant rooms
- `CalculateNights` - Calculates stay duration

**Database Triggers:**
- `before_reservation_insert` - Validates data before insertion
- `after_reservation_insert` - Post-booking operations
- `after_reservation_update` - Handles booking modifications

---

## Installation Guide

### Prerequisites
- Java JDK 11 or higher
- Apache Tomcat 9.0
- MySQL 8.0
- Apache Maven

### Setup Steps

**1. Clone Repository**
```bash
git clone https://github.com/DayaniSamaraweera/oceanview-resort-reservation.git
cd oceanview-resort-reservation
2. Database Setup

Bash

mysql -u root -p
source database_setup.sql
3. Update Database Credentials

Edit src/main/java/com/oceanview/dao/DBConnection.java:

Java

private static final String URL = "jdbc:mysql://localhost:3306/oceanview_resort";
private static final String USERNAME = "root";
private static final String PASSWORD = "your_password";
4. Build Project

Bash

mvn clean package
5. Deploy to Tomcat

Right-click project → Run As → Run on Server
Select Tomcat 9.0
Access: http://localhost:8080/OceanViewResort
Default Login:

text

Username: admin
Password: admin123
Testing
Test-Driven Development (TDD)
JUnit 4 unit tests
Automated test execution
Test coverage for business logic
Bash

mvn test
CI/CD Pipeline
GitHub Actions workflow automatically:

Builds the project using Maven
Runs all unit tests
Reports build status
License & Usage
This project is developed solely for academic purposes as part of the CIS6003 Advanced Programming module at Cardiff Metropolitan University.

Developer
D.I.Samaraweera
Cardiff Metropolitan University
CIS6003 - Advanced Programming
Final Year Project - 2025
