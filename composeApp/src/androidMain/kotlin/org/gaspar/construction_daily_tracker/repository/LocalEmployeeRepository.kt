package org.gaspar.construction_daily_tracker.repository

import kotlinx.coroutines.flow.first
import org.gaspar.construction_daily_tracker.database.AppDatabase
import org.gaspar.construction_daily_tracker.database.entities.EmployeeEntity
import org.gaspar.construction_daily_tracker.model.Employee

class LocalEmployeeRepository(private val database: AppDatabase) {
    private val employeeDao = database.employeeDao()

    suspend fun getAllEmployees(): List<Employee> {
        return employeeDao.getAll().first().map { it.toModel() }
    }

    suspend fun getEmployeeById(id: Int): Employee? {
        return employeeDao.getById(id)?.toModel()
    }

    suspend fun createEmployee(
        name: String,
        surname: String,
        roleId: Int,
        workId: Int?,
        dailyValue: String
    ): Employee {
        val entity = EmployeeEntity(
            name = name,
            surname = surname,
            roleId = roleId,
            workId = workId,
            dailyValue = dailyValue
        )
        val id = employeeDao.insert(entity)
        return Employee(id.toInt(), name, surname, roleId, workId, dailyValue)
    }

    suspend fun updateEmployee(
        id: Int,
        name: String,
        surname: String,
        roleId: Int,
        workId: Int?,
        dailyValue: String
    ): Employee {
        val entity = EmployeeEntity(
            id = id,
            name = name,
            surname = surname,
            roleId = roleId,
            workId = workId,
            dailyValue = dailyValue
        )
        employeeDao.update(entity)
        return Employee(id, name, surname, roleId, workId, dailyValue)
    }

    suspend fun deleteEmployee(id: Int) {
        employeeDao.deleteById(id)
    }

    suspend fun assignToWork(id: Int, workId: Int): Employee {
        val employee = getEmployeeById(id) ?: throw Exception("Employee not found")
        return updateEmployee(
            id = id,
            name = employee.name,
            surname = employee.surname,
            roleId = employee.roleId,
            workId = workId,
            dailyValue = employee.dailyValue
        )
    }

    private fun EmployeeEntity.toModel() = Employee(id, name, surname, roleId, workId, dailyValue)
}
