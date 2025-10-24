package org.gaspar.construction_daily_tracker.i18n

/**
 * Interface for all localized strings in the app.
 */
interface Strings {
    // Common
    val back: String
    val save: String
    val cancel: String
    val delete: String
    val edit: String
    val confirm: String
    val refresh: String
    val loading: String
    val error: String
    val success: String
    val ok: String
    val yes: String
    val no: String

    // Home Screen
    val appName: String
    val homeTitle: String
    val worksTitle: String
    val worksDescription: String
    val rolesTitle: String
    val rolesDescription: String
    val employeesTitle: String
    val employeesDescription: String
    val dailyAdjustmentsTitle: String
    val dailyAdjustmentsDescription: String
    val payrollTitle: String
    val payrollDescription: String
    val settingsTitle: String
    val configurationTitle: String
    val unassignedEmployeesDescription: String

    // Works Screen
    val addWork: String
    val addNewWork: String
    val addConstructionSite: String
    val editSite: String
    val workName: String
    val enterWorkName: String
    val workNameHint: String
    val workNameRequired: String
    val noWorks: String
    val workDetails: String
    val editWork: String
    val deleteWork: String
    val deleteWorkConfirm: String
    val deleteWorkConfirmQuestion: String
    val actionCannotBeUndone: String
    val workNotFound: String
    val workInformation: String
    val work: String
    val assignedEmployees: String
    val add: String
    val deleteWorkWarning: String

    // Roles Screen
    val addRole: String
    val roleTitle: String
    val enterRoleTitle: String
    val roleTitleHint: String
    val noRoles: String

    // Employees Screen
    val addEmployee: String
    val addNewEmployee: String
    val employeeName: String
    val employeeSurname: String
    val selectRole: String
    val selectWork: String
    val dailyValue: String
    val noEmployees: String
    val employeeDetails: String
    val editEmployee: String
    val deleteEmployee: String
    val deleteEmployeeConfirm: String
    val unassignedEmployees: String
    val unassignedEmployeesCount: String
    val employee: String
    val employees: String
    val allEmployeesAssigned: String
    val noEmployeesUnassigned: String
    val employeesWithoutWork: String
    val clickToAssignWork: String
    val noUnassignedEmployees: String
    val assignToWork: String
    val optional: String
    val days: String
    val unknown: String
    val noWork: String
    val employeeNotFound: String
    val employeeInformation: String
    val profession: String
    val workSite: String
    val currentTotalDays: String
    val thisMonthWorkdays: String
    val currentTotalDaysFormula: String
    val deleteEmployeeQuestion: String
    val assignWorkDialogTitle: String
    val missingWorksAndRoles: String
    val name: String
    val surname: String

    // Day Adjustments Screen
    val addAdjustment: String
    val adjustmentDate: String
    val selectDate: String
    val adjustmentValue: String
    val selectValue: String
    val notes: String
    val notesOptional: String
    val notesPlaceholder: String
    val noDayAdjustments: String
    val addAdjustmentValue: String
    val saveAdjustment: String
    val saving: String
    val pleaseSelectDate: String
    val pleaseSelectValue: String
    val adjustmentAdded: String

    // Adjustment values
    val fullDayWork: String
    val halfDayWork: String
    val halfDayAbsence: String
    val fullDayAbsence: String
    val saturdayWork: String
    val sickLeave: String
    val dailyWorkAdjustments: String
    val addWorkAdjustment: String
    val noAdjustmentsYet: String
    val adjustmentExamples: String

    // Payroll Screen
    val currentPeriod: String
    val closedPeriods: String
    val noPayrolls: String
    val periodRange: String
    val baseWorkdays: String
    val finalWorkedDays: String
    val totalPayment: String
    val closedAt: String
    val employeeLabel: String
    val workLabel: String
    val roleLabel: String
    val generatePayroll: String
    val closePayroll: String
    val noPayrollHistory: String
    val payrollPeriodInfo: String
    val statusCurrent: String
    val statusClosed: String
    val noPayrollDataFound: String
    val closedPayrollPeriod: String
    val adjustments: String
    val noJobRoles: String
    val roleExamples: String
    val tapToViewEmployeeList: String
    val tapToViewPayrollDetails: String
    val payrollByWorks: String
    val workPayrollDetails: String
    val totalToPay: String
    val viewEmployeeDetails: String
    val noWorksInPayroll: String
    val noEmployeesInWorkPayroll: String
    val employeesInWork: String

    // Settings Screen
    val apiSettings: String
    val serverUrl: String
    val apiKey: String
    val testConnection: String
    val connectionSuccess: String
    val connectionFailed: String
    val saveSettings: String
    val languageSettings: String
    val selectLanguage: String

    // Configuration Screen
    val apiConfiguration: String
    val apiConfigurationDescription: String
    val rolesConfiguration: String
    val rolesConfigurationDescription: String
    val configurationInfo: String
    val configurationInfoDescription: String

    // Validation messages
    val required: String
    val invalidValue: String
    val mustBePositive: String

    // Date formatting
    val dateFormat: String

    // Month names
    val monthJanuary: String
    val monthFebruary: String
    val monthMarch: String
    val monthApril: String
    val monthMay: String
    val monthJune: String
    val monthJuly: String
    val monthAugust: String
    val monthSeptember: String
    val monthOctober: String
    val monthNovember: String
    val monthDecember: String
}

/**
 * English strings implementation.
 */
class EnglishStrings : Strings {
    override val back = "Back"
    override val save = "Save"
    override val cancel = "Cancel"
    override val delete = "Delete"
    override val edit = "Edit"
    override val confirm = "Confirm"
    override val refresh = "Refresh"
    override val loading = "Loading..."
    override val error = "Error"
    override val success = "Success"
    override val ok = "OK"
    override val yes = "Yes"
    override val no = "No"

    override val appName = "Construction Daily Tracker"
    override val homeTitle = "Home"
    override val worksTitle = "Works"
    override val worksDescription = "Manage construction sites"
    override val rolesTitle = "Roles"
    override val rolesDescription = "Manage job roles"
    override val employeesTitle = "Employees"
    override val employeesDescription = "Manage employees and their roles"
    override val dailyAdjustmentsTitle = "Daily Adjustments"
    override val dailyAdjustmentsDescription = "Track work adjustments"
    override val payrollTitle = "Payroll"
    override val payrollDescription = "View monthly payrolls"
    override val settingsTitle = "Settings"
    override val configurationTitle = "Configuration"
    override val unassignedEmployeesDescription = "Employees without work assignment"

    override val addWork = "Add Work"
    override val addNewWork = "Add New Work"
    override val addConstructionSite = "Add Construction Site"
    override val editSite = "Edit Site"
    override val workName = "Work Name"
    override val enterWorkName = "Enter work name"
    override val workNameHint = "e.g., Building Construction Site"
    override val workNameRequired = "Work name is required"
    override val noWorks = "No works available"
    override val workDetails = "Work Details"
    override val editWork = "Edit Work"
    override val deleteWork = "Delete Work"
    override val deleteWorkConfirm = "Are you sure you want to delete this work?"
    override val deleteWorkConfirmQuestion = "Are you sure you want to delete"
    override val actionCannotBeUndone = "This action cannot be undone."
    override val workNotFound = "Work not found"
    override val workInformation = "Work Information"
    override val work = "Work"
    override val assignedEmployees = "Assigned Employees"
    override val add = "Add"
    override val deleteWorkWarning = "⚠️ Warning: This work has employees assigned. Deleting it may affect their records."

    override val addRole = "Add Role"
    override val roleTitle = "Role Title"
    override val enterRoleTitle = "Enter role title"
    override val roleTitleHint = "e.g., Carpenter, Electrician"
    override val noRoles = "No roles available"

    override val addEmployee = "Add Employee"
    override val addNewEmployee = "Add New Employee"
    override val employeeName = "Name"
    override val employeeSurname = "Surname"
    override val selectRole = "Select Role"
    override val selectWork = "Select Work"
    override val dailyValue = "Daily Value"
    override val noEmployees = "No employees available"
    override val employeeDetails = "Employee Details"
    override val editEmployee = "Edit Employee"
    override val deleteEmployee = "Delete Employee"
    override val deleteEmployeeConfirm = "Are you sure you want to delete this employee?"
    override val unassignedEmployees = "Unassigned Employees"
    override val unassignedEmployeesCount = "unassigned"
    override val employee = "employee"
    override val employees = "employees"
    override val allEmployeesAssigned = "All employees assigned!"
    override val noEmployeesUnassigned = "No employees are currently unassigned."
    override val employeesWithoutWork = "Employees without work assignment"
    override val clickToAssignWork = "Click on an employee to assign them to a work"
    override val noUnassignedEmployees = "All employees are assigned to works"
    override val assignToWork = "Assign to Work"
    override val optional = "optional"
    override val days = "days"
    override val unknown = "Unknown"
    override val noWork = "No Work"
    override val employeeNotFound = "Employee not found"
    override val employeeInformation = "Employee Information"
    override val profession = "Profession"
    override val workSite = "Work Site"
    override val currentTotalDays = "Current Total Days"
    override val thisMonthWorkdays = "This month's workdays"
    override val currentTotalDaysFormula = "Current total days = Base workdays + Adjustments (overtime, absences, etc.)"
    override val deleteEmployeeQuestion = "Are you sure you want to delete"
    override val assignWorkDialogTitle = "Assign to Work"
    override val missingWorksAndRoles = "Please create at least one Work and one Role before adding employees."
    override val name = "Name"
    override val surname = "Surname"

    override val addAdjustment = "Add Adjustment"
    override val adjustmentDate = "Adjustment Date"
    override val selectDate = "Select date"
    override val adjustmentValue = "Adjustment Value"
    override val selectValue = "Select value"
    override val notes = "Notes"
    override val notesOptional = "Notes (optional)"
    override val notesPlaceholder = "e.g., Worked on Saturday, Half-day sick leave"
    override val noDayAdjustments = "No day adjustments available"
    override val addAdjustmentValue = "Add Adjustment Value"
    override val saveAdjustment = "Save Adjustment"
    override val saving = "Saving..."
    override val pleaseSelectDate = "Please select a date"
    override val pleaseSelectValue = "Please select an adjustment value"
    override val adjustmentAdded = "Adjustment added successfully!"

    override val fullDayWork = "+1.0 days (e.g., Saturday work)"
    override val halfDayWork = "+0.5 days (e.g., half-day work)"
    override val halfDayAbsence = "-0.5 days (e.g., half-day absence)"
    override val fullDayAbsence = "-1.0 days (e.g., full-day absence)"
    override val saturdayWork = "Saturday work"
    override val sickLeave = "Sick leave"
    override val dailyWorkAdjustments = "Daily Work Adjustments"
    override val addWorkAdjustment = "Add Work Adjustment"
    override val noAdjustmentsYet = "No adjustments yet"
    override val adjustmentExamples = "Add work adjustments like:\n• +1 for Saturday work\n• -0.5 for half-day absence"

    override val currentPeriod = "Current Period"
    override val closedPeriods = "Closed Periods"
    override val noPayrolls = "No payrolls available"
    override val periodRange = "Period"
    override val baseWorkdays = "Base Workdays"
    override val finalWorkedDays = "Final Worked Days"
    override val totalPayment = "Total Payment"
    override val closedAt = "Closed At"
    override val employeeLabel = "Employee"
    override val workLabel = "Work"
    override val roleLabel = "Role"
    override val generatePayroll = "Generate Payroll"
    override val closePayroll = "Close Payroll"
    override val noPayrollHistory = "No payroll history yet"
    override val payrollPeriodInfo = "Payroll periods will appear here once generated.\nPeriods run from 6th to 5th of each month."
    override val statusCurrent = "CURRENT"
    override val statusClosed = "CLOSED"
    override val noPayrollDataFound = "No payroll data found"
    override val closedPayrollPeriod = "Closed Payroll Period"
    override val adjustments = "Adjustments"
    override val noJobRoles = "No job roles yet"
    override val roleExamples = "Add roles like Carpenter, Electrician, etc."
    override val tapToViewEmployeeList = "→ Tap to view employee list"
    override val tapToViewPayrollDetails = "→ Tap to view payroll details"
    override val payrollByWorks = "Payroll by Works"
    override val workPayrollDetails = "Work Payroll Details"
    override val totalToPay = "Total to Pay"
    override val viewEmployeeDetails = "View Details"
    override val noWorksInPayroll = "No works with employees in this payroll period"
    override val noEmployeesInWorkPayroll = "No employees in this work for this period"
    override val employeesInWork = "Employees in Work"

    override val apiSettings = "API Settings"
    override val serverUrl = "Server URL"
    override val apiKey = "API Key"
    override val testConnection = "Test Connection"
    override val connectionSuccess = "Connection successful!"
    override val connectionFailed = "Connection failed"
    override val saveSettings = "Save Settings"
    override val languageSettings = "Language"
    override val selectLanguage = "Select Language"

    override val apiConfiguration = "API Settings"
    override val apiConfigurationDescription = "Configure server URL and API key"
    override val rolesConfiguration = "Roles"
    override val rolesConfigurationDescription = "Manage job roles (carpenter, electrician, etc.)"
    override val configurationInfo = "Configuration"
    override val configurationInfoDescription = "Manage app settings and advanced features here."

    override val required = "Required"
    override val invalidValue = "Invalid value"
    override val mustBePositive = "Must be positive"

    override val dateFormat = "yyyy-MM-dd"

    override val monthJanuary = "January"
    override val monthFebruary = "February"
    override val monthMarch = "March"
    override val monthApril = "April"
    override val monthMay = "May"
    override val monthJune = "June"
    override val monthJuly = "July"
    override val monthAugust = "August"
    override val monthSeptember = "September"
    override val monthOctober = "October"
    override val monthNovember = "November"
    override val monthDecember = "December"
}

/**
 * Brazilian Portuguese strings implementation.
 */
class PortugueseStrings : Strings {
    override val back = "Voltar"
    override val save = "Salvar"
    override val cancel = "Cancelar"
    override val delete = "Excluir"
    override val edit = "Editar"
    override val confirm = "Confirmar"
    override val refresh = "Atualizar"
    override val loading = "Carregando..."
    override val error = "Erro"
    override val success = "Sucesso"
    override val ok = "OK"
    override val yes = "Sim"
    override val no = "Não"

    override val appName = "Controle Diário de Obras"
    override val homeTitle = "Início"
    override val worksTitle = "Obras"
    override val worksDescription = "Gerenciar canteiros de obras"
    override val rolesTitle = "Funções"
    override val rolesDescription = "Gerenciar funções de trabalho"
    override val employeesTitle = "Funcionários"
    override val employeesDescription = "Gerenciar funcionários e suas funções"
    override val dailyAdjustmentsTitle = "Ajustes Diários"
    override val dailyAdjustmentsDescription = "Rastrear ajustes de trabalho"
    override val payrollTitle = "Folha de Pagamento"
    override val payrollDescription = "Ver folhas de pagamento mensais"
    override val settingsTitle = "Configurações"
    override val configurationTitle = "Configuração"
    override val unassignedEmployeesDescription = "Funcionários sem alocação em obras"

    override val addWork = "Adicionar Obra"
    override val addNewWork = "Adicionar Nova Obra"
    override val addConstructionSite = "Adicionar Canteiro de Obra"
    override val editSite = "Editar Obra"
    override val workName = "Nome da Obra"
    override val enterWorkName = "Digite o nome da obra"
    override val workNameHint = "ex: Construção Edifício Comercial"
    override val workNameRequired = "Nome da obra é obrigatório"
    override val noWorks = "Nenhuma obra disponível"
    override val workDetails = "Detalhes da Obra"
    override val editWork = "Editar Obra"
    override val deleteWork = "Excluir Obra"
    override val deleteWorkConfirm = "Tem certeza que deseja excluir esta obra?"
    override val deleteWorkConfirmQuestion = "Tem certeza que deseja excluir"
    override val actionCannotBeUndone = "Esta ação não pode ser desfeita."
    override val workNotFound = "Obra não encontrada"
    override val workInformation = "Informações da Obra"
    override val work = "Obra"
    override val assignedEmployees = "Funcionários Alocados"
    override val add = "Adicionar"
    override val deleteWorkWarning = "⚠️ Aviso: Esta obra tem funcionários alocados. Excluí-la pode afetar seus registros."

    override val addRole = "Adicionar Função"
    override val roleTitle = "Título da Função"
    override val enterRoleTitle = "Digite o título da função"
    override val roleTitleHint = "ex: Pedreiro, Eletricista"
    override val noRoles = "Nenhuma função disponível"

    override val addEmployee = "Adicionar Funcionário"
    override val addNewEmployee = "Adicionar Novo Funcionário"
    override val employeeName = "Nome"
    override val employeeSurname = "Sobrenome"
    override val selectRole = "Selecionar Função"
    override val selectWork = "Selecionar Obra"
    override val dailyValue = "Valor Diário"
    override val noEmployees = "Nenhum funcionário disponível"
    override val employeeDetails = "Detalhes do Funcionário"
    override val editEmployee = "Editar Funcionário"
    override val deleteEmployee = "Excluir Funcionário"
    override val deleteEmployeeConfirm = "Tem certeza que deseja excluir este funcionário?"
    override val unassignedEmployees = "Funcionários Sem Obra"
    override val unassignedEmployeesCount = "sem obra"
    override val employee = "funcionário"
    override val employees = "funcionários"
    override val allEmployeesAssigned = "Todos os funcionários alocados!"
    override val noEmployeesUnassigned = "Não há funcionários sem alocação no momento."
    override val employeesWithoutWork = "Funcionários sem alocação em obra"
    override val clickToAssignWork = "Clique em um funcionário para alocá-lo em uma obra"
    override val noUnassignedEmployees = "Todos os funcionários estão alocados em obras"
    override val assignToWork = "Alocar em Obra"
    override val optional = "opcional"
    override val days = "dias"
    override val unknown = "Desconhecido"
    override val noWork = "Sem Obra"
    override val employeeNotFound = "Funcionário não encontrado"
    override val employeeInformation = "Informações do Funcionário"
    override val profession = "Profissão"
    override val workSite = "Obra"
    override val currentTotalDays = "Total de Dias Atual"
    override val thisMonthWorkdays = "Dias trabalhados deste mês"
    override val currentTotalDaysFormula = "Total de dias atual = Dias úteis base + Ajustes (horas extras, faltas, etc.)"
    override val deleteEmployeeQuestion = "Tem certeza que deseja excluir"
    override val assignWorkDialogTitle = "Alocar em Obra"
    override val missingWorksAndRoles = "Por favor, crie pelo menos uma Obra e uma Função antes de adicionar funcionários."
    override val name = "Nome"
    override val surname = "Sobrenome"

    override val addAdjustment = "Adicionar Ajuste"
    override val adjustmentDate = "Data do Ajuste"
    override val selectDate = "Selecionar data"
    override val adjustmentValue = "Valor do Ajuste"
    override val selectValue = "Selecionar valor"
    override val notes = "Observações"
    override val notesOptional = "Observações (opcional)"
    override val notesPlaceholder = "ex: Trabalhou no sábado, Faltou meio período"
    override val noDayAdjustments = "Nenhum ajuste diário disponível"
    override val addAdjustmentValue = "Adicionar Valor de Ajuste"
    override val saveAdjustment = "Salvar Ajuste"
    override val saving = "Salvando..."
    override val pleaseSelectDate = "Por favor, selecione uma data"
    override val pleaseSelectValue = "Por favor, selecione um valor de ajuste"
    override val adjustmentAdded = "Ajuste adicionado com sucesso!"

    override val fullDayWork = "+1.0 dias (ex: trabalho no sábado)"
    override val halfDayWork = "+0.5 dias (ex: meio período de trabalho)"
    override val halfDayAbsence = "-0.5 dias (ex: falta meio período)"
    override val fullDayAbsence = "-1.0 dias (ex: falta dia completo)"
    override val saturdayWork = "Trabalho no sábado"
    override val sickLeave = "Atestado médico"
    override val dailyWorkAdjustments = "Ajustes Diários de Trabalho"
    override val addWorkAdjustment = "Adicionar Ajuste de Trabalho"
    override val noAdjustmentsYet = "Nenhum ajuste ainda"
    override val adjustmentExamples = "Adicione ajustes de trabalho como:\n• +1 para trabalho no sábado\n• -0.5 para falta meio período"

    override val currentPeriod = "Período Atual"
    override val closedPeriods = "Períodos Fechados"
    override val noPayrolls = "Nenhuma folha de pagamento disponível"
    override val periodRange = "Período"
    override val baseWorkdays = "Dias Úteis Base"
    override val finalWorkedDays = "Dias Trabalhados Final"
    override val totalPayment = "Pagamento Total"
    override val closedAt = "Fechado Em"
    override val employeeLabel = "Funcionário"
    override val workLabel = "Obra"
    override val roleLabel = "Função"
    override val generatePayroll = "Gerar Folha de Pagamento"
    override val closePayroll = "Fechar Folha de Pagamento"
    override val noPayrollHistory = "Nenhum histórico de folha de pagamento ainda"
    override val payrollPeriodInfo = "Os períodos de folha de pagamento aparecerão aqui após serem gerados.\nPeríodos vão do dia 6 ao dia 5 de cada mês."
    override val statusCurrent = "ATUAL"
    override val statusClosed = "FECHADO"
    override val noPayrollDataFound = "Nenhum dado de folha de pagamento encontrado"
    override val closedPayrollPeriod = "Período de Folha de Pagamento Fechado"
    override val adjustments = "Ajustes"
    override val noJobRoles = "Nenhuma função ainda"
    override val roleExamples = "Adicione funções como Pedreiro, Eletricista, etc."
    override val tapToViewEmployeeList = "→ Toque para ver a lista de funcionários"
    override val tapToViewPayrollDetails = "→ Toque para ver os detalhes da folha de pagamento"
    override val payrollByWorks = "Folha de Pagamento por Obras"
    override val workPayrollDetails = "Detalhes da Folha de Pagamento da Obra"
    override val totalToPay = "Total a Pagar"
    override val viewEmployeeDetails = "Ver Detalhes"
    override val noWorksInPayroll = "Nenhuma obra com funcionários neste período de pagamento"
    override val noEmployeesInWorkPayroll = "Nenhum funcionário nesta obra para este período"
    override val employeesInWork = "Funcionários na Obra"

    override val apiSettings = "Configurações da API"
    override val serverUrl = "URL do Servidor"
    override val apiKey = "Chave da API"
    override val testConnection = "Testar Conexão"
    override val connectionSuccess = "Conexão bem-sucedida!"
    override val connectionFailed = "Falha na conexão"
    override val saveSettings = "Salvar Configurações"
    override val languageSettings = "Idioma"
    override val selectLanguage = "Selecionar Idioma"

    override val apiConfiguration = "Configurações da API"
    override val apiConfigurationDescription = "Configurar URL do servidor e chave da API"
    override val rolesConfiguration = "Funções"
    override val rolesConfigurationDescription = "Gerenciar funções de trabalho (pedreiro, eletricista, etc.)"
    override val configurationInfo = "Configuração"
    override val configurationInfoDescription = "Gerenciar configurações do aplicativo e recursos avançados aqui."

    override val required = "Obrigatório"
    override val invalidValue = "Valor inválido"
    override val mustBePositive = "Deve ser positivo"

    override val dateFormat = "dd/MM/yyyy"

    override val monthJanuary = "Janeiro"
    override val monthFebruary = "Fevereiro"
    override val monthMarch = "Março"
    override val monthApril = "Abril"
    override val monthMay = "Maio"
    override val monthJune = "Junho"
    override val monthJuly = "Julho"
    override val monthAugust = "Agosto"
    override val monthSeptember = "Setembro"
    override val monthOctober = "Outubro"
    override val monthNovember = "Novembro"
    override val monthDecember = "Dezembro"
}

/**
 * Get strings for the specified language.
 */
fun getStrings(language: Language): Strings {
    return when (language) {
        Language.ENGLISH -> EnglishStrings()
        Language.PORTUGUESE -> PortugueseStrings()
    }
}
