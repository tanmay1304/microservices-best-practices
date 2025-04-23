# Microservices Development Lifecycle Best Practices

This project demonstrates best practices in microservices development lifecycle, including CI/CD, unit testing, version control, and monitoring.

## Project Structure

```
microservices-best-practices/
├── .github/
│   └── workflows/
│       ├── ci.yml          # GitHub Actions CI workflow
│       └── cd.yml          # GitHub Actions CD workflow
├── product-service/        # Product Microservice
├── order-service/          # Order Microservice
├── docker/                 # Docker configurations
├── monitoring/             # Monitoring configurations
│   ├── prometheus.yml      # Prometheus config
│   └── grafana/            # Grafana dashboards and configs
├── docker-compose.yml      # Docker Compose file
└── pom.xml                 # Parent POM file
```

## Technologies Used

- Spring Boot - Microservices framework
- MongoDB - Database
- Docker - Containerization
- GitHub Actions - CI/CD
- JUnit & Mockito - Unit testing
- Prometheus & Grafana - Monitoring

## Best Practices Implemented

### 1. Microservices Architecture
- Separate services for products and orders
- Each service has its own database
- Services communicate via REST APIs

### 2. Version Control (Git/GitHub)
- Feature branches for development
- Pull requests for code reviews
- Protected main branch

### 3. CI/CD Pipeline (GitHub Actions)
- Automated testing on pull requests
- Code coverage reports
- Docker image builds
- Automated deployment

### 4. Unit Testing
- JUnit for testing framework
- Mockito for mocking dependencies
- Test coverage with JaCoCo

### 5. Containerization
- Docker for containerizing applications
- Docker Compose for local development
- Multi-stage builds for smaller images

### 6. Monitoring
- Prometheus for metrics collection
- Grafana for visualization
- Custom dashboards for service monitoring

## Getting Started

### Prerequisites
- Java 17
- Maven
- Docker & Docker Compose
- Git

### Running Locally

1. Clone the repository:
```bash
git clone https://github.com/yourusername/microservices-best-practices.git
cd microservices-best-practices
```

2. Start the application using Docker Compose:
```bash
docker-compose up -d
```

3. Access the services:
   - Product Service: http://localhost:8080/api/products
   - Order Service: http://localhost:8081/api/orders
   - Prometheus: http://localhost:9090
   - Grafana: http://localhost:3000 (admin/admin)

### Running Tests

To run tests for both services:
```bash
mvn clean test
```

## API Endpoints

### Product Service
- `GET /api/products` - List all products
- `GET /api/products/{id}` - Get product by ID
- `POST /api/products` - Create a new product

### Order Service
- `GET /api/orders` - List all orders
- `GET /api/orders/{id}` - Get order by ID
- `GET /api/orders/customer/{email}` - Get orders by customer email
- `POST /api/orders` - Create a new order

## Monitoring

The monitoring stack consists of Prometheus and Grafana:

1. Prometheus collects metrics from both microservices through Spring Boot Actuator endpoints
2. Grafana visualizes these metrics in customizable dashboards

Default Grafana dashboard includes:
- Request rates
- Response times
- Error rates
- System metrics (CPU, memory)

## Deployment

The project uses GitHub Actions for automated deployment:

1. Code is pushed to the main branch
2. CI workflow runs tests and builds the artifacts
3. CD workflow builds Docker images and pushes them to Docker Hub
4. Deployment is performed to the target environment

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -m 'Add some feature'`
4. Push to the branch: `git push origin feature/your-feature`
5. Submit a pull request