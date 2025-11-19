# 📱 Guia de Configuração do Aplicativo Android

> Guia completo para compilar, instalar e usar o Construction Daily Tracker em seu dispositivo Android

---

## 📋 Pré-requisitos

### Software Necessário
- **Android Studio** (última versão estável)
- **JDK 11** ou superior
- **Git** para clonar o repositório

### Dispositivo Android
- **Android 7.0 (API 24)** ou superior
- **50 MB** de espaço livre
- **USB Debugging** habilitado (para instalação via ADB)

---

## 🚀 Compilando o Aplicativo

### 1. Clonar o Repositório

```bash
git clone https://github.com/seu-usuario/construction-daily-tracker.git
cd construction-daily-tracker
```

### 2. Compilar APK de Debug

```bash
# Usando o Gradle Wrapper
./gradlew :composeApp:assembleDebug

# O APK será gerado em:
# composeApp/build/outputs/apk/debug/composeApp-debug.apk
```

### 3. Compilar APK de Release

```bash
# Para versão de produção (requer keystore configurado)
./gradlew :composeApp:assembleRelease

# O APK será gerado em:
# composeApp/build/outputs/apk/release/composeApp-release.apk
```

---

## 📲 Instalando o Aplicativo

### Método 1: Via ADB (Recomendado para Desenvolvedores)

```bash
# 1. Conecte seu dispositivo via USB
# 2. Habilite USB Debugging no dispositivo
# 3. Execute o comando:

adb install composeApp/build/outputs/apk/debug/composeApp-debug.apk

# Para reinstalar sobre versão existente:
adb install -r composeApp/build/outputs/apk/debug/composeApp-debug.apk
```

### Método 2: Transferência Manual

1. Transfira o arquivo APK para seu dispositivo Android
2. No dispositivo, vá em **Configurações > Segurança**
3. Habilite **Instalar de fontes desconhecidas**
4. Use o gerenciador de arquivos para localizar o APK
5. Toque no arquivo para instalar

### Método 3: Via Android Studio

1. Abra o projeto no Android Studio
2. Conecte seu dispositivo ou inicie um emulador
3. Clique em **Run** (ou pressione Shift+F10)
4. Selecione seu dispositivo na lista
5. O app será compilado e instalado automaticamente

---

## 🎯 Primeiro Uso

### Iniciando o App

1. **Abra o aplicativo** no seu dispositivo
2. A tela inicial será exibida imediatamente
3. **Nenhuma configuração necessária** - tudo funciona offline!

### Telas Principais

#### 🏠 Home (Tela Inicial)
- Ponto de partida do aplicativo
- Navegação rápida para todas as funcionalidades
- Menu com acesso a:
  - Obras
  - Funcionários
  - Ajustes Diários
  - Folha de Pagamento
  - Configurações

#### 🏢 Obras
- Lista de todas as obras cadastradas
- Adicionar nova obra com botão (+)
- Tocar em uma obra para ver detalhes
- Editar ou excluir obras existentes
- Ver funcionários alocados em cada obra

#### 👷 Funcionários
- Lista completa de funcionários
- Adicionar novo funcionário com:
  - Nome e sobrenome
  - Função (Role)
  - Obra associada (opcional)
  - Valor da diária
- Editar informações de funcionários
- Ver histórico de ajustes e folhas

#### 📅 Ajustes Diários
- Registrar extras e faltas
- Valores positivos (+) para horas extras
- Valores negativos (-) para faltas
- Adicionar notas explicativas
- Atualização automática das folhas

#### 💰 Folha de Pagamento
- Gerar folhas mensais (dia 6 ao dia 5)
- Cálculo automático de dias úteis
- Inclusão automática de ajustes
- Visualizar folhas por período
- Exportar relatórios

#### ⚙️ Configurações
- Alterar idioma (Português/Inglês)
- Informações da versão do app
- Gerenciar preferências

---

## 💡 Funcionalidades Principais

### 1. Gerenciar Obras

```
Home → Obras → Adicionar (+)
```

- Digite o nome da obra
- Confirme para salvar
- A obra aparecerá na lista imediatamente

### 2. Cadastrar Funcionários

```
Home → Funcionários → Adicionar (+)
```

- **Nome:** Primeiro nome do funcionário
- **Sobrenome:** Sobrenome completo
- **Função:** Selecione de uma lista (Pedreiro, Servente, etc.)
- **Obra:** Atribua a uma obra (opcional)
- **Valor Diário:** Digite o valor da diária (ex: 150.00)

### 3. Registrar Ajustes Diários

```
Home → Ajustes Diários → Adicionar (+)
```

Exemplos de ajustes:
- **+1.0** - Trabalhou sábado (dia extra completo)
- **-0.5** - Faltou meio período
- **+0.5** - Hora extra de meio período
- **-1.0** - Faltou o dia inteiro

### 4. Gerar Folha de Pagamento

```
Home → Folha de Pagamento → Gerar
```

- Selecione o dia inicial do período (normalmente dia 6)
- O sistema calcula automaticamente:
  - Dias úteis do período
  - Ajustes aplicados
  - Total a pagar por funcionário

---

## 🗄️ Armazenamento de Dados

### Banco de Dados Local

Todos os dados são armazenados localmente em um banco SQLite:

```
/data/data/org.gaspar.construction_daily_tracker/databases/
└── construction_daily_tracker.db
```

### Backup Manual

Para fazer backup dos dados:

```bash
# Com o dispositivo conectado via USB
adb backup -f backup.ab org.gaspar.construction_daily_tracker

# Para restaurar
adb restore backup.ab
```

### Limpeza de Dados

Para limpar todos os dados do app:

```
Configurações → Apps → Construction Daily Tracker → Limpar dados
```

⚠️ **Atenção:** Isso apagará TODOS os dados permanentemente!

---

## 🔧 Resolução de Problemas

### App não instala

**Erro: "App not installed"**
- Desinstale versão anterior primeiro
- Verifique se tem espaço disponível
- Use `adb install -r` para reinstalar

### App fecha inesperadamente

1. Limpe o cache do app:
   ```
   Configurações → Apps → Construction Daily Tracker → Limpar cache
   ```

2. Reinstale o app:
   ```bash
   adb uninstall org.gaspar.construction_daily_tracker
   adb install composeApp-debug.apk
   ```

### Dados não aparecem

- Verifique se os dados foram salvos corretamente
- Reinicie o aplicativo
- Verifique se há espaço suficiente no dispositivo

### Erros de compilação

```bash
# Limpe o projeto
./gradlew clean

# Recompile
./gradlew :composeApp:assembleDebug
```

---

## 🧪 Modo de Desenvolvimento

### Logs do Aplicativo

Para ver logs em tempo real:

```bash
adb logcat | grep "ConstructionTracker"
```

### Debug via Android Studio

1. Abra o projeto no Android Studio
2. Coloque breakpoints no código
3. Execute em modo Debug (Shift+F9)
4. Interaja com o app para acionar os breakpoints

### Inspecionar Banco de Dados

```bash
# Extrair banco de dados
adb pull /data/data/org.gaspar.construction_daily_tracker/databases/construction_daily_tracker.db

# Abrir com SQLite Browser
sqlite3 construction_daily_tracker.db

# Ver tabelas
.tables

# Ver conteúdo
SELECT * FROM works;
SELECT * FROM employees;
SELECT * FROM monthly_payrolls;
```

---

## 📊 Estrutura do Banco de Dados

### Tabelas Principais

```sql
-- Obras
CREATE TABLE works (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL
);

-- Funções
CREATE TABLE roles (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL
);

-- Funcionários
CREATE TABLE employees (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    surname TEXT NOT NULL,
    roleId INTEGER NOT NULL,
    workId INTEGER,
    dailyValue TEXT NOT NULL,
    FOREIGN KEY (roleId) REFERENCES roles(id),
    FOREIGN KEY (workId) REFERENCES works(id)
);

-- Ajustes Diários
CREATE TABLE day_adjustments (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    employeeId INTEGER NOT NULL,
    date TEXT NOT NULL,
    adjustmentValue TEXT NOT NULL,
    notes TEXT,
    FOREIGN KEY (employeeId) REFERENCES employees(id)
);

-- Folhas Mensais
CREATE TABLE monthly_payrolls (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    employeeId INTEGER NOT NULL,
    periodStartDate TEXT NOT NULL,
    periodEndDate TEXT NOT NULL,
    baseWorkdays TEXT NOT NULL,
    finalWorkedDays TEXT NOT NULL,
    totalPayment TEXT NOT NULL,
    closedAt INTEGER,
    FOREIGN KEY (employeeId) REFERENCES employees(id)
);
```

---

## 🔐 Segurança

### Permissões do App

O aplicativo **NÃO** solicita nenhuma permissão especial:
- ❌ Sem acesso à internet
- ❌ Sem acesso à localização
- ❌ Sem acesso à câmera
- ❌ Sem acesso aos contatos
- ✅ Apenas armazenamento local

### Privacidade dos Dados

- Todos os dados ficam **somente no dispositivo**
- Nenhum dado é enviado para servidores externos
- Preferências são armazenadas com criptografia
- Você tem controle total sobre seus dados

---

## 🚀 Atualizações Futuras

Planejado para próximas versões:
- Exportação de relatórios em PDF
- Backup automático na nuvem (opcional)
- Gráficos e estatísticas
- Suporte a múltiplos usuários
- Sincronização entre dispositivos

---

## 📞 Suporte

Encontrou um problema ou tem sugestões?

- 🐛 [Reportar Bug](https://github.com/seu-usuario/construction-daily-tracker/issues)
- 💡 [Sugerir Funcionalidade](https://github.com/seu-usuario/construction-daily-tracker/discussions)
- 📧 Email: suporte@exemplo.com

---

## ✅ Checklist de Configuração

- [ ] Android Studio instalado
- [ ] JDK 11+ instalado
- [ ] Repositório clonado
- [ ] APK compilado com sucesso
- [ ] App instalado no dispositivo
- [ ] Primeira obra cadastrada
- [ ] Primeiro funcionário adicionado
- [ ] Primeiro ajuste registrado
- [ ] Primeira folha gerada

---

**Pronto para começar! 🎉**

O app está totalmente funcional e pronto para uso offline.
