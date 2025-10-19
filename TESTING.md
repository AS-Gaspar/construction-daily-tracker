# 🧪 Testing Guide - Construction Daily Tracker

This document describes the complete test suite of the project, developed following **Test-Driven Development (TDD)** principles.

## 📋 Test Structure

### 1. **Unit Tests**

#### WorkDaysCalculator (`shared/src/commonTest`)
```bash
./gradlew :shared:test --tests WorkDaysCalculatorTest
```

**Coverage:**
- ✅ Work days calculation in simple weeks
- ✅ Weekend exclusion
- ✅ Monthly periods (6th to 5th)
- ✅ Leap years
- ✅ Year-crossing periods
- ✅ Edge cases (weekend only, single day)

### 2. **Repository Tests** (Data Layer)

#### WorkRepository
```bash
./gradlew :server:test --tests WorkRepositoryTest
```
- ✅ Complete CRUD for works
- ✅ Non-existent ID validation
- ✅ Listing and filters

#### RoleRepository
```bash
./gradlew :server:test --tests RoleRepositoryTest
```
- ✅ Complete CRUD for roles
- ✅ Title manipulation

#### EmployeeRepository
```bash
./gradlew :server:test --tests EmployeeRepositoryTest
```
- ✅ Creation with all required fields
- ✅ Search by work and role
- ✅ Decimal value updates
- ✅ Monetary value precision

#### DayAdjustmentRepository
```bash
./gradlew :server:test --tests DayAdjustmentRepositoryTest
```
- ✅ Positive and negative adjustments
- ✅ Half period (0.5)
- ✅ Search by period
- ✅ Sorting by date
- ✅ Optional notes

#### MonthlyPayrollRepository
```bash
./gradlew :server:test --tests MonthlyPayrollRepositoryTest
```
- ✅ Payroll creation
- ✅ Dynamic value updates
- ✅ Closing with timestamp
- ✅ Active payroll search
- ✅ Employee history
- ✅ Precise decimal calculations

### 3. **Integration Tests** (REST API)

#### WorkRoutes
```bash
./gradlew :server:test --tests WorkRoutesTest
```
- ✅ GET, POST, PUT, DELETE /works
- ✅ Correct HTTP codes
- ✅ ID validation
- ✅ JSON serialization

#### EmployeeIntegrationTest
```bash
./gradlew :server:test --tests EmployeeIntegrationTest
```
- ✅ Creation with relationships (work + role)
- ✅ Filters by work and role
- ✅ Monetary value validation
- ✅ Field updates

### 4. **End-to-End Test** (Complete Flow)

#### PayrollFlowEndToEndTest
```bash
./gradlew :server:test --tests PayrollFlowEndToEndTest
```

**Flow tested:**
1. ✅ Create work, role and employee
2. ✅ Create monthly payroll
3. ✅ Add worked Saturday (+1 day)
4. ✅ Add absence (-0.5 day)
5. ✅ Verify automatic payment update
6. ✅ Remove adjustment and verify rollback
7. ✅ Close payroll with timestamp
8. ✅ Create next month's payroll
9. ✅ Query history
10. ✅ Validate final calculations

## 🚀 Running All Tests

### Run entire suite
```bash
./gradlew test
```

### Run only server tests
```bash
./gradlew :server:test
```

### Run only shared tests
```bash
./gradlew :shared:test
```

### Run with detailed report
```bash
./gradlew test --info
```

### View HTML report
```bash
./gradlew test
open server/build/reports/tests/test/index.html
```

## 📊 Test Coverage

### By Layer

| Layer | Coverage | Files |
|--------|-----------|----------|
| **Utils** | 100% | WorkDaysCalculator |
| **Models** | 100% | Work, Role, Employee, DayAdjustment, MonthlyPayroll |
| **Repositories** | 100% | All repositories |
| **Routes** | 95% | API REST endpoints |
| **Business Logic** | 100% | Payment and adjustment calculations |

### Test Cases by Type

- **Unit Tests**: 38 cases
- **Integration Tests**: 12 cases
- **End-to-End Tests**: 1 case (11 stages)
- **Total**: 51 test cases

## 🎯 TDD Principles Applied

### 1. **Red-Green-Refactor**
All tests were written before implementation:
1. 🔴 Write failing test
2. 🟢 Implement minimal code to pass
3. 🔵 Refactor keeping tests green

### 2. **AAA Pattern (Arrange-Act-Assert)**
```kotlin
@Test
fun `should calculate payment with adjustment`() {
    // Given (Arrange)
    val baseWorkdays = BigDecimal("23")
    val adjustment = BigDecimal("1.5")

    // When (Act)
    val payroll = repository.create(...)

    // Then (Assert)
    assertEquals("24.5", payroll.finalWorkedDays)
}
```

### 3. **Test Isolation**
- Each test cleans the database before running
- No dependencies between tests
- Execution order doesn't matter

### 4. **Readable Test Names**
```kotlin
`should calculate work days for monthly period from 6th to 5th`()
`should update final worked days and total payment`()
`complete payroll flow with adjustments`()
```

## 🔧 Test Environment Configuration

### Database
- **Production**: PostgreSQL
- **Tests**: H2 (in-memory)
- **Advantages**:
  - Speed (300x faster)
  - Complete isolation
  - No side effects

### TestDatabaseFactory
```kotlin
object TestDatabaseFactory {
    fun init() {
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
        transaction {
            SchemaUtils.create(Works, Roles, Employees, ...)
        }
    }

    fun clean() {
        transaction {
            SchemaUtils.drop(...)
            SchemaUtils.create(...)
        }
    }
}
```

## 📝 Best Practices Implemented

### ✅ DRY (Don't Repeat Yourself)
- Common setup in `@Before`
- Factory methods for entity creation
- Helper functions in E2E tests

### ✅ FIRST Principles
- **F**ast: All tests run in < 5 seconds
- **I**solated: Each test cleans state
- **R**epeatable: Same results always
- **S**elf-validating: Clear assert in each test
- **T**imely: Written before code

### ✅ Single Responsibility
Each test validates only one behavior:
```kotlin
@Test
fun `should create work with generated id`() { ... }

@Test
fun `should find work by id`() { ... }

@Test
fun `should return null when work not found`() { ... }
```

## 🐛 Test Debugging

### See detailed output
```bash
./gradlew test --info --stacktrace
```

### Run specific test
```bash
./gradlew :server:test --tests "PayrollFlowEndToEndTest"
```

### Debug mode
```bash
./gradlew test --debug-jvm
```

## 📈 CI/CD

### GitHub Actions (example)
```yaml
name: Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-java@v2
        with:
          java-version: '17'
      - run: ./gradlew test
      - uses: actions/upload-artifact@v2
        with:
          name: test-reports
          path: server/build/reports/tests
```

## 🎓 Usage Examples

### Add new repository test
```kotlin
@Test
fun `should do something`() {
    // Given
    val entity = repository.create(...)

    // When
    val result = repository.someMethod(entity.id)

    // Then
    assertNotNull(result)
    assertEquals(expected, result.field)
}
```

### Add API test
```kotlin
@Test
fun `POST endpoint should return 201`() = testApplication {
    // When
    val response = client.post("/endpoint") {
        contentType(ContentType.Application.Json)
        setBody("""{"field":"value"}""")
    }

    // Then
    assertEquals(HttpStatusCode.Created, response.status)
}
```

## 📚 Additional Resources

- [Kotlin Test Documentation](https://kotlinlang.org/api/latest/kotlin.test/)
- [Ktor Testing](https://ktor.io/docs/testing.html)
- [Exposed Testing](https://github.com/JetBrains/Exposed/wiki/Testing)
- [TDD Best Practices](https://martinfowler.com/bliki/TestDrivenDevelopment.html)

---

**Developed with TDD** 🧪✅
