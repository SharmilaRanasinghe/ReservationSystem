Bus Reservation System
This is a Java-based Bus Reservation System that allows users to check seat availability and reserve tickets for bus routes. The system is built using Java Servlets and follows a modular, thread-safe design.

Features

Check Seat Availability: Users can check if seats are available for a specific route and date.
Reserve Tickets: Users can reserve seats for a specific route and date.
Thread-Safe Design: The system uses ConcurrentHashMap and synchronization to ensure thread safety.
Validation: All requests are validated to ensure they meet the required criteria.
Logging: Detailed logging is implemented for debugging and monitoring.
Technologies Used

Java: Core programming language.
Java Servlets: For handling HTTP requests and responses.
Jackson: For JSON serialization and deserialization.
SLF4J: For logging.
Maven: For dependency management and building the project.
Prerequisites

Before running the project, ensure you have the following installed:

Java Development Kit (JDK): Version 11 or higher.
Apache Tomcat: Version 9 or higher (or any other servlet container).
Maven: For building the project.
Setup Instructions

1. Clone the Repository

bash
Copy
git clone https://github.com/your-username/bus-reservation-system.git
cd bus-reservation-system
2. Build the Project

Use Maven to build the project:

bash
Copy
mvn clean install
This will compile the code and package it into a .war file.

3. Deploy the Application

Copy the generated .war file from the target directory to the webapps directory of your Tomcat server.
Start the Tomcat server.
bash
Copy
cp target/bus-reservation-system.war /path/to/tomcat/webapps/
/path/to/tomcat/bin/startup.sh
4. Access the Application

Once the server is running, you can access the application at:

Copy
http://localhost:8080/bus-reservation-system
API Endpoints

1. Check Seat Availability

Method: GET
Endpoint: /check-availability
Query Parameters:
origin: Origin location (e.g., "New York").
destination: Destination location (e.g., "Boston").
passengerCount: Number of passengers (e.g., 2).
travelDate: Travel date in yyyy-MM-dd format (e.g., "2023-12-25").
Example Request:

Copy
GET /check-availability?origin=New%20York&destination=Boston&passengerCount=2&travelDate=2023-12-25
Example Response:

json
Copy
{
    "seatAvailability": true,
    "pricingInfo": {
        "totalPrice": 100.0,
        "currency": "USD"
    }
}
2. Reserve Tickets

Method: POST
Endpoint: /reserve
Request Body (JSON):
json
Copy
{
    "origin": "New York",
    "destination": "Boston",
    "passengerCount": 2,
    "travelDate": "2023-12-25",
    "paymentAmount": 100.0
}
Example Response:

json
Copy
{
    "reservationId": "12345",
    "origin": "New York",
    "destination": "Boston",
    "allocatedSeatNumbers": ["A1", "A2"],
    "totalPrice": 100.0,
    "departureTime": "2023-12-25T10:00:00",
    "arrivalTime": "2023-12-25T14:00:00"
}
