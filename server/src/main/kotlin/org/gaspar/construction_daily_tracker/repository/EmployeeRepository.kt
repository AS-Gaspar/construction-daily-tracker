package org.gaspar.construction_daily_tracker.repository

import org.gaspar.construction_daily_tracker.database.Employees
import org.gaspar.construction_daily_tracker.model.Employee
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal

class EmployeeRepository {

    fun create(name: String, surname: String, roleId: Int, workId: Int, dailyValue: BigDecimal): Employee = transaction {
        val id = Employees.insert {
            it[Employees.name] = name
            it[Employees.surname] = surname
            it[Employees.roleId] = roleId
            it[Employees.workId] = workId
            it[Employees.dailyValue] = dailyValue
        }[Employees.id]

        Employee(id, name, surname, roleId, workId, dailyValue.toString())
    }

    fun findById(id: Int): Employee? = transaction {
        Employees.selectAll().where { Employees.id eq id }
            .map { toEmployee(it) }
            .singleOrNull()
    }

    fun findAll(): List<Employee> = transaction {
        Employees.selectAll().map { toEmployee(it) }
    }

    fun findByWorkId(workId: Int): List<Employee> = transaction {
        Employees.selectAll().where { Employees.workId eq workId }
            .map { toEmployee(it) }
    }

    fun findByRoleId(roleId: Int): List<Employee> = transaction {
        Employees.selectAll().where { Employees.roleId eq roleId }
            .map { toEmployee(it) }
    }

    fun update(
        id: Int,
        name: String? = null,
        surname: String? = null,
        roleId: Int? = null,
        workId: Int? = null,
        dailyValue: BigDecimal? = null
    ): Employee? = transaction {
        val updated = Employees.update({ Employees.id eq id }) {
            name?.let { v -> it[Employees.name] = v }
            surname?.let { v -> it[Employees.surname] = v }
            roleId?.let { v -> it[Employees.roleId] = v }
            workId?.let { v -> it[Employees.workId] = v }
            dailyValue?.let { v -> it[Employees.dailyValue] = v }
        }

        if (updated > 0) {
            findById(id)
        } else {
            null
        }
    }

    fun delete(id: Int): Boolean = transaction {
        Employees.deleteWhere { Employees.id eq id } > 0
    }

    private fun toEmployee(row: ResultRow) = Employee(
        id = row[Employees.id],
        name = row[Employees.name],
        surname = row[Employees.surname],
        roleId = row[Employees.roleId],
        workId = row[Employees.workId],
        dailyValue = row[Employees.dailyValue].toString()
    )
}
