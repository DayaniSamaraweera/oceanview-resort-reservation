# Ocean View Resort - Room Reservation System

> A comprehensive online reservation management system for Ocean View Resort, Galle, Sri Lanka

---

## About This Project

This web-based application streamlines the room booking process for Ocean View Resort, enabling efficient management of guest reservations, billing, and customer service operations.

### Academic Information

- **Course Code:** CIS6003 - Advanced Programming
- **Institution:** Cardiff Metropolitan University
- **Developer:** D.I.Samaraweera
- **Academic Year:** Final Year, First Semester

---

## Technical Implementation

### Core Technologies

**Backend Development**
- Java 11
- Apache Tomcat 9.0
- JDBC for database connectivity

**Database Management**
- MySQL 8.0
- Stored Procedures & Functions
- Database Triggers

**Development Tools**
- Eclipse IDE
- Maven Build Tool
- JUnit 4 Testing Framework

**DevOps**
- Git Version Control
- GitHub Repository
- CI/CD with GitHub Actions

---

## System Architecture

### Architectural Pattern
This application follows a **3-Tier Architecture**:

1. **Presentation Layer** - User Interface (JSP, HTML, CSS, JavaScript)
2. **Business Logic Layer** - Application Services and Controllers
3. **Data Access Layer** - DAO Pattern with Database Integration

### Design Patterns Implemented

| Pattern | Purpose |
|---------|---------|
| **MVC** | Separates data, logic, and presentation |
| **Singleton** | Ensures single database connection instance |
| **DAO** | Abstracts database operations |

---

## System Features

### Core Functionalities

1. **User Authentication System**
   - Secure login/logout mechanism
   - Session management

2. **Reservation Management**
   - Create new bookings
   - Store guest information (name, address, contact)
   - Select room types and dates

3. **Information Display**
   - View detailed reservation information
   - Search and filter bookings

4. **Billing System**
   - Automated bill calculation
   - Print bill functionality

5. **Help & Support**
   - User guide for new staff members
   - System documentation

6. **System Controls**
   - Safe exit mechanism

---

## 📂 Directory Structure
oceanview-resort-reservation/
│
├── .github/
│ └── workflows/
│ └── ci.yml # CI/CD Pipeline
│
├── src/
│ ├── main/
│ │ ├── java/
│ │ │ └── com/oceanview/
│ │ │ ├── model/ # Entity classes
│ │ │ ├── dao/ # Data Access Objects
│ │ │ ├── service/ # Business logic
│ │ │ ├── controller/ # Servlets
│ │ │ ├── filter/ # Authentication filter
│ │ │ └── listener/ # Application listeners
│ │ │
│ │ ├── resources/ # Configuration files
│ │ │
│ │ └── webapp/
│ │ ├── WEB-INF/
│ │ ├── css/ # Stylesheets
│ │ ├── js/ # JavaScript files
│ │ └── *.jsp # JSP pages
│ │
│ └── test/
│ └── java/com/oceanview/ # JUnit test classes
│
├── .gitignore
├── README.md
├── pom.xml # Maven configuration
└── database_setup.sql # Database schema & data

text


---

## Database Implementation

### Advanced Database Features

**Stored Procedures:**
- `GenerateReservationNumber` - Creates unique booking IDs
- `CalculateBill` - Computes total charges based on stay duration
- `GetReservationDetails` - Retrieves complete booking information

**MySQL Functions:**
- `GetAvailableRoomCount` - Returns number of vacant rooms

**Database Triggers:**
- `after_reservation_insert` - Post-booking operations
- `after_reservation_update` - Handles booking modifications
- `before_reservation_insert` - Validates data before insertion

---

## Installation Guide

### System Requirements

Ensure the following are installed:
- Java Development Kit (JDK) 11 or higher
- Apache Tomcat Server 9.0
- MySQL Database Server 8.0
- Apache Maven

### Setup Steps

**1. Clone Repository**
```bash
git clone https://github.com/DayaniSamaraweera/oceanview-resort-reservation.git
cd oceanview-resort-reservation

2. Database Configuration

Bash

mysql -u root -p
source database_setup.sql
3. Update Database Credentials
Edit src/main/java/com/oceanview/dao/DBConnection.java:

Java

private static final String DB_URL = "jdbc:mysql://localhost:3306/oceanview_db";
private static final String USER = "your_username";
private static final String PASS = "your_password";

4. Import to Eclipse

Open Eclipse IDE
File → Import → Existing Maven Projects
Select project folder
Build project
Deploy to Tomcat

Right-click project → Run As → Run on Server
Select Tomcat 9.0
Application runs on http://localhost:8080/OceanViewResort

Testing Methodology
Test-Driven Development Approach
This project implements TDD (Test-Driven Development) methodology:

Comprehensive unit tests using JUnit 4
Automated test execution
Test coverage for critical business logic
Integration testing for database operations

Running Tests:
Bash
mvn test

License & Usage
This project is developed solely for academic purposes as part of the CIS6003 module at Cardiff Metropolitan University.

Developer
Dayani Imasha Samaraweera
Cardiff Metropolitan University
CIS6003 - Advanced Programming
Final Year Project