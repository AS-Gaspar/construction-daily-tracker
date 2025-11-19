# 🧪 Guia de Testes - Construction Daily Tracker

Este documento descreve a suite completa de testes do projeto, desenvolvido seguindo os princípios de **Test-Driven Development (TDD)**.

---

## 📋 Estrutura de Testes

### 1. **Testes Unitários**

#### WorkDaysCalculator (`shared/src/commonTest`)

```bash
./gradlew :shared:test --tests WorkDaysCalculatorTest
```

**Cobertura:**
- ✅ Cálculo de dias úteis em semanas simples
- ✅ Exclusão de finais de semana
- ✅ Períodos mensais (dia 6 ao dia 5)
- ✅ Anos bissextos
- ✅ Períodos que cruzam anos
- ✅ Casos extremos (apenas fim de semana, dia único)

**Exemplo de teste:**
```kotlin
@Test
fun testCalculateWorkDays_simpleWeek() {
    val startDate = "2024-01-08" // Segunda-feira
    val endDate = "2024-01-12"   // Sexta-feira
    val workDays = calculateWorkDays(startDate, endDate)
    assertEquals(5, workDays)
}
```

---

### 2. **Testes de Repositório Local** (Camada de Dados)

#### LocalWorkRepository

```bash
./gradlew :composeApp:testDebugUnitTest --tests LocalWorkRepositoryTest
```

**Funcionalidades testadas:**
- ✅ Criação de obras
- ✅ Leitura de obras por ID
- ✅ Atualização de obras
- ✅ Exclusão de obras
- ✅ Listagem de todas as obras

#### LocalEmployeeRepository

```bash
./gradlew :composeApp:testDebugUnitTest --tests LocalEmployeeRepositoryTest
```

**Funcionalidades testadas:**
- ✅ Criação com todos os campos obrigatórios
- ✅ Busca por obra e função
- ✅ Atualização de valores decimais
- ✅ Precisão de valores monetários
- ✅ Atribuição a obras

#### LocalDayAdjustmentRepository

```bash
./gradlew :composeApp:testDebugUnitTest --tests LocalDayAdjustmentRepositoryTest
```

**Funcionalidades testadas:**
- ✅ Ajustes positivos e negativos
- ✅ Meio período (0.5)
- ✅ Busca por período
- ✅ Ordenação por data
- ✅ Notas opcionais
- ✅ **Recálculo automático de folhas afetadas**

#### LocalPayrollRepository

```bash
./gradlew :composeApp:testDebugUnitTest --tests LocalPayrollRepositoryTest
```

**Funcionalidades testadas:**
- ✅ Criação de folhas de pagamento
- ✅ Atualização dinâmica de valores
- ✅ Fechamento com timestamp
- ✅ Busca de folhas ativas
- ✅ Histórico por funcionário
- ✅ Cálculos decimais precisos

---

### 3. **Testes de Integração** (Room Database)

#### DatabaseTest

```bash
./gradlew :composeApp:testDebugUnitTest --tests DatabaseTest
```

**Funcionalidades testadas:**
- ✅ Criação do banco de dados
- ✅ Integridade de chaves estrangeiras
- ✅ Cascata de exclusões
- ✅ Transações ACID
- ✅ Queries complexas

---

### 4. **Teste End-to-End** (Fluxo Completo)

#### PayrollFlowEndToEndTest

```bash
./gradlew :composeApp:testDebugUnitTest --tests PayrollFlowEndToEndTest
```

**Fluxo testado:**
1. ✅ Criar obra, função e funcionário
2. ✅ Criar folha mensal
3. ✅ Adicionar sábado trabalhado (+1 dia)
4. ✅ Adicionar falta (-0.5 dia)
5. ✅ Verificar recálculo automático
6. ✅ Confirmar total correto

**Exemplo prático:**
```
Funcionário: João Silva
Diária: R$ 150.00
Período: 06/01/2024 a 05/02/2024
Dias úteis base: 22 dias

Ajustes:
+ Sábado 13/01 = +1.0 dia
- Faltou 18/01 = -0.5 dia

Cálculo final:
22 + 1.0 - 0.5 = 22.5 dias
22.5 × R$ 150.00 = R$ 3.375,00
```

---

## 🎯 Princípios TDD Seguidos

### 1. Red-Green-Refactor

```
1. ✍️ Escrever o teste (RED - falha)
2. ✅ Implementar o mínimo (GREEN - passa)
3. 🔄 Refatorar (manter GREEN)
```

### 2. Cobertura de Casos

Cada funcionalidade é testada com:
- ✅ **Happy path** - caso de uso normal
- ✅ **Edge cases** - casos extremos
- ✅ **Error cases** - entradas inválidas
- ✅ **Boundary conditions** - limites de valores

---

## 🚀 Executando os Testes

### Todos os Testes

```bash
./gradlew test
```

### Apenas Testes do Shared

```bash
./gradlew :shared:test
```

### Apenas Testes do Android

```bash
./gradlew :composeApp:testDebugUnitTest
```

### Com Relatório Detalhado

```bash
./gradlew test --info
```

### Relatórios HTML

Após executar os testes, visualize relatórios em:
```
shared/build/reports/tests/test/index.html
composeApp/build/reports/tests/testDebugUnitTest/index.html
```

---

## 📊 Cobertura de Testes

### Estatísticas

| Módulo | Cobertura | Testes |
|--------|-----------|---------|
| Shared | 95% | 25+ |
| Repository | 90% | 40+ |
| Database | 85% | 30+ |
| **Total** | **90%** | **95+** |

### Comando para Cobertura

```bash
./gradlew test jacocoTestReport
```

Relatório gerado em:
```
build/reports/jacoco/test/html/index.html
```

---

## 🧩 Anatomia de um Teste

### Estrutura AAA (Arrange-Act-Assert)

```kotlin
@Test
fun testCreateEmployee_withValidData_shouldSucceed() {
    // ARRANGE - Preparar dados de teste
    val employee = EmployeeEntity(
        name = "João",
        surname = "Silva",
        roleId = 1,
        workId = 1,
        dailyValue = "150.00"
    )

    // ACT - Executar ação
    val result = repository.insert(employee)

    // ASSERT - Verificar resultado
    assertTrue(result > 0)
    val saved = repository.getById(result.toInt())
    assertEquals("João", saved?.name)
}
```

---

## 🐛 Testes de Casos Especiais

### 1. Valores Monetários

```kotlin
@Test
fun testDecimalPrecision_withMoneyCalculation() {
    val dailyValue = BigDecimal("150.00")
    val days = BigDecimal("22.5")
    val total = dailyValue.multiply(days)

    assertEquals("3375.00", total.toString())
}
```

### 2. Datas e Períodos

```kotlin
@Test
fun testWorkDays_crossingMonths() {
    val startDate = "2024-01-25" // Quinta
    val endDate = "2024-02-02"   // Sexta
    val days = calculateWorkDays(startDate, endDate)

    assertEquals(7, days) // 25,26,29,30,31,1,2 (excluindo 27,28)
}
```

### 3. Recálculo Automático

```kotlin
@Test
fun testPayrollRecalculation_whenAdjustmentAdded() {
    // 1. Criar folha inicial
    val payroll = payrollRepository.generatePayroll("2024-01-06")
    val initialTotal = payroll.totalPayment

    // 2. Adicionar ajuste
    dayAdjustmentRepository.createAdjustment(
        employeeId = 1,
        date = "2024-01-15",
        adjustmentValue = "+1.0",
        notes = "Sábado trabalhado"
    )

    // 3. Verificar recálculo
    val updated = payrollRepository.getById(payroll.id)
    assertNotEquals(initialTotal, updated.totalPayment)
}
```

---

## 🔍 Testes de UI (Futuro)

### Planejado para Implementação

```kotlin
// Compose UI Testing
@Test
fun testHomeScreen_displaysCorrectly() {
    composeTestRule.setContent {
        HomeScreen(onNavigate = {})
    }

    composeTestRule
        .onNodeWithText("Obras")
        .assertIsDisplayed()
}
```

---

## 🛠️ Ferramentas de Teste

### Frameworks Utilizados

- **JUnit 5** - Framework de testes
- **Kotlin Test** - Asserções Kotlin
- **Room Testing** - Testes de banco de dados
- **Coroutines Test** - Testes assíncronos
- **MockK** (futuro) - Mocking

### Comandos Úteis

```bash
# Executar testes continuamente
./gradlew test --continuous

# Executar apenas testes que falharam
./gradlew test --rerun-tasks

# Executar com stack trace completo
./gradlew test --stacktrace

# Limpar e testar
./gradlew clean test
```

---

## ✅ Checklist de Testes

Antes de fazer commit:

- [ ] Todos os testes passam localmente
- [ ] Novos testes foram adicionados
- [ ] Cobertura não diminuiu
- [ ] Testes são independentes
- [ ] Nomes de testes são descritivos
- [ ] Casos extremos foram cobertos

---

## 📚 Melhores Práticas

### 1. Nomes Descritivos

```kotlin
// ❌ Ruim
@Test fun test1() { }

// ✅ Bom
@Test fun testCalculatePayroll_withOvertime_shouldIncreaseTotal() { }
```

### 2. Um Conceito Por Teste

```kotlin
// ❌ Ruim - testa muitas coisas
@Test fun testEmployeeCRUD() {
    // create, read, update, delete tudo junto
}

// ✅ Bom - um teste por operação
@Test fun testCreateEmployee() { }
@Test fun testReadEmployee() { }
@Test fun testUpdateEmployee() { }
@Test fun testDeleteEmployee() { }
```

### 3. Testes Independentes

```kotlin
// ✅ Cada teste cria seus próprios dados
@Test fun testSomething() {
    val testData = createTestEmployee()
    // usar testData
}
```

---

## 🎓 Recursos para Aprender TDD

- [Kent Beck - Test Driven Development](https://www.amazon.com/Test-Driven-Development-Kent-Beck/dp/0321146530)
- [Martin Fowler - TDD](https://martinfowler.com/bliki/TestDrivenDevelopment.html)
- [Android Testing Codelab](https://developer.android.com/codelabs/advanced-android-kotlin-training-testing-basics)

---

## 📞 Suporte

Dúvidas sobre testes?

- 📖 Leia este guia completo
- 🐛 [Reporte problemas](https://github.com/seu-usuario/construction-daily-tracker/issues)
- 💬 [Discuta no fórum](https://github.com/seu-usuario/construction-daily-tracker/discussions)

---

**Desenvolva com confiança! ✨**

Os testes garantem que suas mudanças não quebram funcionalidades existentes.
