# 🏗️ Construction Daily Tracker

> Uma solução moderna e completa para gestão de folha de pagamento na construção civil, desenvolvida com Kotlin Multiplatform e funcionando 100% offline

[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-blue.svg)](https://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose-1.9.0-green.svg)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![Room](https://img.shields.io/badge/Room-2.6.1-orange.svg)](https://developer.android.com/training/data-storage/room)

---

## 📖 Sobre o Projeto

Gerenciar a folha de pagamento na construção civil é complicado, especialmente quando você precisa cuidar de diversas obras e calcular dias trabalhados com precisão. O **Construction Daily Tracker** resolve esses desafios com uma solução local e offline.

Seja gerenciando uma pequena equipe ou supervisionando múltiplas obras, este app ajuda você a:

✅ Rastrear funcionários em diferentes obras e funções
✅ Registrar ajustes diários (horas extras, faltas, bonificações)
✅ Calcular automaticamente a folha de pagamento com precisão
✅ Gerar relatórios mensais do dia 6 ao dia 5
✅ Manter um histórico completo de todas as alterações
✅ **Funcionar 100% offline - todos os dados ficam no seu celular**

---

## 🌟 Funcionalidades Principais

### 🎯 Cálculo Inteligente de Folha
- **Cálculo automático de dias úteis** excluindo finais de semana
- **Ajustes dinâmicos** para horas extras, faltas e bonificações
- **Atualizações em tempo real** quando ajustes são adicionados ou removidos
- **Precisão financeira** com BigDecimal para valores monetários

### 📱 Aplicativo Local e Offline
- **Banco de dados local** com Room/SQLite
- **Sem necessidade de internet** - funciona completamente offline
- **Dados seguros** armazenados no dispositivo
- **Interface moderna** construída com Compose Multiplatform
- **Lógica de negócio compartilhada** entre plataformas

### 🔐 Seguro & Privado
- **Dados locais** - tudo fica no seu dispositivo
- **Armazenamento criptografado** para preferências sensíveis
- **Sem envio de dados** para servidores externos
- **Cobertura de testes** seguindo princípios TDD

### 🏗️ Gestão Multi-Obras
- Rastreie múltiplas obras simultaneamente
- Atribua funcionários a diferentes funções
- Monitore trabalho em vários projetos
- Gere relatórios específicos por obra

---

## 🚀 Início Rápido

### Pré-requisitos

- **Android Studio** (versão mais recente)
- **JDK 11** ou superior
- **Dispositivo Android** ou emulador (API 24+)

### 📱 Instalar o App

#### Opção 1: Baixar APK Pré-compilado
1. Navegue até `composeApp/build/outputs/apk/debug/`
2. Transfira `composeApp-debug.apk` para seu dispositivo
3. Habilite "Instalar de fontes desconhecidas" nas configurações
4. Instale o APK

#### Opção 2: Compilar do Código-Fonte

```bash
# Clone o repositório
git clone https://github.com/seu-usuario/construction-daily-tracker/
cd construction-daily-tracker

# Compile o APK de debug
./gradlew :composeApp:assembleDebug

# APK estará em: composeApp/build/outputs/apk/debug/composeApp-debug.apk

# Instalar via ADB (opcional)
adb install composeApp/build/outputs/apk/debug/composeApp-debug.apk
```

### 🧪 Executar Testes

```bash
# Executar todos os testes
./gradlew test

# Executar apenas testes do shared
./gradlew :shared:test

# Executar com saída detalhada
./gradlew test --info
```

---

## 🏛️ Arquitetura

Este projeto segue uma **arquitetura limpa e modular** com armazenamento local:

```
construction-daily-tracker/
├── shared/              # Lógica de negócio agnóstica de plataforma
│   ├── models/          # Modelos de dados (@Serializable)
│   └── utils/           # WorkDaysCalculator, helpers
└── composeApp/          # Aplicativo Android
    ├── commonMain/      # Código de UI compartilhado
    └── androidMain/     # Código específico do Android
        ├── database/    # Room database (entities, DAOs)
        ├── repository/  # Repositórios locais
        └── ui/          # Telas e componentes
```

### Stack Tecnológica

**Android:**
- **Compose Multiplatform** - UI declarativa moderna
- **Room Database** - Persistência local type-safe
- **SQLite** - Banco de dados embarcado
- **Kotlin Coroutines** - Operações assíncronas
- **ViewModel** - Gerenciamento de estado
- **EncryptedSharedPreferences** - Armazenamento seguro de preferências

**Compartilhado:**
- **Kotlin Serialization** - Serialização de dados
- **Kotlin Multiplatform** - Compartilhamento de código

---

## 💼 Conceitos Fundamentais

### Período de Folha de Pagamento
A folha mensal vai do **dia 6 de um mês até o dia 5 do mês seguinte**

### Ajustes Diários
Rastreie modificações diárias no cronograma de trabalho de um funcionário:
- `+1.0` para diária extra de sábado
- `-0.5` para falta de meio período
- `+0.5` para extra de meio período
- Qualquer valor de ajuste personalizado

### Recálculo Automático
Quando você adiciona ou remove ajustes diários, o sistema automaticamente recalcula a folha mensal afetada. Sem necessidade de intervenção manual!

### Calculadora de Dias Úteis
Usa manipulação de datas específica da plataforma para calcular dias úteis (apenas segunda a sexta), considerando:
- Limites de meses
- Anos bissextos
- Intervalos de datas personalizados

---

## 💾 Estrutura do Banco de Dados

### Tabelas Principais

#### Works (Obras)
```kotlin
@Entity(tableName = "works")
data class WorkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)
```

#### Roles (Funções)
```kotlin
@Entity(tableName = "roles")
data class RoleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String
)
```

#### Employees (Funcionários)
```kotlin
@Entity(tableName = "employees")
data class EmployeeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val surname: String,
    val roleId: Int,
    val workId: Int? = null,
    val dailyValue: String
)
```

#### Day Adjustments (Ajustes Diários)
```kotlin
@Entity(tableName = "day_adjustments")
data class DayAdjustmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val employeeId: Int,
    val date: String,
    val adjustmentValue: String,
    val notes: String? = null
)
```

#### Monthly Payrolls (Folhas Mensais)
```kotlin
@Entity(tableName = "monthly_payrolls")
data class MonthlyPayrollEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val employeeId: Int,
    val periodStartDate: String,
    val periodEndDate: String,
    val baseWorkdays: String,
    val finalWorkedDays: String,
    val totalPayment: String,
    val closedAt: Long? = null
)
```

---

## 🧪 Filosofia de Testes

Este projeto segue **Test-Driven Development (TDD)**:

1. ✍️ Escreva os testes primeiro
2. 🔴 Observe-os falhar
3. ✅ Implemente a funcionalidade
4. 🟢 Observe os testes passarem
5. 🔄 Refatore com confiança

**Cobertura de Testes:**
- **Testes unitários** para lógica de negócio (`WorkDaysCalculator`)
- **Testes de repositório** para camada de acesso a dados
- **Testes de integração** para fluxos de UI

---

## 🔐 Segurança e Privacidade

### Armazenamento Local Seguro
Todos os dados são armazenados localmente no dispositivo:

✅ **Dados privados** - nunca saem do seu celular
✅ **EncryptedSharedPreferences** para configurações sensíveis
✅ **Room Database** com SQLite para dados estruturados
✅ **Sem conexão com internet** necessária
✅ **Controle total** sobre seus dados

---

## 📁 Estrutura do Projeto

```
construction-daily-tracker/
├── README.md                    # Este arquivo
├── TESTING.md                   # Documentação de testes
├── gradle/                      # Configuração Gradle
├── shared/                      # Lógica de negócio compartilhada
│   ├── src/commonMain/         # Código agnóstico de plataforma
│   ├── src/jvmMain/            # Implementações específicas JVM
│   └── src/commonTest/         # Testes compartilhados
└── composeApp/                  # Aplicação Android
    ├── src/commonMain/         # UI compartilhada
    └── src/androidMain/        # Implementação Android
        ├── database/           # Room database
        ├── repository/         # Repositórios locais
        └── ui/                 # Interface do usuário
```

---

## 📱 Requisitos do Sistema

- **Android 7.0 (API 24)** ou superior
- **50 MB** de espaço livre
- **Resolução mínima:** 320x480
- **Sem necessidade de internet**

---

## 📄 Licença

Este projeto está licenciado sob a Licença MIT - consulte o arquivo LICENSE para detalhes.

---

## 🙏 Agradecimentos

Construído com ❤️ usando:
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Android Jetpack](https://developer.android.com/jetpack)

---

## 📞 Suporte

Tem dúvidas ou precisa de ajuda?

- 📖 Leia este README para instruções completas
- 🧪 Confira [TESTING.md](TESTING.md) para documentação de testes
- 🐛 [Abra uma issue](https://github.com/seu-usuario/construction-daily-tracker/issues)

---

<div align="center">

**Feito com Kotlin Multiplatform** 🚀

**Funciona 100% Offline** 📱

Se este projeto te ajudou, dê uma ⭐!

</div>
