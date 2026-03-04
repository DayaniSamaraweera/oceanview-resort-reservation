# Ocean View Resort - Online Room Reservation System

## Project Overview
A computerized room reservation system developed for **Ocean View Resort**, a popular
beachside hotel in **Galle, Sri Lanka**. This system replaces manual booking processes
to handle room reservations efficiently, eliminating booking conflicts and delays.

## Technology Stack
| Layer | Technology |
|-------|-----------|
| Backend | Java 17, Servlets, JDBC |
| Frontend | HTML5, CSS3, Vanilla JavaScript |
| Database | MySQL 8.0 |
| Server | Apache Tomcat 9 |
| Build | Maven |
| Testing | JUnit 4, Mockito |
| CI/CD | GitHub Actions |

## System Features
- User Authentication (Role-based: Admin and Receptionist)
- Add New Reservation
- Display Reservation Details
- Calculate and Print Bill
- Room Availability Management
- Decision-Making Reports
- Help Section
- Activity Audit Trail
- Email Notifications
- Exit System

## Architecture and Design Patterns
- **Architecture:** 3-Tier (Presentation, Business Logic, Data Access)
- **Design Patterns:** MVC, Singleton, DAO, Builder, Observer

## Room Categories
| Room Type | Rate Per Night (LKR) |
|-----------|---------------------|
| Standard | 5,500.00 |
| Superior | 8,500.00 |
| Premium | 13,000.00 |
| Executive | 22,000.00 |

## Setup Instructions
1. Run `database_setup.sql` in MySQL Workbench
2. Import project into Eclipse as Existing Maven Project
3. Configure Tomcat 9 server in Eclipse
4. Deploy and access at `http://localhost:8080/OceanViewResort`

## Default Login Credentials
| Role | Username | Password |
|------|----------|----------|
| Admin (Resort Manager) | admin | admin123 |
| Receptionist | reception1 | recep123 |

## Author
Dayani Samaraweera