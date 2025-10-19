package org.gaspar.construction_daily_tracker.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import org.gaspar.construction_daily_tracker.model.*

/**
 * Repository for Role-related API calls.
 */
class RoleRepository(private val client: HttpClient) {
    suspend fun getAllRoles(): List<Role> = client.get("/roles").body()
    suspend fun getRoleById(id: Int): Role = client.get("/roles/$id").body()
    suspend fun createRole(title: String): Role =
        client.post("/roles") { setBody(mapOf("title" to title)) }.body()
    suspend fun updateRole(id: Int, title: String): Role =
        client.put("/roles/$id") { setBody(mapOf("title" to title)) }.body()
    suspend fun deleteRole(id: Int) = client.delete("/roles/$id")
}

/**
 * Repository for Employee-related API calls.
 */
class EmployeeRepository(private val client: HttpClient) {
    suspend fun getAllEmployees(): List<Employee> = client.get("/employees").body()
    suspend fun getEmployeeById(id: Int): Employee = client.get("/employees/$id").body()
    suspend fun createEmployee(
        name: String,
        surname: String,
        roleId: Int,
        workId: Int,
        dailyValue: String
    ): Employee = client.post("/employees") {
        setBody(mapOf(
            "name" to name,
            "surname" to surname,
            "roleId" to roleId,
            "workId" to workId,
            "dailyValue" to dailyValue
        ))
    }.body()
    suspend fun updateEmployee(
        id: Int,
        name: String,
        surname: String,
        roleId: Int,
        workId: Int,
        dailyValue: String
    ): Employee = client.put("/employees/$id") {
        setBody(mapOf(
            "name" to name,
            "surname" to surname,
            "roleId" to roleId,
            "workId" to workId,
            "dailyValue" to dailyValue
        ))
    }.body()
    suspend fun deleteEmployee(id: Int) = client.delete("/employees/$id")
}

/**
 * Repository for DayAdjustment-related API calls.
 */
class DayAdjustmentRepository(private val client: HttpClient) {
    suspend fun getAllAdjustments(): List<DayAdjustment> = client.get("/day-adjustments").body()
    suspend fun getAdjustmentById(id: Int): DayAdjustment = client.get("/day-adjustments/$id").body()
    suspend fun createAdjustment(
        employeeId: Int,
        date: String,
        adjustmentValue: String,
        notes: String?
    ): DayAdjustment = client.post("/day-adjustments") {
        setBody(buildMap {
            put("employeeId", employeeId)
            put("date", date)
            put("adjustmentValue", adjustmentValue)
            notes?.let { put("notes", it) }
        })
    }.body()
    suspend fun deleteAdjustment(id: Int) = client.delete("/day-adjustments/$id")
}

/**
 * Repository for MonthlyPayroll-related API calls.
 */
class PayrollRepository(private val client: HttpClient) {
    suspend fun getAllPayrolls(): List<MonthlyPayroll> = client.get("/monthly-payrolls").body()
    suspend fun getPayrollById(id: Int): MonthlyPayroll = client.get("/monthly-payrolls/$id").body()
    suspend fun generatePayroll(periodStartDate: String): List<MonthlyPayroll> =
        client.post("/monthly-payrolls/generate") {
            setBody(mapOf("periodStartDate" to periodStartDate))
        }.body()
    suspend fun closePayroll(id: Int): MonthlyPayroll =
        client.put("/monthly-payrolls/$id/close").body()
}
