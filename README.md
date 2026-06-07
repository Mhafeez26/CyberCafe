# CyberCafe Management System

A desktop-based **Cyber Cafe Management System** built with **Java Swing** and **MySQL**. This application helps cafe owners manage computers, customers, sessions, billing, reservations, employees, and reports — all from a single clean interface.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java (JDK 17+) |
| UI Framework | Java Swing + [FlatLaf 3.7.1](https://www.formdev.com/flatlaf/) |
| Database | MySQL |
| JDBC Driver | MySQL Connector/J 9.4.0 |
| IDE | IntelliJ IDEA |

---

## Features

### Authentication
- Role-based login system (Admin / Employee)
- Secure session management

### Computer Management
- Add, edit, and delete computers
- Assign computers to categories (Normal, Gaming, VIP)
- Real-time status tracking (Available / Occupied)

### Customer Management
- Register and manage customers
- Membership tiers: None, Silver, Gold, Platinum
- Automatic discount application based on membership

### Session Management
- Start and monitor active sessions
- Walk-in and registered customer sessions
- Live session duration timer
- Reservation conflict detection (reserved PCs blocked for walk-ins)

### Billing
- Automatic bill calculation (hourly rate × duration)
- Membership discounts and tax applied automatically
- Reservation surcharge support
- Printable receipts with customizable footer

### Reservations
- Book computers for specific time slots
- Conflict-free scheduling (no double booking)
- Auto-complete on session end

### ‍ Employee Management
- Add, edit, and remove employee records
- Store position, salary, phone, and linked user account

### Reports
- Daily revenue report (last 30 days)
- Session history (last 100 sessions)
- Customer spending report
- PC usage and revenue breakdown per computer

### Settings
- Customize system name, currency, tax rate, and receipt footer

---

## Project Structure

```
CyberCafe/
├── src/
│ ├── Main.java # Entry point
│ ├── db/
│ │ └── DBConnection.java # Singleton DB connection
│ ├── backend/
│ │ ├── LoginBackend.java
│ │ ├── DashboardBackend.java
│ │ ├── ComputerBackend.java
│ │ ├── CustomerBackend.java
│ │ ├── SessionBackend.java
│ │ ├── BillingBackend.java
│ │ ├── ReservationBackend.java
│ │ ├── EmployeeBackend.java
│ │ ├── ReportsBackend.java
│ │ └── SettingsBackend.java
│ ├── frontend/
│ │ ├── LoginFrame.java
│ │ ├── DashboardFrame.java
│ │ ├── ComputerPanel.java
│ │ ├── CustomerPanel.java
│ │ ├── SessionPanel.java
│ │ ├── BillingPanel.java
│ │ ├── ReservationPanel.java
│ │ ├── EmployeePanel.java
│ │ ├── ReportsPanel.java
│ │ └── SettingsPanel.java
│ ├── factory/
│ │ └── PanelFactory.java # Factory Pattern
│ ├── observer/
│ │ ├── SessionObserver.java # Observer interface
│ │ └── SessionEventPublisher.java
│ ├── uifactory/
│ │ ├── UIFactory.java
│ │ └── UIConstants.java
│ └── util/
│ └── ExceptionHandler.java
├── sql/
│ └── schema.sql # Full DB schema + seed data
└── library/
├── flatlaf-3.7.1.jar
└── mysql-connector-j-9.4.0.jar
```

---

## Design Patterns Used

| Pattern | Where Used |
|---|---|
| **Singleton** | `DBConnection` — single shared DB connection throughout the app |
| **Factory** | `PanelFactory` — creates UI panels dynamically by name |
| **Observer** | `SessionEventPublisher` + `SessionObserver` — notifies listeners on session start/end |

---

## Database Schema

The database includes the following tables:

`roles` · `users` · `computer_categories` · `computers` · `games` · `memberships` · `customers` · `sessions` · `reservations` · `employees` · `settings`

**Default seed data is included:**
- Admin user: `admin` / `admin`
- Computer categories: Normal (PKR 50/hr), Gaming (PKR 100/hr), VIP (PKR 150/hr)
- Membership tiers: None (0%), Silver (10%), Gold (20%), Platinum (30%)
- Tax: 5% · Currency: PKR

---

## Getting Started

### Prerequisites

- Java JDK 17 or higher
- MySQL Server 8.0+
- IntelliJ IDEA (recommended)

### Setup

**1. Clone the repository**
```bash
git clone https://github.com/your-username/CyberCafe.git
cd CyberCafe
```

**2. Set up the database**
```bash
mysql -u root -p < sql/schema.sql
```

**3. Configure DB credentials**

Open `src/db/DBConnection.java` and update:
```java
private static final String URL = "jdbc:mysql://localhost:3306/cybercafe";
private static final String USER = "root";
private static final String PASSWORD = "your_password";
```

**4. Add libraries to your project**

Add both JARs from the `library/` folder to your project's classpath:
- `flatlaf-3.7.1.jar`
- `mysql-connector-j-9.4.0.jar`

**5. Run the application**

Run `Main.java`. The login window will open.

**Default credentials:**
```
Username: admin
Password: admin
```

---

## Requirements

- JDK 17+
- MySQL 8.0+
- Minimum screen resolution: 1280 × 720

---

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

---

## License

This project is open source and available under the [MIT License](LICENSE).
