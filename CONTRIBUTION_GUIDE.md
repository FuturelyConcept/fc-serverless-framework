# Contributing to Spring Function Mesh

Thank you for your interest in contributing to Spring Function Mesh! This document provides guidelines and information for contributors.

## 🚀 Getting Started

### Prerequisites

- **Java 17+** - Required for building and running the framework
- **Maven 3.6+** - For dependency management and building
- **Git** - For version control
- **AWS CLI** (optional) - For testing AWS Lambda deployments
- **IDE** - IntelliJ IDEA, Eclipse, or VS Code with Java extensions

### Development Environment Setup

1. **Fork and Clone the Repository**
   ```bash
   git clone https://github.com/YOUR_USERNAME/spring-function-mesh.git
   cd spring-function-mesh
   ```

2. **Build the Project**
   ```bash
   mvn clean install
   ```

3. **Run Tests**
   ```bash
   mvn test
   ```

4. **Verify Sample Applications**
   ```bash
   # Terminal 1
   cd spring-function-mesh-samples/sample-aws-configsupplier
   mvn spring-boot:run -Dspring-boot.run.profiles=local
   
   # Terminal 2
   cd spring-function-mesh-samples/sample-aws-pricecalculator
   mvn spring-boot:run -Dspring-boot.run.profiles=local
   
   # Terminal 3
   cd spring-function-mesh-samples/sample-aws-orderprocessor
   mvn spring-boot:run -Dspring-boot.run.profiles=local
   
   # Test the flow
   curl -X POST http://localhost:8081/orderProcessor \
     -H "Content-Type: application/json" \
     -d '{"productId":"widget-123","quantity":5,"customerType":"PREMIUM"}'
   ```

## 🎯 Areas for Contribution

### 1. Core Framework Enhancements

**Priority: High**

- **Enhanced Authentication Support**
  - JWT token validation
  - API Key authentication
  - Custom authentication providers
  - Role-based access control

- **Performance Optimizations**
  - Connection pooling for HTTP clients
  - Request/response caching
  - Async function invocation
  - Circuit breaker patterns

- **Error Handling & Resilience**
  - Retry mechanisms with exponential backoff
  - Timeout configuration
  - Fallback strategies
  - Health check endpoints

### 2. Multi-Cloud Support

**Priority: Medium**

- **Google Cloud Functions Adapter**
  - `spring-function-mesh-gcp-starter` module
  - Google Cloud authentication
  - Cloud Functions deployment scripts

- **Azure Functions Adapter**
  - `spring-function-mesh-azure-starter` module
  - Azure Active Directory integration
  - Azure deployment automation

- **Generic Cloud Adapter**
  - Abstract cloud provider interface
  - Pluggable authentication mechanisms
  - Universal deployment patterns

### 3. Developer Experience

**Priority: High**

- **Enhanced Configuration**
  - YAML-based function mesh definitions
  - Visual configuration validation
  - IDE plugins for autocomplete
  - Configuration templates

- **Testing Support**
  - Mock function providers
  - Integration testing utilities
  - Load testing tools
  - Contract testing support

- **Documentation & Examples**
  - Step-by-step tutorials
  - Real-world use cases
  - Performance benchmarks
  - Troubleshooting guides

### 4. Monitoring & Observability

**Priority: Medium**

- **Distributed Tracing**
  - AWS X-Ray integration
  - Jaeger/Zipkin support
  - Custom trace correlation
  - Performance analytics

- **Metrics & Monitoring**
  - CloudWatch metrics
  - Prometheus integration
  - Custom metrics collection
  - Dashboard templates

- **Logging Enhancements**
  - Structured logging formats
  - Log correlation across functions
  - Centralized log aggregation
  - Debug tracing modes

## 📝 Contribution Process

### 1. Before You Start

- **Check existing issues** to avoid duplicate work
- **Create an issue** to discuss your proposed changes
- **Get feedback** from maintainers before starting large features
- **Follow the coding standards** outlined below

### 2. Making Changes

1. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make your changes**
   - Follow the existing code style
   - Add tests for new functionality
   - Update documentation if needed

3. **Test your changes**
   ```bash
   mvn clean test
   mvn verify
   ```

4. **Commit your changes**
   ```bash
   git add .
   git commit -m "feat: add support for JWT authentication"
   ```

### 3. Submitting Changes

1. **Push your branch**
   ```bash
   git push origin feature/your-feature-name
   ```

2. **Create a Pull Request**
   - Use a descriptive title
   - Include a detailed description
   - Reference related issues
   - Add screenshots if applicable

3. **Address review feedback**
   - Respond to comments
   - Make requested changes
   - Update tests if needed

## 🏗️ Project Structure

```
spring-function-mesh/
├── spring-function-mesh-core/           # Core annotations and interfaces
│   ├── src/main/java/com/fc/serverless/core/
│   │   └── annotation/RemoteFunction.java
│   └── src/test/java/                   # Unit tests
├── spring-function-mesh-aws-starter/    # AWS-specific implementation
│   ├── src/main/java/com/fc/serverless/
│   │   ├── auth/AwsIamRequestSigner.java
│   │   ├── config/                      # Auto-configuration
│   │   └── proxy/                       # Proxy factory
│   └── src/test/java/                   # Integration tests
└── spring-function-mesh-samples/        # Sample applications
    ├── sample-shared-domain/            # Domain objects
    ├── sample-aws-orderprocessor/       # Sample function 1
    ├── sample-aws-pricecalculator/      # Sample function 2
    └── sample-aws-configsupplier/       # Sample function 3
```

## 📋 Coding Standards

### Java Code Style

- **Use Java 17+ features** where appropriate
- **Follow Spring Boot conventions** for configuration and beans
- **Use Apache Commons Logging** for all logging
- **Write descriptive variable and method names**
- **Add JavaDoc** for public APIs
- **Include unit tests** for all new functionality

### Example Code Style

```java
/**
 * Enhanced authentication provider for Spring Function Mesh.
 * 
 * @since 1.1.0
 */
@Component
public class EnhancedAuthProvider {
    
    private static final Log log = LogFactory.getLog(EnhancedAuthProvider.class);
    
    /**
     * Authenticates the given request using the specified auth type.
     * 
     * @param request the HTTP request to authenticate
     * @param authType the authentication type to use
     * @return the authenticated request headers
     * @throws AuthenticationException if authentication fails
     */
    public HttpHeaders authenticate(HttpRequest request, AuthType authType) {
        log.debug("Authenticating request with auth type: " + authType);
        
        switch (authType) {
            case AWS_IAM:
                return authenticateWithAwsIam(request);
            case JWT:
                return authenticateWithJwt(request);
            default:
                throw new AuthenticationException("Unsupported auth type: " + authType);
        }
    }
}
```

### Configuration Guidelines

```yaml
# Use hierarchical configuration structure
spring:
  function:
    mesh:
      auth:
        default-type: NONE
        aws:
          region: ${AWS_REGION:us-east-1}
          sign-requests: true
        jwt:
          issuer: ${JWT_ISSUER}
          audience: ${JWT_AUDIENCE}
      
      functions:
        my-function:
          url: ${FUNCTION_URL}
          auth-type: AWS_IAM
          timeout: 30s
          retry:
            max-attempts: 3
            backoff: exponential
```

### Testing Guidelines

- **Write unit tests** for all business logic
- **Use integration tests** for cross-function communication
- **Mock external dependencies** in unit tests
- **Test error scenarios** and edge cases
- **Verify security configurations** work correctly

```java
@SpringBootTest
class RemoteFunctionProxyTest {
    
    @MockBean
    private RestTemplate restTemplate;
    
    @Test
    void shouldSignRequestWithAwsIam() {
        // Given
        FunctionConfig config = new FunctionConfig("https://example.com", AuthType.AWS_IAM);
        
        // When
        Object result = proxyFactory.createProxy(Function.class, annotation, environment, String.class);
        
        // Then
        assertThat(result).isNotNull();
        verify(awsIamSigner).signRequest(any(), any(), any(), any());
    }
}
```

## 🔍 Review Process

### What We Look For

- **Functionality** - Does the code work as intended?
- **Testing** - Are there adequate tests covering the changes?
- **Documentation** - Is the code well-documented?
- **Performance** - Will this impact framework performance?
- **Security** - Are there any security implications?
- **Compatibility** - Is this backward compatible?
- **Code Style** - Does it follow our coding standards?

### Review Timeline

- **Initial Review**: Within 2-3 business days
- **Follow-up Reviews**: Within 1-2 business days
- **Merge Decision**: After all feedback is addressed

### Review Criteria

✅ **Ready to Merge**
- All tests pass
- Code coverage is maintained or improved
- Documentation is updated
- No breaking changes (unless discussed)
- Follows coding standards

❌ **Needs Work**
- Tests are failing
- Code coverage decreased significantly
- Missing documentation
- Breaking changes without migration path
- Code style violations

## 🐛 Bug Reports

### Before Reporting a Bug

1. **Search existing issues** to avoid duplicates
2. **Test with the latest version** to ensure bug still exists
3. **Isolate the problem** with a minimal reproduction case
4. **Check documentation** for correct usage patterns

### Bug Report Template

```markdown
## Bug Description
A clear description of what the bug is.

## To Reproduce
Steps to reproduce the behavior:
1. Configure function with '...'
2. Call remote function '...'
3. See error

## Expected Behavior
What you expected to happen.

## Actual Behavior
What actually happened.

## Environment
- Spring Function Mesh version: [e.g., 1.0.0]
- Java version: [e.g., Java 17]
- Spring Boot version: [e.g., 3.3.0]
- Cloud provider: [e.g., AWS Lambda]
- OS: [e.g., macOS 14.0]

## Logs
```
Include relevant log output here
```

## Additional Context
Any other context about the problem.
```

## 💡 Feature Requests

### Before Requesting a Feature

1. **Check existing issues** and roadmap
2. **Consider alternatives** that might meet your needs
3. **Think about implementation** complexity and impact
4. **Discuss with maintainers** for large features

### Feature Request Template

```markdown
## Feature Description
A clear description of the feature you'd like to see.

## Use Case
Describe the problem this feature would solve.

## Proposed Solution
How you envision this feature working.

## Alternatives Considered
Other approaches you've considered.

## Additional Context
Any other context or screenshots about the feature.
```

## 🏷️ Commit Message Guidelines

We follow [Conventional Commits](https://www.conventionalcommits.org/) specification:

### Format
```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

### Types
- **feat**: A new feature
- **fix**: A bug fix
- **docs**: Documentation only changes
- **style**: Code style changes (formatting, missing semi-colons, etc)
- **refactor**: Code change that neither fixes a bug nor adds a feature
- **perf**: Performance improvements
- **test**: Adding missing tests or correcting existing tests
- **chore**: Changes to build process or auxiliary tools

### Examples
```bash
feat(auth): add JWT authentication support

fix(proxy): resolve null pointer exception in request signing

docs(readme): update deployment instructions for AWS Lambda

test(integration): add tests for multi-function scenarios

chore(deps): update Spring Boot to 3.3.1
```

## 📚 Documentation Guidelines

### Code Documentation

- **Use JavaDoc** for all public APIs
- **Include examples** in documentation
- **Document parameters and return values**
- **Explain complex logic** with inline comments

### README Updates

- **Keep examples current** and working
- **Update configuration sections** when adding new features
- **Include troubleshooting** information
- **Add links to relevant issues** or discussions

### Wiki and Guides

- **Write step-by-step tutorials** for common scenarios
- **Include screenshots** where helpful
- **Test all examples** before publishing
- **Keep language clear** and beginner-friendly

## 🎯 Getting Help

### Questions and Discussions

- **GitHub Discussions** - For general questions and ideas
- **Issues** - For bug reports and feature requests
- **Email** - For security-related concerns

### Community Guidelines

- **Be respectful** and professional
- **Help others** when you can
- **Search before asking** to avoid duplicate questions
- **Provide context** when asking for help

## 🏆 Recognition

### Contributors Wall

We maintain a contributors list in our README and recognize:

- **Code contributions** (features, bug fixes, tests)
- **Documentation improvements** (README, guides, examples)
- **Issue triaging** (helping with bug reports, feature requests)
- **Community support** (answering questions, providing examples)

### Types of Recognition

- **Contributor badge** in README
- **Special mentions** in release notes
- **Conference speaking opportunities** about the project
- **Maintainer privileges** for consistent contributors

## 📋 Release Process

### Version Numbering

We follow [Semantic Versioning](https://semver.org/):

- **Major (X.0.0)**: Breaking changes
- **Minor (1.X.0)**: New features, backward compatible
- **Patch (1.0.X)**: Bug fixes, backward compatible

### Release Schedule

- **Major releases**: 1-2 times per year
- **Minor releases**: Every 2-3 months
- **Patch releases**: As needed for critical bugs

### What Goes in Each Release

**Major Release**
- Breaking API changes
- Architecture improvements
- New cloud provider support

**Minor Release**
- New features
- Performance improvements
- Enhanced configuration options

**Patch Release**
- Bug fixes
- Security updates
- Documentation corrections

## 🔐 Security

### Reporting Security Issues

**DO NOT** create public issues for security vulnerabilities.

Instead:
1. **Email us** at security@futurelyconcept.com
2. **Include details** about the vulnerability
3. **Provide reproduction steps** if possible
4. **Wait for our response** before public disclosure

### Security Review Process

1. **Acknowledgment** within 24 hours
2. **Initial assessment** within 3 business days
3. **Fix development** and testing
4. **Coordinated disclosure** after fix is available

## 📞 Contact

### Maintainers

- **Primary Maintainer**: [Your Name] (@yourusername)
- **Security Contact**: security@futurelyconcept.com
- **General Questions**: GitHub Discussions

### Community

- **GitHub**: [Spring Function Mesh Repository](https://github.com/FuturelyConcept/spring-function-mesh)
- **Website**: [FuturelyConcept](https://futurelyconcept.com)
- **Blog**: [Function Mesh Programming](https://futurelyconcept.com/concepts/function-mesh.html)

---

Thank you for contributing to Spring Function Mesh! Together, we're building the future of serverless function orchestration. 🚀