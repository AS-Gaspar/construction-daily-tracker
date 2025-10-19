package org.gaspar.construction_daily_tracker.repository

import org.gaspar.construction_daily_tracker.database.Roles
import org.gaspar.construction_daily_tracker.model.Role
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class RoleRepository {

    fun create(title: String): Role = transaction {
        val id = Roles.insert {
            it[Roles.title] = title
        }[Roles.id]

        Role(id, title)
    }

    fun findById(id: Int): Role? = transaction {
        Roles.selectAll().where { Roles.id eq id }
            .map { toRole(it) }
            .singleOrNull()
    }

    fun findAll(): List<Role> = transaction {
        Roles.selectAll().map { toRole(it) }
    }

    fun update(id: Int, title: String): Boolean = transaction {
        Roles.update({ Roles.id eq id }) {
            it[Roles.title] = title
        } > 0
    }

    fun delete(id: Int): Boolean = transaction {
        Roles.deleteWhere { Roles.id eq id } > 0
    }

    private fun toRole(row: ResultRow) = Role(
        id = row[Roles.id],
        title = row[Roles.title]
    )
}
