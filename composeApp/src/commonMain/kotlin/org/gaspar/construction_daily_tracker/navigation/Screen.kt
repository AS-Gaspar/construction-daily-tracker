package org.gaspar.construction_daily_tracker.navigation

/**
 * Sealed class representing all screens in the app.
 */
sealed class Screen(val route: String, val title: String) {
    data object Home : Screen("home", "Home")
    data object Works : Screen("works", "Works")
    data object Roles : Screen("roles", "Roles")
    data object Employees : Screen("employees", "Employees")
    data object UnassignedEmployees : Screen("unassigned-employees", "Unassigned Employees")
    data object DailyAdjustments : Screen("adjustments", "Daily Work")
    data object Payroll : Screen("payroll", "Monthly Payroll")
    data object Settings : Screen("settings", "API Settings")
    data object Configuration : Screen("configuration", "Configuration")

    // Detail screens
    data class EmployeeDetail(val employeeId: Int? = null) :
        Screen("employee/${employeeId ?: "new"}", "Employee Details")

    data class EmployeeEdit(val employeeId: Int? = null) :
        Screen("employee/edit/${employeeId ?: "new"}", "Edit Employee")

    data class WorkDetail(val workId: Int? = null) :
        Screen("work/${workId ?: "new"}", "Work Details")

    data class RoleDetail(val roleId: Int? = null) :
        Screen("role/${roleId ?: "new"}", "Role Details")

    data class ClosedPayrollDetail(
        val periodStartDate: String,
        val periodEndDate: String
    ) : Screen("payroll/$periodStartDate/$periodEndDate", "Payroll Details")
}
