# FitnessAI

[![Ask DeepWiki](https://devin.ai/assets/askdeepwiki.png)](https://deepwiki.com/sairamadepu13/fitnessAI)

FitnessAI is a comprehensive fitness tracking application built on a microservices architecture. It allows users to register, track their various fitness activities, and receive personalized, AI-generated recommendations to improve their performance and maintain safety.

## Architecture

The application is composed of several independent microservices that communicate with each other synchronously via REST APIs and asynchronously via a message broker.

-   **Eureka Server**: Handles service discovery, allowing services to find and communicate with each other without hardcoded URLs.
-   **Config Server**: A central place to manage external configuration for all services (though currently, services use local configuration).
-   **User Service**: Manages user-related data, including registration and profile information.
-   **Activity Service**: Responsible for tracking fitness activities like running, cycling, etc.
-   **AI Service**: Consumes activity data, interacts with the Google Gemini API to generate intelligent feedback, and provides recommendations to the user.

## How It Works

1.  A new user registers through the **User Service**.
2.  The user logs a new fitness activity (e.g., a 30-minute run) using the **Activity Service**.
3.  The **Activity Service** first validates the user's existence by calling the **User Service**.
4.  Upon successful validation, the activity is saved to a MongoDB database.
5.  The **Activity Service** then publishes the new activity's details to a RabbitMQ message queue.
6.  The **AI Service**, listening to the queue, consumes the new activity message.
7.  It constructs a detailed prompt with the activity data and sends it to the Google Gemini API for analysis.
8.  The **AI Service** parses the AI-generated response, formats it into a structured recommendation (including analysis, improvements, suggestions, and safety tips), and saves it to its own MongoDB database.
9.  The user can later retrieve their activity history from the **Activity Service** and their personalized recommendations from the **AI Service**.

## Technology Stack

-   **Backend**: Java 21, Spring Boot 3, Spring Cloud
-   **Service Discovery**: Spring Cloud Netflix Eureka
-   **Messaging**: RabbitMQ
-   **Databases**:
    -   PostgreSQL (for User Service)
    -   MongoDB (for Activity Service & AI Service)
-   **AI Integration**: Google Gemini API
-   **API Communication**: REST, WebClient (Reactive)
-   **Build Tool**: Maven

## Getting Started

### Prerequisites

-   Java 21
-   Maven
-   Docker and Docker Compose
-   A Google Gemini API Key


### Installation & Running

1.  **Clone the repository:**
    ```sh
    git clone https://github.com/sairamadepu13/fitnessAI.git
    cd fitnessAI
    ```

2.  **Start Dependencies:**
    You need instances of PostgreSQL, MongoDB, and RabbitMQ running. You can use Docker for this.

3.  **Run the Microservices:**
    The services must be started in a specific order. Open a new terminal for each service.

    a. **Eureka Server** (`eureka`)
    ```sh
    cd eureka
    ./mvnw spring-boot:run
    ```
    Wait until the server is running on port `8761`.

    b. **User Service** (`userservice `)
    ```sh
    cd ../userservice\ 
    ./mvnw spring-boot:run
    ```

    c. **Activity Service** (`activityservice`)
    ```sh
    cd ../activityservice
    ./mvnw spring-boot:run
    ```

    d. **AI Service** (`aiservice`)
    ```sh
    cd ../aiservice
    ./mvnw spring-boot:run
    ```

## API Endpoints

### User Service (`http://localhost:8081`)

-   `POST /api/users/register`: Register a new user.
    ```json
    {
      "email": "user@example.com",
      "password": "password123",
      "firstName": "John",
      "lastName": "Doe"
    }
    ```
-   `GET /api/users/{userId}`: Get a user's profile.
-   `GET /api/users/{userId}/validate`: Checks if a user exists and returns `true` or `false`.

### Activity Service (`http://localhost:8082`)

-   `POST /api/activities`: Track a new activity.
    ```json
    {
      "userId": "<user-id>",
      "type": "RUNNING",
      "duration": 30,
      "caloriesBurned": 300,
      "startTime": "2024-08-15T18:00:00",
      "additionalMetrics": {
        "distance": "5km",
        "avgPace": "6:00/km"
      }
    }
    ```
-   `GET /api/activities`: Get all activities for a specific user. (Requires `x_userId` header).
-   `GET /api/activities/{id}`: Get a single activity by its ID.

### AI Service (`http://localhost:8083`)

-   `GET /api/recommendations/user/{userId}`: Get all recommendations for a user.
-   `GET /api/recommendations/activity/{activityId}`: Get the specific recommendation for a given activity.
