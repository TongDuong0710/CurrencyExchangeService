# Exchange Rate Service
Java/Spring Boot microservice for managing currencies and exchange rates,
including scheduled synchronization with external providers (e.g., OANDA).\

## Directory structure
```text
project  
│
└───api  // # Presentation – REST Controllers, DTOs, Spring Boot configuration
│   └─── src
│   │   └─── main    
│   │   └─── test  // End-to-End & Integration Tests
│   │   pom.xml
└─── application  // # Application services (use cases) – orchestrate flow, validation, transactions, ports to domain
│   └─── src
│   │   pom.xml
└─── domain // # Core business rules – Entities/Aggregates, Value Objects, Domain services, Ports
│   └─── src 
│   │   pom.xml
└─── infra // # Infrastructure – JPA entities/repos, adapters implementing ports, external API clients
│   └─── src 
│   │   pom.xml
pom.xml
```

## Run locally
```bash
    .\mvnw.cmd -f .\api\pom.xml spring-boot:run
```

## Run with Docker compose
```bash
docker compose up -d --build
```

## Run with Docker
```bash
./mvnw -DskipTests package
docker build -t exchange-rate-app .
docker run --rm -p 8080:8080 exchange-rate-app
```

## Test (one command)
```bash
    ./mvnw clean test
```