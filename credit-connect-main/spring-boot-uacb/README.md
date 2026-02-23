# Unified Academic Credit Bank (UACB)

A comprehensive academic credit management system built with Spring Boot for the Smart India Hackathon.

## 🎯 Overview

UACB is a centralized digital repository for storing, managing, and verifying academic credits across institutions. It enables students to accumulate credits from multiple institutions and provides a unified view of their academic achievements.

## ✨ Features

### For Students
- 📚 View academic credit wallet
- 📊 Track degree progress with visual indicators
- 📜 Access verified credit history and audit trail
- 👤 Manage profile information

### For Institutions
- 📖 Add and manage courses with credit values
- 🎓 Issue credits to students
- 📋 View all issued credits and their verification status
- ⚠️ Cannot modify verified credits (data integrity)

### For Administrators
- ✅ Approve/reject credit submissions
- 👥 View all students and institutions
- 📈 Analytics dashboard with system-wide statistics
- 📝 Complete audit trail of all activities

## 🛠️ Tech Stack

- **Backend**: Spring Boot 3.2 (Java 17)
- **Framework**: Spring MVC
- **ORM**: Hibernate/JPA
- **Security**: Spring Security (role-based access)
- **Database**: PostgreSQL
- **Frontend**: Thymeleaf + Bootstrap 5
- **Build Tool**: Maven

## 📦 Prerequisites

- Java 17 or higher
- Maven 3.8+
- PostgreSQL 14+

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/uacb.git
cd uacb
```

### 2. Configure Database

Create a PostgreSQL database:
```sql
CREATE DATABASE uacb_db;
```

### 3. Set Environment Variables

Create `application-local.properties` or set environment variables:
```properties
DATABASE_URL=jdbc:postgresql://localhost:5432/uacb_db
DB_USERNAME=postgres
DB_PASSWORD=your_password
```

### 4. Build and Run
```bash
mvn clean package
java -jar target/unified-academic-credit-bank-1.0.0.jar
```

Or run directly with Maven:
```bash
mvn spring-boot:run
```

### 5. Access the Application

Open http://localhost:8080 in your browser.

## 🔐 Default Login Credentials

After first run, sample data is created:

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@uacb.gov.in | admin123 |
| Institution | admin@iitd.ac.in | inst123 |
| Student | rahul@student.ac.in | student123 |

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/uacb/
│   │   ├── config/          # Security & app configuration
│   │   ├── controller/      # MVC Controllers & REST APIs
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── entity/          # JPA Entities
│   │   ├── repository/      # Spring Data repositories
│   │   └── service/         # Business logic
│   └── resources/
│       ├── templates/       # Thymeleaf templates
│       └── application.properties
```

## 🌐 Deployment on Render

### 1. Create a PostgreSQL Database on Render
- Go to Render Dashboard
- Create new PostgreSQL database
- Copy the External Database URL

### 2. Create Web Service
- Connect your GitHub repository
- Set build command: `mvn clean package -DskipTests`
- Set start command: `java -jar target/*.jar`

### 3. Environment Variables
```
DATABASE_URL=<your-render-postgres-external-url>
DB_USERNAME=<username>
DB_PASSWORD=<password>
SPRING_PROFILES_ACTIVE=prod
PORT=10000
```

## 🔒 Security Features

- BCrypt password encryption
- Role-based access control (STUDENT, INSTITUTION, ADMIN)
- CSRF protection
- Session management with timeout
- Complete audit logging

## 📊 Database Schema

### Core Entities
- **User**: Authentication and role management
- **StudentProfile**: Student information and credit tracking
- **Institution**: College/University details
- **Course**: Course catalog with credit values
- **CreditRecord**: Issued credits with verification workflow
- **DegreeProgram**: Degree requirements and progress tracking
- **AuditLog**: Complete activity trail

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## 📄 License

This project is created for the Smart India Hackathon prototype demonstration.

## 📞 Support

For questions or support, please open an issue in the GitHub repository.

---

**Built with ❤️ for Smart India Hackathon**
