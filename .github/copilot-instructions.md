# SupplyChainX - AI Coding Agent Instructions

## Architecture Overview

**SupplyChainX** is a Spring Boot 3.5.7 REST API for supply chain management with three core domains:
- `approvisionnement/` - Raw materials, suppliers, supply orders
- `production/` - Products, production orders
- `livraison/` - Customers, client orders, deliveries

Each domain follows consistent layering: `controller/` → `service/impl/` → `repository/`, with `entity/`, `dto/`, and `mapper/` packages.

## Authentication & Security

**Current**: JWT-based auth with database-persisted refresh tokens
- **Login** via `POST /api/auth/login` returns `accessToken` (15min) and `refreshToken` (7 days)
- **Refresh** via `POST /api/auth/refresh?refreshToken=<token>` rotates tokens (revokes old, issues new)
- **Logout** via `POST /api/auth/logout?refreshToken=<token>` revokes single token
- **Logout All** via `POST /api/auth/logout-all` (requires access token) revokes all user tokens
- All endpoints except `/api/auth/**` require JWT via `Authorization: Bearer <token>`
- Security config: `config/SecurityConfig.java` uses OAuth2 Resource Server with JWT
- **Refresh tokens stored in DB** (`refresh_tokens` table) with SHA-256 hash for security
- **Token rotation**: Each refresh invalidates old token and creates new one
- **User enabled check**: Disabled users cannot login or refresh tokens
- See `DOCS/AUTH_FLOW.md` for detailed authentication flow

**Key Classes**:
- `config/SecurityConfig` - JWT config with `jwtDecoder()` and environment-based secret
- `config/JpaUserDetailsService` - loads users from DB by email
- `security/JwtTokenUtil` - generates/validates JWT tokens (env-based secret)
- `auth/entity/RefreshToken` - DB entity for refresh token persistence
- `auth/service/RefreshTokenService` - manages token lifecycle (create, validate, rotate, revoke)
- `administration/entity/User` - user entity with BCrypt password hash and `enabled` flag
- Roles defined in `common/security/Role.java` enum

## Code Conventions

### MapStruct DTOs
**Always use MapStruct** for entity ↔ DTO conversions:
```java
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RawMaterialMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    RawMaterial toEntity(RawMaterialRequestDto dto);
    
    RawMaterialResponseDto toDto(RawMaterial entity);
    
    void updateEntityFromDto(RawMaterialRequestDto dto, @MappingTarget RawMaterial entity);
}
```
- MapStruct processors configured in `pom.xml` with Lombok binding
- Pattern: `RequestDto` for input, `ResponseDto` for output
- Always ignore `id` and timestamp fields in `toEntity()` mappings

### Service Layer Pattern
All service implementations are `@Transactional` at class level:
```java
@Service
@AllArgsConstructor
@Transactional
public class RawMaterialServiceImpl implements RawMaterialService {
    private final RawMaterialRepository repository;
    private final RawMaterialMapper mapper;
    
    // Business logic with custom validations before save
    public RawMaterialResponseDto create(RawMaterialRequestDto dto) {
        if (dto.getName() != null && repository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Raw material name already exists");
        }
        RawMaterial entity = mapper.toEntity(dto);
        entity.setCreatedAt(LocalDateTime.now());
        return mapper.toDto(repository.save(entity));
    }
}
```

### Exception Handling
- `common/exception/ResourceNotFoundException` - 404 scenarios
- Services throw `IllegalArgumentException` for validation failures
- Follow pattern: validate → map → set timestamps → save → map to DTO

## Development Workflows

### Build & Run Locally
```bash
# Start MySQL via Docker
docker-compose up mysql phpmyadmin -d

# Run app (uses application.yml with localhost:3306)
./mvnw spring-boot:run

# Or run with specific profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=docker
```

### Testing Strategy
- **Unit tests** (Surefire): `*Test.java` - use Mockito, no Spring context
- **Integration tests** (Failsafe): `*IT.java` 
  - `@DataJpaTest` for repository tests with H2 in-memory DB
  - `@SpringBootTest` with `@AutoConfigureMockMvc(addFilters = false)` for controller tests
  - Profile: `@ActiveProfiles("test")` uses H2 via `application-test.properties`

```bash
# Unit tests only
./mvnw test

# Integration tests only
./mvnw verify -DskipUTs

# All tests
./mvnw verify
```

### Docker Deployment
```bash
# Build and start full stack (MySQL + PHPMyAdmin + App)
docker-compose up --build

# App uses application-docker.yml with mysql:3306 hostname
# Access: http://localhost:8080
# PHPMyAdmin: http://localhost:8081
```

## Database & Schema Management

- **Dev**: `hibernate.ddl-auto: update` auto-creates tables from JPA entities
- **Liquibase**: Disabled but configured - use for production migrations
- **Production**: Change to `ddl-auto: validate` and enable Liquibase
- DB credentials in `docker-compose.yml`: user `scx`, database `supplychainx`

## API Documentation

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI spec configured via SpringDoc (`springdoc-openapi-starter-webmvc-ui`)
- Security scheme configured for JWT Bearer auth in Swagger

## Key Dependencies

- **Spring Boot 3.5.7** (Java 17)
- **MapStruct 1.5.5** - DTO mapping
- **Lombok** - boilerplate reduction (works with MapStruct via `lombok-mapstruct-binding`)
- **JJWT 0.12.5** - JWT tokens
- **Testcontainers 1.18.3** - DB integration tests
- **MySQL 8.0** connector
- **H2** - test database only

## Common Pitfalls

1. **MapStruct + Lombok**: Ensure annotation processors ordered correctly in `pom.xml` (MapStruct → Lombok → binding)
2. **JWT Testing**: Controllers have `addFilters = false` in tests to bypass security
3. **Profile Confusion**: Local uses `application.yml`, Docker uses `application-docker.yml`, tests use `application-test.properties`
4. **Role Prefix**: Spring Security expects `ROLE_` prefix - JWT converter adds it automatically
5. **Timestamp Fields**: Always set `createdAt` manually in services before save
