# Load Testing in Java

A comprehensive load testing project for a Zomato-style restaurant API, built with Spring Boot and tested using Apache JMeter and Python async scripts.

## Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Load Testing Tools                     │
│                                                          │
│  ┌──────────────┐   ┌──────────────┐   ┌──────────────┐ │
│  │    JMeter    │   │   seed.py    │   │   Custom     │ │
│  │  (570 VUs)   │   │ (10K recs)  │   │   Scripts    │ │
│  └──────┬───────┘   └──────┬───────┘   └──────┬───────┘ │
└─────────┼──────────────────┼──────────────────┼─────────┘
          │                  │                  │
          ▼                  ▼                  ▼
┌─────────────────────────────────────────────────────────┐
│               Spring Boot REST API                       │
│                                                          │
│  ┌────────────────────────────────────────────────────┐  │
│  │  /api/restaurants                                  │  │
│  │  ├── GET    /api/restaurants          (list all)   │  │
│  │  ├── GET    /api/restaurants/{id}     (by ID)      │  │
│  │  ├── POST   /api/restaurants          (create)     │  │
│  │  ├── PUT    /api/restaurants/{id}     (update)     │  │
│  │  ├── DELETE /api/restaurants/{id}     (delete)     │  │
│  │  └── GET    /api/restaurants/search   (search)     │  │
│  └────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
          │
          ▼
┌─────────────────────────────────────────────────────────┐
│                    MySQL Database                        │
│                    zomato_db.restaurant                   │
└─────────────────────────────────────────────────────────┘
```

## Tech Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Language | Java | 17 |
| Framework | Spring Boot | 3.2.5 |
| Build Tool | Maven | - |
| Database | MySQL | - |
| DB Access | Spring JDBC (JdbcTemplate) | - |
| Load Testing | Apache JMeter | 5.6.3 |
| Data Seeding | Python (asyncio + aiohttp) | 3.8+ |

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/restaurants` | List all restaurants |
| `GET` | `/api/restaurants/{id}` | Get restaurant by ID |
| `POST` | `/api/restaurants` | Create a new restaurant |
| `PUT` | `/api/restaurants/{id}` | Update a restaurant |
| `DELETE` | `/api/restaurants/{id}` | Delete a restaurant |
| `GET` | `/api/restaurants/search?name=X` | Search restaurants by name |

## Database Schema

```sql
CREATE TABLE restaurant (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    address       VARCHAR(500),
    cuisine_type  VARCHAR(100),
    rating        DECIMAL(2,1) DEFAULT 0.0,
    phone         VARCHAR(20),
    is_active     BOOLEAN DEFAULT TRUE,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

## Load Test Configuration

The JMeter test plan (`zomato_load_test.jmx`) simulates **570 concurrent virtual users** across 4 thread groups:

| Thread Group | Virtual Users | Ramp-Up | Duration | Endpoint |
|--------------|---------------|---------|----------|----------|
| GET All Restaurants | 500 | 10s | 60s | `GET /api/restaurants` |
| GET Restaurant by ID | 30 | 10s | 60s | `GET /api/restaurants/{random_id}` |
| POST Create Restaurant | 20 | 10s | 60s | `POST /api/restaurants` |
| Search Restaurants | 20 | 10s | 60s | `GET /api/restaurants/search?name={term}` |

### Test Parameters

- **GET All**: Tests full table scan performance under heavy read load
- **GET by ID**: Random ID lookups (1-10,000) simulating user profile views
- **POST Create**: Random restaurant data generation with 20 cuisine types and Indian city addresses
- **Search**: LIKE-based queries using predefined restaurant name patterns

## Data Seeding

The `seed_data.py` script pre-loads the database with **10,000 restaurant records** before running load tests.

### Features
- Async HTTP requests with configurable concurrency (20 simultaneous connections)
- Batch processing (100 records per batch)
- Retry logic with exponential backoff (3 attempts)
- Real-time progress tracking with records/sec metrics
- Indian-themed restaurant data (cities, phone numbers, street names)

## Running the Load Tests

### 1. Start the Application
```bash
cd loadtesting
mvn spring-boot:run
```

### 2. Seed the Database
```bash
python seed_data.py
```

### 3. Run JMeter Tests
```bash
jmeter -n -t zomato_load_test.jmx -l results.jtl
```

### 4. View Results
- Open JMeter GUI and load `results.jtl`
- Or analyze the CSV output directly

## Project Structure

```
├── README.md
├── seed_data.py                 # Async data seeding script (10K records)
├── zomato_load_test.jmx         # JMeter test plan (570 VUs)
└── loadtesting/                 # Spring Boot application
    ├── pom.xml                  # Maven configuration
    └── src/main/
        ├── java/com/loadtesting/
        │   ├── App.java                 # Spring Boot entry point
        │   ├── AppConfig.java           # JDBC configuration
        │   ├── HelloController.java     # Auxiliary endpoint
        │   ├── Restaurant.java          # Restaurant entity model
        │   ├── RestaurantController.java # REST controller
        │   ├── RestaurantRepository.java # Data access layer
        │   └── RestaurantService.java    # Business logic
        └── resources/
            ├── application.properties   # Application config
            └── schema.sql               # Database DDL
```

## Expected Results

After running the load tests, you can expect:

| Metric | Value |
|--------|-------|
| Total Virtual Users | 570 |
| Test Duration | 60 seconds |
| Read:Write Ratio | ~25:1 (realistic read-heavy) |
| Search Queries | LIKE-based with random terms |
| Response Codes | 200 (GET), 201 (POST), 404 (invalid IDs) |

## License

MIT
