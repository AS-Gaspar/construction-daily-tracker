# 🏗️ Construction Daily Tracker

> Uma solução moderna e completa para gestão de folha de pagamento na construção civil, desenvolvida com Kotlin Multiplatform

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.20-blue.svg)](https://kotlinlang.org)
[![Ktor](https://img.shields.io/badge/Ktor-3.3.0-orange.svg)](https://ktor.io)
[![Compose Multiplatform](https://img.shields.io/badge/Compose-1.9.0-green.svg)](https://www.jetbrains.com/lp/compose-multiplatform/)

---

## 📖 Sobre o Projeto

Gerenciar a folha de pagamento na construção civil é complexo—especialmente quando você precisa rastrear ajustes diários, calcular dias úteis com precisão e lidar com períodos de pagamento únicos do setor (do dia 6 ao dia 5). O **Construction Daily Tracker** resolve esses desafios com uma solução poderosa e multiplataforma que funciona em Android e ambientes servidor.

Seja gerenciando uma pequena equipe ou supervisionando múltiplas obras, este app ajuda você a:

✅ Rastrear funcionários em diferentes obras e funções
✅ Registrar ajustes diários (horas extras, faltas, bonificações)
✅ Calcular automaticamente a folha de pagamento com precisão
✅ Gerar relatórios mensais do dia 6 ao dia 5
✅ Manter um histórico completo de todas as alterações

---

## 🌟 Funcionalidades Principais

### 🎯 Cálculo Inteligente de Folha
- **Cálculo automático de dias úteis** excluindo finais de semana
- **Ajustes dinâmicos** para horas extras, faltas e bonificações
- **Atualizações em tempo real** quando ajustes são adicionados ou removidos
- **Precisão financeira** com BigDecimal para valores monetários

### 📱 Arquitetura Multiplataforma
- **App Android** construído com Compose Multiplatform
- **API REST** backend desenvolvida com Ktor
- **Lógica de negócio compartilhada** entre todas as plataformas
- **Modelos type-safe** com Kotlin Serialization

### 🔐 Seguro & Confiável
- **Autenticação por API key** para todos os endpoints
- **Migrações de banco de dados** com Flyway
- **Segurança transacional** com PostgreSQL
- **Cobertura abrangente de testes** seguindo princípios TDD

### 🏗️ Gestão Multi-Obras
- Rastreie múltiplas obras simultaneamente
- Atribua funcionários a diferentes funções
- Monitore trabalho em vários projetos
- Gere relatórios específicos por obra

---

## 🚀 Início Rápido

### Pré-requisitos

- **Java 11** ou superior
- **PostgreSQL** (para produção)
- **Android Studio** (para desenvolvimento mobile)
- **Gradle** 8.x (wrapper incluído)

### 🖥️ Executar o Servidor

```bash
# Clone o repositório
git clone <url-do-seu-repo>
cd construction-daily-tracker

# Configure variáveis de ambiente (opcional)
export DB_URL=jdbc:postgresql://localhost:5432/construction_tracker
export DB_USER=postgres
export DB_PASSWORD=postgres
export API_KEY=sua-chave-api-segura

# Execute o servidor
./gradlew :server:run
```

A API estará disponível em `http://localhost:8080`

### 📱 Compilar o App Android

```bash
# Compilar APK de debug
./gradlew :composeApp:assembleDebug

# APK estará em: composeApp/build/outputs/apk/debug/
```

### 🧪 Executar Testes

```bash
# Executar todos os testes
./gradlew test

# Executar apenas testes do servidor
./gradlew :server:test

# Executar com saída detalhada
./gradlew test --info
```

Visualize relatórios detalhados em: `server/build/reports/tests/test/index.html`

---

## 🏛️ Arquitetura

Este projeto segue uma **arquitetura limpa e modular** projetada para escalabilidade e manutenibilidade:

```
construction-daily-tracker/
├── shared/              # Lógica de negócio agnóstica de plataforma
│   ├── models/          # Modelos de dados (@Serializable)
│   └── utils/           # WorkDaysCalculator, helpers
├── server/              # Backend Ktor (REST API)
│   ├── routes/          # Endpoints da API
│   ├── repository/      # Camada de acesso a dados
│   └── database/        # Tabelas, migrações
└── composeApp/          # UI Android (Compose)
    ├── commonMain/      # Código de UI compartilhado
    └── androidMain/     # Código específico do Android
```

### Stack Tecnológica

**Backend:**
- **Ktor 3.3.0** - Framework web assíncrono moderno
- **Exposed ORM** - Framework SQL type-safe
- **PostgreSQL** - Banco de dados de produção
- **H2** - Banco de dados em memória para testes
- **Flyway** - Migrações de banco de dados
- **HikariCP** - Pool de conexões

**Frontend:**
- **Compose Multiplatform** - UI declarativa
- **Kotlin Coroutines** - Operações assíncronas
- **Ktor Client** - Networking HTTP

**Compartilhado:**
- **Kotlin Serialization** - JSON type-safe
- **Kotlin Multiplatform** - Compartilhamento de código

---

## 💼 Conceitos Fundamentais

### Período de Folha de Pagamento
A folha mensal vai do **dia 6 de um mês até o dia 5 do mês seguinte**—não segue o mês calendário padrão. Isso se alinha com práticas comuns da indústria da construção civil.

### Ajustes Diários
Rastreie modificações diárias no cronograma de trabalho de um funcionário:
- `+1.0` para hora extra no sábado
- `-0.5` para falta de meio período
- `+0.5` para bonificação de meio período
- Qualquer valor de ajuste personalizado

### Recálculo Automático
Quando você adiciona ou remove ajustes diários, o sistema automaticamente recalcula a folha mensal afetada. Sem necessidade de intervenção manual!

### Calculadora de Dias Úteis
Usa manipulação de datas específica da plataforma para calcular dias úteis (apenas segunda a sexta), considerando:
- Limites de meses
- Anos bissextos
- Intervalos de datas personalizados

---

## 📚 Documentação da API

Todos os endpoints requerem o header `X-API-Key` (exceto a raiz `/`).

### 🏢 Works (Obras)
```http
GET    /works           # Listar todas as obras
POST   /works           # Criar nova obra
GET    /works/:id       # Obter detalhes da obra
PUT    /works/:id       # Atualizar obra
DELETE /works/:id       # Deletar obra
```

### 👷 Employees (Funcionários)
```http
GET    /employees                    # Listar todos os funcionários
POST   /employees                    # Criar funcionário
GET    /employees/:id                # Obter detalhes do funcionário
PUT    /employees/:id                # Atualizar funcionário
DELETE /employees/:id                # Deletar funcionário
GET    /employees/work/:workId       # Obter funcionários por obra
```

### 📅 Day Adjustments (Ajustes Diários)
```http
GET    /day-adjustments                         # Listar todos os ajustes
POST   /day-adjustments                         # Criar ajuste
GET    /day-adjustments/:id                     # Obter ajuste
DELETE /day-adjustments/:id                     # Deletar ajuste
GET    /day-adjustments/employee/:employeeId    # Obter por funcionário
```

### 💰 Monthly Payrolls (Folhas Mensais)
```http
GET    /monthly-payrolls                    # Listar todas as folhas
POST   /monthly-payrolls                    # Gerar folha de pagamento
GET    /monthly-payrolls/:id                # Obter detalhes da folha
GET    /monthly-payrolls/employee/:id       # Obter folhas do funcionário
```

Para exemplos completos da API, consulte `ANDROID_API_SETUP.md`.

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
- **Testes de integração** para rotas da API
- **Testes end-to-end** para fluxos completos

Consulte `TESTING.md` para documentação completa sobre testes.

---

## 🔐 Segurança

### Autenticação por API Key
Todos os endpoints da API são protegidos com autenticação por chave estática:

```bash
# Defina sua API key
export API_KEY="sua-chave-de-producao-aqui"

# Inclua em todas as requisições
curl -H "X-API-Key: sua-chave-de-producao-aqui" http://localhost:8080/works
```

**Melhores Práticas de Segurança:**
- ✅ Altere a API key padrão antes da produção
- ✅ Armazene chaves com segurança (EncryptedSharedPreferences no Android)
- ✅ Use HTTPS em produção
- ✅ Rotacione chaves periodicamente

Consulte `auth/ApiKeyAuth.kt` para detalhes de implementação.

---

## 🤝 Como Contribuir

Contribuições são bem-vindas! Veja como começar:

1. **Faça um fork do repositório**
2. **Crie uma branch de feature**: `git checkout -b feature/funcionalidade-incrivel`
3. **Escreva os testes primeiro** (abordagem TDD)
4. **Implemente sua funcionalidade**
5. **Execute os testes**: `./gradlew test`
6. **Commit suas alterações**: `git commit -m 'Adiciona funcionalidade incrível'`
7. **Faça push para a branch**: `git push origin feature/funcionalidade-incrivel`
8. **Abra um Pull Request**

### Diretrizes de Desenvolvimento

- Siga os princípios TDD—testes antes da implementação
- Use mensagens de commit significativas
- Mantenha as funções pequenas e focadas
- Documente lógica de negócio complexa
- Atualize `CLAUDE.md` para mudanças arquiteturais

---

## 📁 Estrutura do Projeto

```
construction-daily-tracker/
├── CLAUDE.md                    # Diretrizes para assistente IA
├── TESTING.md                   # Documentação de testes
├── ANDROID_API_SETUP.md         # Guia de configuração Android
├── gradle/                      # Configuração Gradle
├── shared/                      # Lógica de negócio compartilhada
│   ├── src/commonMain/         # Código agnóstico de plataforma
│   ├── src/jvmMain/            # Implementações específicas JVM
│   └── src/commonTest/         # Testes compartilhados
├── server/                      # Backend Ktor
│   ├── src/main/kotlin/        # Implementação do servidor
│   ├── src/main/resources/     # Configurações, migrações
│   └── src/test/kotlin/        # Testes do servidor
└── composeApp/                  # Aplicação Android
    ├── src/commonMain/         # UI compartilhada
    └── src/androidMain/        # UI específica do Android
```

---

## 📄 Licença

Este projeto está licenciado sob a Licença MIT - consulte o arquivo LICENSE para detalhes.

---

## 🙏 Agradecimentos

Construído com ❤️ usando:
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Ktor](https://ktor.io/)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Exposed ORM](https://github.com/JetBrains/Exposed)

---

## 📞 Suporte

Tem dúvidas ou precisa de ajuda?

- 📖 Leia o [CLAUDE.md](CLAUDE.md) para diretrizes de desenvolvimento
- 🧪 Confira [TESTING.md](TESTING.md) para documentação de testes
- 📱 Veja [ANDROID_API_SETUP.md](ANDROID_API_SETUP.md) para configuração mobile
- 🐛 [Abra uma issue](https://github.com/seu-usuario/construction-daily-tracker/issues)

---

<div align="center">

**Feito com Kotlin Multiplatform** 🚀

Se este projeto te ajudou, dê uma ⭐!

</div>
