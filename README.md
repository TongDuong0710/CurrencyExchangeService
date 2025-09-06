# Electronics Store Backend
Java/Spring Boot backend for a web-based electronics store checkout system.

## Directory structure
```text
project  
│
└───api  // # Presentation - Web Controller, DTO and SpringBoot configuration
│   └─── src
│   │   └─── main    
│   │   └─── test  // End To End Tests
│   │   pom.xml
└─── application  // # Application services (use cases) – orchestrate flow, validation, transactions, ports to domain
│   └─── src
│   │   pom.xml
└─── domain // # Enterprise business rules - Entities/Aggregates, Value Objects, Domain services, Ports
│   └─── src 
│   │   pom.xml
└─── infra // # Infrastructure – JPA entities/repos, adapters implement ports
│   └─── src 
│   │   pom.xml
pom.xml
```

## Run 
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
docker build -t electrostore-app .
docker run --rm -p 8080:8080 electrostore-app
```

## Test (one command)
```bash
    ./mvnw -pl api test
```

## Test a specific test class/method
````bash
    ./mvnw -pl api test -Dtest=ConcurrentAddStockTests
```