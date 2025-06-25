# FC Serverless Framework

Revolutionary **Serverless by Design Programming** approach that enables developers to write pure business logic while the framework handles all cross-cutting concerns through transparent HTTP proxies.

## 🚀 What is FC Serverless Framework?

FC (FuturelyConcept) Serverless Framework transforms how you build distributed serverless applications by:

- ✅ **Pure Business Logic** - Write standard `java.util.Function` components with zero framework pollution
- ✅ **Transparent Cross-Service Calls** - Framework automatically creates HTTP proxies for `@RemoteFunction` annotated dependencies
- ✅ **Zero Learning Curve** - Uses familiar Spring Boot patterns and annotations
- ✅ **Local Development** - Same code works locally and in distributed Lambda environment
- ✅ **Type Safety** - Full compile-time validation and IDE support
- ✅ **AWS Lambda Ready** - Built on Spring Cloud Function for seamless cloud deployment

## 🎯 The Problem We Solve

Traditional serverless development forces you to think about infrastructure concerns in your business logic:

| Traditional Serverless | FC Framework |
|------------------------|--------------|
| ❌ Manual HTTP clients and URL management | ✅ Transparent `@RemoteFunction` proxies |
| ❌ No compile-time validation between services | ✅ Full type safety with IDE autocomplete |
| ❌ Different code for local vs cloud environments | ✅ Same code works everywhere |
| ❌ Framework-specific learning curve | ✅ Standard Spring Boot patterns |
| ❌ Hard to test cross-service interactions | ✅ Standard Mockito testing |

## ⚡ Quick Start

### 1. Clone and Build

```bash
git clone https://github.com/FuturelyConcept/fc-serverless-framework.git
cd fc-serverless-framework
mvn clean install
```

### 2. Run Sample Application Locally

```bash
# Terminal 1 - User Validator (Port 8082)
cd fc-serverless-sample/sample-aws-uservalidator
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Terminal 2 - Inventory Checker (Port 8083)
cd fc-serverless-sample/sample-aws-inventorychecker
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Terminal 3 - Payment Processor (Port 8084)
cd fc-serverless-sample/sample-aws-paymentprocessor
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Terminal 4 - Order Processor (Port 8081)
cd fc-serverless-sample/sample-aws-orderprocessor
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### 3. Test the Complete Flow

```bash
curl -X POST http://localhost:8081/orderProcessor \
  -H "Content-Type: application/json" \
  -d '{
    "userData": {
      "name": "John Doe",
      "email": "john@example.com",
      "age": 25
    },
    "productId": "product-123",
    "quantity": 50,
    "paymentMethod": "CREDIT_CARD"
  }'
```

Expected response:
```json
{
  "success": true,
  "orderId": "order-1234567890",
  "message": "Order processed successfully"
}
```

## 🧩 Core Concepts

### 1. Pure Business Functions

Write clean business logic with zero framework dependencies:

```java
@Component
public class UserValidatorFunction implements Function<UserData, ValidationResult> {
    @Override
    public ValidationResult apply(UserData userData) {
        // Pure business logic - no HTTP, no URLs, no framework clutter
        if (userData.getAge() < 18) {
            return ValidationResult.invalid("Must be 18 or older");
        }
        if (!userData.getEmail().contains("@")) {
            return ValidationResult.invalid("Invalid email format");
        }
        return ValidationResult.valid("User data is valid");
    }
}
```

### 2. Transparent Cross-Service Communication

Use `@RemoteFunction` to automatically create HTTP proxies to other services:

```java
@Component
public class OrderProcessorFunction implements Function<CreateOrderRequest, OrderResult> {

    // FC Framework automatically creates HTTP proxies for these!
    @RemoteFunction(name = "userValidator")
    private Function<UserData, ValidationResult> userValidator;

    @RemoteFunction(name = "inventoryChecker")
    private Function<InventoryCheckRequest, InventoryResult> inventoryChecker;

    @RemoteFunction(name = "paymentProcessor")
    private Function<PaymentRequest, PaymentResult> paymentProcessor;

    @Override
    public OrderResult apply(CreateOrderRequest request) {
        // These calls automatically become HTTP requests to other Lambdas!
        ValidationResult validation = userValidator.apply(request.getUserData());
        if (!validation.isValid()) {
            return OrderResult.failed("User validation failed: " + validation.getMessage());
        }

        InventoryResult inventory = inventoryChecker.apply(
            new InventoryCheckRequest(request.getProductId(), request.getQuantity())
        );
        if (!inventory.isAvailable()) {
            return OrderResult.failed("Inventory check failed: " + inventory.getMessage());
        }

        PaymentResult payment = paymentProcessor.apply(
            new PaymentRequest(request.getUserData().getName(), 
                              calculateTotal(request), "USD", request.getPaymentMethod())
        );
        if (!payment.isSuccess()) {
            return OrderResult.failed("Payment failed: " + payment.getMessage());
        }

        return OrderResult.success(generateOrderId());
    }
}
```

### 3. Configuration-Based URL Resolution

Environment-specific configuration in `application.yml`:

```yaml
# Local development
userValidator:
  url: http://localhost:8082/userValidator

inventoryChecker:
  url: http://localhost:8083/inventoryChecker

paymentProcessor:
  url: http://localhost:8084/paymentProcessor

# AWS Lambda (environment variables)
# FC_LAMBDA_URL_USERVALIDATOR=https://xyz.lambda-url.us-east-1.on.aws/
# FC_LAMBDA_URL_INVENTORYCHECKER=https://abc.lambda-url.us-east-1.on.aws/
# FC_LAMBDA_URL_PAYMENTPROCESSOR=https://def.lambda-url.us-east-1.on.aws/
```

## 🏗️ Architecture

```
┌─────────────────┐    HTTP     ┌─────────────────┐
│   Order         │────────────▶│  User           │
│   Processor     │             │  Validator      │
│   Lambda        │             │  Lambda         │
└─────────────────┘             └─────────────────┘
         │                               
         │ HTTP                          
         ▼                               
┌─────────────────┐    HTTP     ┌─────────────────┐
│   Inventory     │────────────▶│  Payment        │
│   Checker       │             │  Processor      │
│   Lambda        │             │  Lambda         │
└─────────────────┘             └─────────────────┘
```

## 📁 Project Structure

```
fc-serverless-framework/
├── fc-serverless-core/           # Core framework components
│   └── annotation/
│       └── RemoteFunction.java   # @RemoteFunction annotation
├── fc-serverless-starter/        # Auto-configuration and proxy factory
│   ├── config/
│   │   ├── RemoteFunctionAutoConfiguration.java
│   │   ├── RemoteFunctionBeanPostProcessor.java
│   │   └── EnvironmentPropertyResolver.java
│   └── proxy/
│       └── RemoteFunctionProxyFactory.java
└── fc-serverless-sample/         # Complete sample application
    ├── fc-serverless-sample-domain/     # Shared domain objects
    ├── sample-aws-uservalidator/        # User validation microservice
    ├── sample-aws-inventorychecker/     # Inventory checking microservice
    ├── sample-aws-paymentprocessor/     # Payment processing microservice
    └── sample-aws-orderprocessor/       # Order orchestration microservice
```

## 🚀 Deployment to AWS Lambda

### 1. Build Deployment Packages

```bash
# Build all functions
mvn clean package

# Each function creates a deployment JAR in target/
ls fc-serverless-sample/sample-aws-*/target/*.jar
```

### 2. Deploy Functions (Example using AWS CLI)

```bash
# Deploy User Validator
aws lambda update-function-code \
  --function-name fc-uservalidator \
  --zip-file fileb://fc-serverless-sample/sample-aws-uservalidator/target/sample-aws-uservalidator-1.0-SNAPSHOT.jar

# Deploy Inventory Checker
aws lambda update-function-code \
  --function-name fc-inventorychecker \
  --zip-file fileb://fc-serverless-sample/sample-aws-inventorychecker/target/sample-aws-inventorychecker-1.0-SNAPSHOT.jar

# Deploy Payment Processor
aws lambda update-function-code \
  --function-name fc-paymentprocessor \
  --zip-file fileb://fc-serverless-sample/sample-aws-paymentprocessor/target/sample-aws-paymentprocessor-1.0-SNAPSHOT.jar

# Deploy Order Processor
aws lambda update-function-code \
  --function-name fc-orderprocessor \
  --zip-file fileb://fc-serverless-sample/sample-aws-orderprocessor/target/sample-aws-orderprocessor-1.0-SNAPSHOT.jar
```

### 3. Configure Lambda URLs

Each Lambda function needs a Function URL for HTTP access:

```bash
# Create function URLs for each Lambda
aws lambda create-function-url-config \
  --function-name fc-uservalidator \
  --auth-type NONE \
  --cors '{"AllowMethods":["POST"],"AllowOrigins":["*"]}'

aws lambda create-function-url-config \
  --function-name fc-inventorychecker \
  --auth-type NONE \
  --cors '{"AllowMethods":["POST"],"AllowOrigins":["*"]}'

aws lambda create-function-url-config \
  --function-name fc-paymentprocessor \
  --auth-type NONE \
  --cors '{"AllowMethods":["POST"],"AllowOrigins":["*"]}'

aws lambda create-function-url-config \
  --function-name fc-orderprocessor \
  --auth-type NONE \
  --cors '{"AllowMethods":["POST"],"AllowOrigins":["*"]}'
```

### 4. Set Environment Variables

Update the Order Processor Lambda with URLs of other functions:

```bash
aws lambda update-function-configuration \
  --function-name fc-orderprocessor \
  --environment Variables='{
    "FC_LAMBDA_URL_USERVALIDATOR":"https://xyz.lambda-url.us-east-1.on.aws/",
    "FC_LAMBDA_URL_INVENTORYCHECKER":"https://abc.lambda-url.us-east-1.on.aws/",
    "FC_LAMBDA_URL_PAYMENTPROCESSOR":"https://def.lambda-url.us-east-1.on.aws/"
  }'
```

## 🧪 Testing

### Unit Tests

Test individual functions with standard mocking:

```java
@ExtendWith(MockitoExtension.class)
class OrderProcessorTest {
    
    @Mock
    private Function<UserData, ValidationResult> userValidator;
    
    @Mock
    private Function<InventoryCheckRequest, InventoryResult> inventoryChecker;
    
    @Mock
    private Function<PaymentRequest, PaymentResult> paymentProcessor;
    
    @InjectMocks
    private OrderProcessorFunction orderProcessor;
    
    @Test
    void shouldProcessValidOrder() {
        // Given
        when(userValidator.apply(any())).thenReturn(ValidationResult.valid("OK"));
        when(inventoryChecker.apply(any())).thenReturn(InventoryResult.available(100));
        when(paymentProcessor.apply(any())).thenReturn(PaymentResult.success("txn-123"));
        
        // When
        OrderResult result = orderProcessor.apply(createValidOrderRequest());
        
        // Then
        assertThat(result.isSuccess()).isTrue();
        verify(userValidator).apply(any());
        verify(inventoryChecker).apply(any());
        verify(paymentProcessor).apply(any());
    }
}
```

### Integration Tests

Run the complete flow locally:

```bash
# Start all services and run integration tests
./scripts/run-integration-tests.sh
```

## 🔧 Configuration

### Application Properties

Each microservice can be configured independently:

```yaml
server:
  port: ${PORT:8081}

spring:
  application:
    name: fc-orderprocessor
  cloud:
    function:
      definition: orderProcessor

# Remote function URLs
userValidator:
  url: ${FC_LAMBDA_URL_USERVALIDATOR:http://localhost:8082/userValidator}

inventoryChecker:
  url: ${FC_LAMBDA_URL_INVENTORYCHECKER:http://localhost:8083/inventoryChecker}

paymentProcessor:
  url: ${FC_LAMBDA_URL_PAYMENTPROCESSOR:http://localhost:8084/paymentProcessor}
```

### Environment Variables

For AWS Lambda deployment:

- `FC_LAMBDA_URL_USERVALIDATOR` - URL of User Validator Lambda
- `FC_LAMBDA_URL_INVENTORYCHECKER` - URL of Inventory Checker Lambda  
- `FC_LAMBDA_URL_PAYMENTPROCESSOR` - URL of Payment Processor Lambda

## 🛠️ Technology Stack

- **Java 17+** - Modern Java features and performance
- **Spring Boot 3.3** - Latest Spring Boot with native support
- **Spring Cloud Function 4.1** - Serverless function abstraction
- **Jackson** - JSON serialization/deserialization
- **AWS Lambda** - Primary cloud deployment target
- **Maven** - Build and dependency management

## 🔮 Future Roadmap

### Multi-Cloud Support
- **Google Cloud Functions** - GCP adapter for the framework
- **Azure Functions** - Azure adapter and deployment tools
- **Multi-cloud deployment** - Single codebase, multiple cloud targets

### Alternative Tech Stacks
- **Micronaut** - Native compilation and faster cold starts
- **Quarkus** - Kubernetes-native and reactive capabilities
- **GraalVM** - Native image compilation for ultra-fast startup

### Enhanced Features
- **Service Discovery** - Automatic function URL discovery
- **Circuit Breakers** - Resilience patterns for function calls
- **Distributed Tracing** - End-to-end request tracing
- **Metrics & Monitoring** - Built-in observability

## 🤝 Contributing

We welcome contributions! Areas where we need help:

1. **Multi-cloud adapters** (GCP, Azure)
2. **Alternative tech stack support** (Micronaut, Quarkus)
3. **Enhanced testing utilities**
4. **Documentation and examples**
5. **Performance optimizations**

### To Contribute:

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'Add amazing feature'`
4. Push to the branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

## 📖 Learn More

- **Blog Post**: [Serverless by Design Programming](https://futurelyconcept.com/concepts/serverless-by-design.html)
- **Repository**: [GitHub - FC Serverless Framework](https://github.com/FuturelyConcept/fc-serverless-framework)
- **Examples**: See `fc-serverless-sample/` directory for complete working examples

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Spring Cloud Function team for the excellent serverless foundation
- AWS Lambda team for the robust serverless platform
- Spring Boot team for the amazing framework ecosystem

---

**Ready to build serverless applications the right way? Clone the repo and start coding!** 🚀