# FC Serverless Framework

Revolutionary **Serverless by Design Programming** approach that enables developers to write pure business logic while the framework handles all cross-cutting concerns through transparent HTTP proxies.

## 🚀 What is FC Serverless Framework?

FC (FuturelyConcept) Serverless Framework transforms how you build distributed serverless applications by:

- ✅ **Pure Business Logic** - Write standard `java.util.Function` and `Supplier` components with zero framework pollution
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
| ✅ Different code for local vs cloud environments | ✅ Same code works everywhere |
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
# Terminal 1 - Config Supplier (Port 8083)
cd fc-serverless-sample/sample-configsupplier
mvn spring-boot:run

# Terminal 2 - Price Calculator (Port 8082)
cd fc-serverless-sample/sample-pricecalculator
mvn spring-boot:run

# Terminal 3 - Order Processor (Port 8081)
cd fc-serverless-sample/sample-orderprocessor
mvn spring-boot:run
```

### 3. Test the Complete Flow

```bash
curl -X POST http://localhost:8081/orderProcessor \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "widget-123",
    "quantity": 5,
    "customerType": "PREMIUM"
  }'
```

Expected response:
```json
{
  "success": true,
  "orderId": "order-1706123456789",
  "totalPrice": 134.96,
  "message": "Order processed successfully! Premium customer discount applied. Total: $134.96"
}
```

## 🏗️ Simple 3-Lambda Architecture

```
OrderProcessor (Lambda 1)
       │
       │ @RemoteFunction(name = "priceCalculator")
       │ Function<OrderRequest, PriceInfo>
       ▼
PriceCalculator (Lambda 2)  
       │
       │ @RemoteFunction(name = "configSupplier")
       │ Supplier<PricingConfig>
       ▼
ConfigSupplier (Lambda 3)
```

**Key Learning Points:**
- **Function Interface**: Standard request/response with type safety
- **Supplier Interface**: Configuration/settings retrieval (no input required)
- **Transparent Proxies**: No HTTP code in business logic
- **Environment Agnostic**: Same code works locally and in AWS Lambda

## 🧩 Core Concepts

### 1. Pure Business Functions

Write clean business logic with zero framework dependencies:

```java
@Component("configSupplier")
public class ConfigSupplierFunction implements Supplier<PricingConfig> {
    @Override
    public PricingConfig get() {
        // Pure business logic - no HTTP, no URLs, no framework clutter
        return PricingConfig.defaultConfig();
    }
}
```

### 2. Transparent Cross-Service Communication

Use `@RemoteFunction` to automatically create HTTP proxies to other services:

```java
@Component("orderProcessor")
public class OrderProcessorFunction implements Function<OrderRequest, OrderResult> {

    // FC Framework automatically creates HTTP proxy for this!
    @RemoteFunction(name = "priceCalculator")
    private Function<OrderRequest, PriceInfo> priceCalculator;

    @Override
    public OrderResult apply(OrderRequest request) {
        // This call automatically becomes an HTTP request to PriceCalculator Lambda!
        PriceInfo priceInfo = priceCalculator.apply(request);
        
        String orderId = "order-" + System.currentTimeMillis();
        return OrderResult.success(orderId, priceInfo.getTotalPrice(), 
                                 "Order processed successfully! " + priceInfo.getDiscountReason());
    }
}
```

### 3. Multi-Interface Support

The framework supports different functional interfaces seamlessly:

```java
@Component("priceCalculator")
public class PriceCalculatorFunction implements Function<OrderRequest, PriceInfo> {

    // Supplier interface - no input parameters needed!
    @RemoteFunction(name = "configSupplier")
    private Supplier<PricingConfig> configSupplier;

    @Override
    public PriceInfo apply(OrderRequest request) {
        // Get configuration using Supplier (HTTP GET call)
        PricingConfig config = configSupplier.get();
        
        // Apply business logic using configuration
        return calculatePrice(request, config);
    }
}
```

### 4. Configuration-Based URL Resolution

Environment-specific configuration in `application.yml`:

```yaml
# Local development
priceCalculator:
  url: http://localhost:8082/priceCalculator

configSupplier:
  url: http://localhost:8083/configSupplier

# AWS Lambda (environment variables)
# FC_LAMBDA_URL_PRICECALCULATOR=https://xyz.lambda-url.us-east-1.on.aws/
# FC_LAMBDA_URL_CONFIGSUPPLIER=https://abc.lambda-url.us-east-1.on.aws/
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
└── fc-serverless-sample/         # Simple 3-Lambda demonstration
    ├── fc-sample-domain/         # Shared domain objects
    │   ├── OrderRequest.java
    │   ├── OrderResult.java
    │   ├── PriceInfo.java
    │   └── PricingConfig.java
    ├── sample-orderprocessor/     # Lambda 1: Orchestrator
    ├── sample-pricecalculator/    # Lambda 2: Business Logic
    └── sample-configsupplier/     # Lambda 3: Configuration Provider
```

## 🚀 Deployment to AWS Lambda

### 1. Build Deployment Packages

```bash
# Build all functions
mvn clean package

# Each function creates a deployment JAR in target/
ls fc-serverless-sample/sample-*/target/*.jar
```

### 2. Deploy Functions (Example using AWS CLI)

```bash
# Deploy Config Supplier
aws lambda create-function \
  --function-name fdd-demo-configSupplier \
  --runtime java17 \
  --role arn:aws:iam::YOUR_ACCOUNT:role/lambda-execution-role \
  --handler org.springframework.cloud.function.adapter.aws.FunctionInvoker \
  --zip-file fileb://fc-serverless-sample/sample-configsupplier/target/sample-configsupplier-1.0-SNAPSHOT.jar

# Deploy Price Calculator
aws lambda create-function \
  --function-name fdd-demo-priceCalculator \
  --runtime java17 \
  --role arn:aws:iam::YOUR_ACCOUNT:role/lambda-execution-role \
  --handler org.springframework.cloud.function.adapter.aws.FunctionInvoker \
  --zip-file fileb://fc-serverless-sample/sample-pricecalculator/target/sample-pricecalculator-1.0-SNAPSHOT.jar

# Deploy Order Processor
aws lambda create-function \
  --function-name fdd-demo-orderProcessor \
  --runtime java17 \
  --role arn:aws:iam::YOUR_ACCOUNT:role/lambda-execution-role \
  --handler org.springframework.cloud.function.adapter.aws.FunctionInvoker \
  --zip-file fileb://fc-serverless-sample/sample-orderprocessor/target/sample-orderprocessor-1.0-SNAPSHOT.jar
```

### 3. Configure Lambda URLs

Each Lambda function needs a Function URL for HTTP access:

```bash
# Create function URLs
aws lambda create-function-url-config \
  --function-name fdd-demo-configSupplier \
  --auth-type NONE \
  --cors '{"AllowMethods":["GET"],"AllowOrigins":["*"]}'

aws lambda create-function-url-config \
  --function-name fdd-demo-priceCalculator \
  --auth-type NONE \
  --cors '{"AllowMethods":["POST"],"AllowOrigins":["*"]}'

aws lambda create-function-url-config \
  --function-name fdd-demo-orderProcessor \
  --auth-type NONE \
  --cors '{"AllowMethods":["POST"],"AllowOrigins":["*"]}'

# Add permissions for public access
aws lambda add-permission \
  --function-name fdd-demo-configSupplier \
  --statement-id "FunctionURLAllowPublicAccess" \
  --action "lambda:InvokeFunctionUrl" \
  --principal "*" \
  --function-url-auth-type "NONE"

aws lambda add-permission \
  --function-name fdd-demo-priceCalculator \
  --statement-id "FunctionURLAllowPublicAccess" \
  --action "lambda:InvokeFunctionUrl" \
  --principal "*" \
  --function-url-auth-type "NONE"

aws lambda add-permission \
  --function-name fdd-demo-orderProcessor \
  --statement-id "FunctionURLAllowPublicAccess" \
  --action "lambda:InvokeFunctionUrl" \
  --principal "*" \
  --function-url-auth-type "NONE"
```

### 4. Set Environment Variables

Update Lambda functions with URLs of their dependencies:

```bash
# Configure PriceCalculator to call ConfigSupplier
aws lambda update-function-configuration \
  --function-name fdd-demo-priceCalculator \
  --environment Variables='{
    "FC_LAMBDA_URL_CONFIGSUPPLIER":"https://config-url.lambda-url.us-east-1.on.aws/"
  }'

# Configure OrderProcessor to call PriceCalculator
aws lambda update-function-configuration \
  --function-name fdd-demo-orderProcessor \
  --environment Variables='{
    "FC_LAMBDA_URL_PRICECALCULATOR":"https://price-url.lambda-url.us-east-1.on.aws/"
  }'
```

## 🧪 Testing

### Test Individual Functions

```bash
# Test ConfigSupplier (Supplier interface - GET request)
curl -X GET https://config-url.lambda-url.us-east-1.on.aws/configSupplier

# Test PriceCalculator (Function interface - POST request)
curl -X POST https://price-url.lambda-url.us-east-1.on.aws/priceCalculator \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "widget-123",
    "quantity": 5,
    "customerType": "VIP"
  }'

# Test Complete Flow
curl -X POST https://order-url.lambda-url.us-east-1.on.aws/orderProcessor \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "widget-123",
    "quantity": 8,
    "customerType": "PREMIUM"
  }'
```

### Sample Test Scenarios

```bash
# Regular Customer Order
curl -X POST http://localhost:8081/orderProcessor \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "widget-123",
    "quantity": 3,
    "customerType": "REGULAR"
  }'
# Expected: $89.97 (no discount)

# VIP Customer Order (20% discount)
curl -X POST http://localhost:8081/orderProcessor \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "widget-123",
    "quantity": 5,
    "customerType": "VIP"
  }'
# Expected: $119.96 (20% VIP discount)

# Bulk Order (5% bulk discount)
curl -X POST http://localhost:8081/orderProcessor \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "widget-123",
    "quantity": 15,
    "customerType": "REGULAR"
  }'
# Expected: $427.31 (bulk discount wins over customer discount)
```

## 🔧 Configuration

### Application Properties

Each microservice can be configured independently:

```yaml
# OrderProcessor configuration
server:
  port: 8081
spring:
  application:
    name: fc-orderprocessor
  cloud:
    function:
      definition: orderProcessor

priceCalculator:
  url: ${FC_LAMBDA_URL_PRICECALCULATOR:http://localhost:8082/priceCalculator}

# PriceCalculator configuration  
server:
  port: 8082
spring:
  application:
    name: fc-pricecalculator
  cloud:
    function:
      definition: priceCalculator

configSupplier:
  url: ${FC_LAMBDA_URL_CONFIGSUPPLIER:http://localhost:8083/configSupplier}

# ConfigSupplier configuration
server:
  port: 8083
spring:
  application:
    name: fc-configsupplier
  cloud:
    function:
      definition: configSupplier
```

### Environment Variables

For AWS Lambda deployment:

- `FC_LAMBDA_URL_PRICECALCULATOR` - URL of Price Calculator Lambda
- `FC_LAMBDA_URL_CONFIGSUPPLIER` - URL of Config Supplier Lambda

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

### Enhanced Functional Interface Support
- **Consumer<T>** - For fire-and-forget operations (notifications, logging)
- **BiFunction<T,U,R>** - For functions requiring two input parameters
- **Predicate<T>** - For validation and filtering operations

### Enhanced Features
- **Service Discovery** - Automatic function URL discovery
- **Circuit Breakers** - Resilience patterns for function calls
- **Distributed Tracing** - End-to-end request tracing
- **Metrics & Monitoring** - Built-in observability

## 🤝 Contributing

We welcome contributions! Areas where we need help:

1. **Multi-cloud adapters** (GCP, Azure)
2. **Alternative tech stack support** (Micronaut, Quarkus)
3. **Enhanced functional interface support** (Consumer, BiFunction, Predicate)
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