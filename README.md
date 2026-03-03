# Ocean View Resort - Online Room Reservation System

A professional web-based hotel room reservation system developed for 
Ocean View Resort, Galle, Sri Lanka.

---

## Project Information

| Item | Details |
|------|---------|
| **Module** | CIS6003 - Advanced Programming |
| **University** | Cardiff Metropolitan University |
| **Student** | D.S. Samaraweera |
| **Year** | Final Year - Semester 1 |
| **System** | Online Room Reservation System |

---

## Technology Stack

| Layer | Technology |
|-------|------------|
| **Language** | Java 17 |
| **Server** | Apache Tomcat 9 |
| **Database** | MySQL 8.0 |
| **Build Tool** | Maven |
| **IDE** | Eclipse |
| **Testing** | JUnit 4 + Mockito |
| **Version Control** | Git & GitHub |
| **CI/CD** | GitHub Actions |

---

## Architecture

- **3-Tier Architecture** (Presentation, Business Logic, Data Access)
- **Design Patterns:**
  - MVC (Model-View-Controller)
  - Singleton (DatabaseManager)
  - DAO (Data Access Object)
  - Factory Pattern (DAOProvider)
  - Observer Pattern (SystemStartupListener)

---

## System Features

| Feature | Description |
|---------|-------------|
| **User Authentication** | Secure login/logout with session management |
| **Add Reservation** | Register guests with full booking details |
| **View Reservation** | Display complete booking information |
| **Calculate & Print Bill** | Auto-calculate total stay cost |
| **Help Section** | User guide for new staff members |
| **Exit System** | Secure logout functionality |
| **Reports** | Management reports for decision making |
| **Email Notifications** | Automatic confirmation emails |

```
## Project Structure
oceanview-resort-reservation/
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
│ │ ├── resources/
│ │ └── webapp/
│ │ ├── WEB-INF/
│ │ ├── css/
│ │ ├── js/
│ │ └── *.jsp
│ └── test/
│ └── java/com/oceanview/
├── .gitignore
├── README.md
├── pom.xml
└── database_setup.sql




---

## 🗄️ Database Features

| Feature | Details |
|---------|---------|
| **Tables** | users, rooms, reservations, bills, email_logs |
| **Stored Procedures** | GenerateReservationNumber, CalculateBill, GetReservationDetails |
| **Functions** | GetAvailableRoomCount |
| **Triggers** | before_reservation_insert, after_reservation_insert, after_reservation_update |

---

## Setup Instructions

### Prerequisites
- Java JDK 17
- Apache Tomcat 9
- MySQL 8.0
- Maven 3.x
- Eclipse IDE

### Installation Steps

**1. Clone the repository:**
```bash
git clone https://github.com/DayaniSamaraweera/oceanview-resort-reservation.git
2. Import into Eclipse:

File → Import → Maven → Existing Maven Projects
Browse to cloned folder → Finish
3. Setup MySQL Database:

Bash

mysql -u root -p < database_setup.sql
4. Update database credentials:

Open src/main/java/com/oceanview/dao/DatabaseManager.java
Update USERNAME and PASSWORD
5. Deploy to Tomcat:

Right click project → Run As → Run on Server
Select Tomcat 9 → Finish
6. Access System:



http://localhost:8080/OceanViewResort/
### Default Login Credentials
| Role | Username |
|------|----------|
| Admin | manager |
| Receptionist | staff01 |


🧪 Testing
Approach: Test-Driven Development (TDD)
Framework: JUnit 4 + Mockito
Test Classes: InputValidatorTest, BookingServiceTest, RoomManagerTest, StaffServiceTest, ReservationModelTest, UserDAOTest

Run tests:

Bash
mvn test
🔄 CI/CD Pipeline
GitHub Actions workflow configured
Automatic build and test on every push
Pipeline file: .github/workflows/ci.yml

👩‍💻 Developer
D.S. Samaraweera
Cardiff Metropolitan University
Final Year - Software Engineering

📄 License
This project is developed for academic purposes only.

© 2025 Ocean View Resort. All Rights Reserved.