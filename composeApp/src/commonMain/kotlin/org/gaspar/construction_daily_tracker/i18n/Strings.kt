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
    val adjustmentsHistory: String
    val date: String
    val value: String
    val note: String
    val viewNote: String
    val noAdjustments: String
    val noteDialogTitle: String
    val noNoteAvailable: String
    val deleteAdjustment: String
    val deleteAdjustmentConfirm: String

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
    override val adjustmentsHistory = "Histórico de Ajustes"
    override val date = "Data"
    override val value = "Valor"
    override val note = "Nota"
    override val viewNote = "Ver Nota"
    override val noAdjustments = "Nenhum ajuste encontrado"
    override val noteDialogTitle = "Nota do Ajuste"
    override val noNoteAvailable = "Nenhuma nota disponível"
    override val deleteAdjustment = "Excluir Ajuste"
    override val deleteAdjustmentConfirm = "Tem certeza que deseja excluir este ajuste?"

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
 * Get Portuguese strings.
 */
fun getStrings(): Strings {
    return PortugueseStrings()
}
