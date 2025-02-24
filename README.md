# Bus Reservation System

A Java-based Bus Reservation System that allows users to check seat availability and reserve tickets for bus routes. The system is built using Java Servlets, ensuring a modular and thread-safe design.

## Features

- **Check Seat Availability**: Users can check if seats are available for a specific route and date.
- **Reserve Tickets**: Users can reserve seats for a specific route and date.
- **Thread-Safe Design**: Utilizes `ConcurrentHashMap` and synchronization to ensure thread safety.
- **Validation**: Ensures all requests meet required criteria.
- **Logging**: Detailed logging for debugging and monitoring.

## Technologies Used

- **Java**: Core programming language (Java 23).
- **Java Servlets**: For handling HTTP requests and responses.
- **Jackson**: For JSON serialization and deserialization.
- **SLF4J**: For logging.
- **Maven**: For dependency management and project building.

## Prerequisites

Before running the project, ensure you have the following installed:

- **Java Development Kit (JDK)**: Version 11 or higher.
- **Apache Tomcat**: Version 9 or higher (or any other servlet container).
- **Maven**: For building the project.

## Setup Instructions

### 1. Clone the Repository

Clone the repository to your local machine:

```bash
git clone https://github.com/your-username/bus-reservation-system.git
cd bus-reservation-system
```

### 2. Build the Project
Use Maven to build the project:
```bash
mvn clean install
```
This will compile the code and package it into a .war file.


### 3. Deploy the Application
Copy the generated .war file from the target directory to the webapps directory of your Tomcat server.
```bash
cp target/reservation-system.war /path/to/tomcat/webapps/
```
Start the Tomcat server:
```bash
/path/to/tomcat/bin/startup.sh
```

### 4.Access the Application
Once the server is running, you can access the application at:
```bash
http://localhost:8080/reservation-system
```
This will compile the code and package it into a .war file.

## API Endpoints

### 1. Check Seat Availability
   **Method: GET**
   - Endpoint: /check-availability
   - GET /check-availability?origin=New%20York&destination=Boston&passengerCount=2&travelDate=2023-12-25

### 2. Reserve Seats
**Method: POST**
- Endpoint: /reserve
- Request Body (JSON):
- `` {"origin": "A",
    "destination": "C",
    "passengerCount": 10,
    "paymentAmount": 300.00,
    "travelDate": "2025-02-25"
}
``
