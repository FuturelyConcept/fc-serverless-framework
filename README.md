# Spring Function Mesh

Revolutionary **Function Mesh Programming** approach that enables developers to write pure business logic while the framework handles all cross-cutting concerns through transparent HTTP proxies with AWS IAM security.

## 🚀 What is Spring Function Mesh?

Spring Function Mesh transforms how you build distributed serverless applications by:

- ✅ **Pure Business Logic** - Write standard `java.util.Function` and `Supplier` components with zero framework pollution
- ✅ **Transparent Cross-Service Calls** - Framework automatically creates HTTP proxies for `@RemoteFunction` annotated dependencies
- ✅ **AWS IAM Security** - Built-in support for secure Lambda-to-Lambda communication using AWS SigV4 signing
- ✅ **Zero Learning Curve** - Uses familiar Spring Boot patterns and annotations
- ✅ **Local Development** - Same code works locally and in distributed Lambda environment
- ✅ **Type Safety** - Full compile-time validation and IDE support
- ✅ **AWS Lambda Ready** - Built on Spring Cloud Function for seamless cloud deployment

## 🎯 The Problem We Solve

Traditional serverless development forces you to think about infrastructure concerns in your business logic:

| Traditional Serverless | Spring Function Mesh |
|------------------------|----------------------|
| ❌ Manual HTTP clients and URL management | ✅ Transparent `@RemoteFunction` proxies |
| ❌ No compile-time validation between services | ✅ Full type safety with IDE autocomplete |
| ❌ Different code for local vs cloud environments | ✅ Same code works everywhere |
| ❌ Framework-specific learning curve | ✅ Standard Spring Boot patterns |
| ❌ Hard to test cross-service interactions | ✅ Standard Mockito testing |
| ❌ Manual security implementation | ✅ Built-in AWS IAM authentication |

## ⚡ Quick Start

### 1. Clone and Build

```bash
git clone https://github.com/FuturelyConcept/spring-function-mesh.git
cd spring-function-mesh
mvn clean install
```

### 2. Run Sample Application Locally

```bash
# Terminal 1 - Config Supplier (Port 8086)
cd spring-function-mesh-samples/sample-aws-configsupplier
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Terminal 2 - Price Calculator (Port 8085)
cd spring-function-mesh-samples/sample-aws-pricecalculator
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Terminal 3 - Order Processor (Port 8081)
cd spring-function-mesh-samples/sample-aws-orderprocessor
mvn spring-boot:run -Dspring-boot.run.profiles=local
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
  "totalPrice": 127.46,
  "message": "Order processed successfully! Premium customer discount applied. Total: $127.46"
}
```

## 🏗️ Enhanced 3-Lambda Architecture with Security

```
OrderProcessor (Lambda 1 - Public)
       │
       │ @RemoteFunction(name = "priceCalculator")
       │ Function<OrderRequest, PriceInfo>
       │ 🔐 AWS IAM Authentication
       ▼
PriceCalculator (Lambda 2 - IAM Protected)  
       │
       │ @RemoteFunction(name = "configSupplier")
       │ Supplier<PricingConfig>
       │ ℹ️ No Authentication Required
       ▼
ConfigSupplier (Lambda 3 - Public)
```

**Key Security Features:**
- **IAM-Protected Internal Services**: PriceCalculator requires AWS IAM authentication
- **Automatic SigV4 Signing**: Framework handles AWS request signing transparently
- **Public External Access**: OrderProcessor accessible without authentication
- **Flexible Auth Configuration**: Mix of public and protected services

## 🧩 Core Concepts

### 1. Pure Business Functions

Write clean business logic with zero framework dependencies:

```java
@Component("configSupplier")
public class ConfigSupplierFunction implements Supplier<PricingConfig> {
    private static final Log log = LogFactory.getLog(ConfigSupplierFunction.class);
    
    @Override
    public PricingConfig get() {
        log.info("⚙️ ConfigSupplier Lambda executing (public service)");
        // Pure business logic - no HTTP, no URLs, no framework clutter
        return PricingConfig.defaultConfig();
    }
}
```

### 2. Transparent Cross-Service Communication with Security

Use `@RemoteFunction` with automatic AWS IAM authentication:

```java
@Component("orderProcessor")
public class OrderProcessorFunction implements Function<OrderRequest, OrderResult> {
    private static final Log log = LogFactory.getLog(OrderProcessorFunction.class);

    // Framework automatically creates HTTP proxy with IAM authentication!
    @RemoteFunction(name = "priceCalculator")
    private Function<OrderRequest, PriceInfo> priceCalculator;

    @Override
    public OrderResult apply(OrderRequest request) {
        log.info("🚀 OrderProcessor Lambda started!");
        
        // This call automatically becomes an authenticated HTTP request to PriceCalculator Lambda!
        PriceInfo priceInfo = priceCalculator.apply(request);
        
        String orderId = "order-" + System.currentTimeMillis();
        return OrderResult.success(orderId, priceInfo.getTotalPrice(), 
                                 "Order processed successfully! " + priceInfo.getDiscountReason());
    }
}
```

### 3. Enhanced Configuration with Authentication

Configure services with authentication types in `application.yml`:

```yaml
# Enhanced FC Framework Configuration with Auth Types
fc:
  functions:
    # PriceCalculator - IAM protected
    priceCalculator:
      url: ${FC_LAMBDA_URL_PRICECALCULATOR:http://localhost:8085/priceCalculator}
      authType: ${FC_AUTH_PRICECALCULATOR:AWS_IAM}
    
    # ConfigSupplier - Public access
    configSupplier:
      url: ${FC_LAMBDA_URL_CONFIGSUPPLIER:http://localhost:8086/configSupplier}
      authType: ${FC_AUTH_CONFIGSUPPLIER:NONE}
```

## 📁 Project Structure

```
spring-function-mesh/
├── spring-function-mesh-core/           # Core framework components
│   └── annotation/
│       └── RemoteFunction.java          # @RemoteFunction annotation
├── spring-function-mesh-aws-starter/    # AWS-specific auto-configuration
│   ├── auth/
│   │   └── AwsIamRequestSigner.java     # AWS SigV4 request signing
│   ├── config/
│   │   ├── RemoteFunctionAutoConfiguration.java
│   │   ├── RemoteFunctionBeanPostProcessor.java
│   │   └── EnvironmentPropertyResolver.java
│   └── proxy/
│       └── RemoteFunctionProxyFactory.java
└── spring-function-mesh-samples/        # Complete 3-Lambda demonstration
    ├── sample-shared-domain/            # Shared domain objects
    │   ├── OrderRequest.java
    │   ├── OrderResult.java
    │   ├── PriceInfo.java
    │   └── PricingConfig.java
    ├── sample-aws-orderprocessor/       # Lambda 1: Public Orchestrator
    ├── sample-aws-pricecalculator/      # Lambda 2: IAM-Protected Business Logic
    └── sample-aws-configsupplier/       # Lambda 3: Public Configuration Provider
```

## 🚀 AWS Lambda Deployment Guide

### Prerequisites

- AWS CLI configured with appropriate permissions
- Java 17+ installed
- Maven 3.6+ installed

### Step 1: Build Deployment Packages

```bash
# Build all functions
cd spring-function-mesh
mvn clean package

# Verify JAR files are created
ls spring-function-mesh-samples/sample-aws-*/target/*.jar
```

### Step 2: Create IAM Execution Role

```bash
# Create trust policy for Lambda execution
cat > lambda-trust-policy.json << EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
EOF

# Create IAM role for Lambda execution
aws iam create-role \
  --role-name spring-function-mesh-execution-role \
  --assume-role-policy-document file://lambda-trust-policy.json

# Attach basic Lambda execution policy
aws iam attach-role-policy \
  --role-name spring-function-mesh-execution-role \
  --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole

# Get the role ARN for later use
EXECUTION_ROLE_ARN=$(aws iam get-role --role-name spring-function-mesh-execution-role --query 'Role.Arn' --output text)
echo "Execution Role ARN: $EXECUTION_ROLE_ARN"
```

### Step 3: Deploy Lambda Functions

```bash
# Deploy ConfigSupplier (Public Service)
aws lambda create-function \
  --function-name spring-mesh-configsupplier \
  --runtime java17 \
  --role $EXECUTION_ROLE_ARN \
  --handler org.springframework.cloud.function.adapter.aws.FunctionInvoker \
  --zip-file fileb://spring-function-mesh-samples/sample-aws-configsupplier/target/sample-aws-configsupplier-1.0-SNAPSHOT.jar \
  --timeout 30 \
  --memory-size 512 \
  --environment Variables='{
    "SPRING_CLOUD_FUNCTION_DEFINITION":"configSupplier"
  }'

# Deploy PriceCalculator (IAM-Protected Service)
aws lambda create-function \
  --function-name spring-mesh-pricecalculator \
  --runtime java17 \
  --role $EXECUTION_ROLE_ARN \
  --handler org.springframework.cloud.function.adapter.aws.FunctionInvoker \
  --zip-file fileb://spring-function-mesh-samples/sample-aws-pricecalculator/target/sample-aws-pricecalculator-1.0-SNAPSHOT.jar \
  --timeout 30 \
  --memory-size 512 \
  --environment Variables='{
    "SPRING_CLOUD_FUNCTION_DEFINITION":"priceCalculator"
  }'

# Deploy OrderProcessor (Public Entry Point)
aws lambda create-function \
  --function-name spring-mesh-orderprocessor \
  --runtime java17 \
  --role $EXECUTION_ROLE_ARN \
  --handler org.springframework.cloud.function.adapter.aws.FunctionInvoker \
  --zip-file fileb://spring-function-mesh-samples/sample-aws-orderprocessor/target/sample-aws-orderprocessor-1.0-SNAPSHOT.jar \
  --timeout 30 \
  --memory-size 512 \
  --environment Variables='{
    "SPRING_CLOUD_FUNCTION_DEFINITION":"orderProcessor"
  }'
```

### Step 4: Create Function URLs

```bash
# Create function URL for ConfigSupplier (Public)
aws lambda create-function-url-config \
  --function-name spring-mesh-configsupplier \
  --auth-type NONE \
  --cors '{"AllowMethods":["POST"],"AllowOrigins":["*"],"AllowHeaders":["Content-Type"]}'

# Create function URL for PriceCalculator (IAM Protected)
aws lambda create-function-url-config \
  --function-name spring-mesh-pricecalculator \
  --auth-type AWS_IAM \
  --cors '{"AllowMethods":["POST"],"AllowOrigins":["*"],"AllowHeaders":["Content-Type","Authorization"]}'

# Create function URL for OrderProcessor (Public)
aws lambda create-function-url-config \
  --function-name spring-mesh-orderprocessor \
  --auth-type NONE \
  --cors '{"AllowMethods":["POST"],"AllowOrigins":["*"],"AllowHeaders":["Content-Type"]}'

# Get the function URLs
CONFIG_SUPPLIER_URL=$(aws lambda get-function-url-config --function-name spring-mesh-configsupplier --query 'FunctionUrl' --output text)
PRICE_CALCULATOR_URL=$(aws lambda get-function-url-config --function-name spring-mesh-pricecalculator --query 'FunctionUrl' --output text)
ORDER_PROCESSOR_URL=$(aws lambda get-function-url-config --function-name spring-mesh-orderprocessor --query 'FunctionUrl' --output text)

echo "ConfigSupplier URL: $CONFIG_SUPPLIER_URL"
echo "PriceCalculator URL: $PRICE_CALCULATOR_URL"
echo "OrderProcessor URL: $ORDER_PROCESSOR_URL"
```

### Step 5: Create IAM Policies for Cross-Function Calls

```bash
# Create policy for Lambda-to-Lambda invocation
cat > lambda-invoke-policy.json << EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "lambda:InvokeFunction",
                "lambda:InvokeFunctionUrl"
            ],
            "Resource": [
                "arn:aws:lambda:*:*:function:spring-mesh-*"
            ]
        }
    ]
}
EOF

# Create the IAM policy
aws iam create-policy \
  --policy-name SpringFunctionMeshInvokePolicy \
  --policy-document file://lambda-invoke-policy.json

# Get account ID and policy ARN
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
INVOKE_POLICY_ARN="arn:aws:iam::$ACCOUNT_ID:policy/SpringFunctionMeshInvokePolicy"

# Attach policy to the execution role
aws iam attach-role-policy \
  --role-name spring-function-mesh-execution-role \
  --policy-arn $INVOKE_POLICY_ARN
```

### Step 6: Configure Environment Variables

```bash
# Configure OrderProcessor to call PriceCalculator
aws lambda update-function-configuration \
  --function-name spring-mesh-orderprocessor \
  --environment Variables='{
    "SPRING_CLOUD_FUNCTION_DEFINITION":"orderProcessor",
    "FC_LAMBDA_URL_PRICECALCULATOR":"'$PRICE_CALCULATOR_URL'",
    "FC_AUTH_PRICECALCULATOR":"AWS_IAM"
  }'

# Configure PriceCalculator to call ConfigSupplier
aws lambda update-function-configuration \
  --function-name spring-mesh-pricecalculator \
  --environment Variables='{
    "SPRING_CLOUD_FUNCTION_DEFINITION":"priceCalculator",
    "FC_LAMBDA_URL_CONFIGSUPPLIER":"'$CONFIG_SUPPLIER_URL'",
    "FC_AUTH_CONFIGSUPPLIER":"NONE"
  }'
```

### Step 7: Test the Deployment

```bash
# Test individual functions
echo "Testing ConfigSupplier..."
curl -X POST $CONFIG_SUPPLIER_URL \
  -H "Content-Type: application/json"

echo -e "\nTesting PriceCalculator..."
curl -X POST $PRICE_CALCULATOR_URL \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "widget-123",
    "quantity": 2,
    "customerType": "PREMIUM"
  }'

echo -e "\nTesting complete flow..."
curl -X POST $ORDER_PROCESSOR_URL \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "widget-123",
    "quantity": 5,
    "customerType": "PREMIUM"
  }'
```

### Step 8: Monitor and Debug

```bash
# Check CloudWatch logs
aws logs describe-log-groups --log-group-name-prefix "/aws/lambda/spring-mesh"

# Tail logs for specific function
aws logs tail /aws/lambda/spring-mesh-orderprocessor --follow

# Check function configuration
aws lambda get-function-configuration --function-name spring-mesh-orderprocessor
```

## 🧪 Testing Scenarios

### Test Different Customer Types

```bash
# VIP Customer (20% discount)
curl -X POST $ORDER_PROCESSOR_URL \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "widget-123",
    "quantity": 2,
    "customerType": "VIP"
  }'

# Premium Customer (15% discount)
curl -X POST $ORDER_PROCESSOR_URL \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "widget-123",
    "quantity": 3,
    "customerType": "PREMIUM"
  }'

# Regular Customer (5% discount)
curl -X POST $ORDER_PROCESSOR_URL \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "widget-123",
    "quantity": 5,
    "customerType": "REGULAR"
  }'
```

### Test Bulk Orders

```bash
# Large quantity order (triggers bulk discount)
curl -X POST $ORDER_PROCESSOR_URL \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "widget-123",
    "quantity": 15,
    "customerType": "REGULAR"
  }'
```

## 🔧 Configuration Reference

### Local Development Configuration

```yaml
# application-local.yml - For testing without AWS IAM
fc:
  functions:
    priceCalculator:
      port: 8085
      authType: NONE
    configSupplier:
      port: 8086
      authType: NONE

logging:
  level:
    com.fc: DEBUG
    com.fc.serverless.auth: TRACE
```

### AWS Lambda Configuration

```yaml
# application.yml - For production deployment
fc:
  functions:
    priceCalculator:
      url: ${FC_LAMBDA_URL_PRICECALCULATOR}
      authType: ${FC_AUTH_PRICECALCULATOR:AWS_IAM}
    configSupplier:
      url: ${FC_LAMBDA_URL_CONFIGSUPPLIER}
      authType: ${FC_AUTH_CONFIGSUPPLIER:NONE}

logging:
  level:
    com.fc: INFO
    com.fc.serverless.auth: DEBUG
```

### Environment Variables Reference

| Variable | Description | Example |
|----------|-------------|---------|
| `FC_LAMBDA_URL_PRICECALCULATOR` | URL of PriceCalculator Lambda | `https://abc123.lambda-url.us-east-1.on.aws/` |
| `FC_AUTH_PRICECALCULATOR` | Auth type for PriceCalculator | `AWS_IAM` or `NONE` |
| `FC_LAMBDA_URL_CONFIGSUPPLIER` | URL of ConfigSupplier Lambda | `https://def456.lambda-url.us-east-1.on.aws/` |
| `FC_AUTH_CONFIGSUPPLIER` | Auth type for ConfigSupplier | `NONE` |

## 🛠️ Technology Stack

- **Java 17+** - Modern Java features and performance
- **Spring Boot 3.3** - Latest Spring Boot with native support
- **Spring Cloud Function 4.1** - Serverless function abstraction
- **AWS SDK v2** - For IAM authentication and request signing
- **Jackson** - JSON serialization/deserialization
- **AWS Lambda** - Primary cloud deployment target
- **Apache Commons Logging** - Structured logging
- **Maven** - Build and dependency management

## 🔮 Future Roadmap

### Enhanced Security Features
- **Multiple Auth Types** - Support for API Keys, JWT tokens
- **Role-Based Access** - Fine-grained permission control
- **Request Encryption** - End-to-end encryption for sensitive data

### Multi-Cloud Support
- **Google Cloud Functions** - GCP adapter for the framework
- **Azure Functions** - Azure adapter and deployment tools
- **Multi-cloud deployment** - Single codebase, multiple cloud targets

### Enhanced Observability
- **Distributed Tracing** - End-to-end request tracing with AWS X-Ray
- **Metrics & Monitoring** - Built-in CloudWatch metrics
- **Performance Analytics** - Function performance insights

## 🤝 Contributing

We welcome contributions! See [CONTRIBUTING.md](CONTRIBUTING.md) for details.

Areas where we need help:

1. **Multi-cloud adapters** (GCP, Azure)
2. **Enhanced security features** (JWT, API Keys)
3. **Performance optimizations**
4. **Documentation and examples**
5. **Testing and quality assurance**

## 📖 Learn More

- **Blog Post**: [Function Mesh Programming](https://futurelyconcept.com/concepts/function-mesh.html)
- **Repository**: [GitHub - Spring Function Mesh](https://github.com/FuturelyConcept/spring-function-mesh)
- **Examples**: See `spring-function-mesh-samples/` directory for complete working examples

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Spring Cloud Function team for the excellent serverless foundation
- AWS Lambda team for the robust serverless platform
- Spring Boot team for the amazing framework ecosystem

---

**Ready to build serverless applications with function mesh architecture? Clone the repo and start coding!** 🚀