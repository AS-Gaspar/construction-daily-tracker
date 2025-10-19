package org.gaspar.construction_daily_tracker.repository

import org.gaspar.construction_daily_tracker.database.Works
import org.gaspar.construction_daily_tracker.model.Work
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class WorkRepository {

    fun create(name: String): Work = transaction {
        val id = Works.insert {
            it[Works.name] = name
        }[Works.id]

        Work(id, name)
    }

    fun findById(id: Int): Work? = transaction {
        Works.selectAll().where { Works.id eq id }
            .map { toWork(it) }
            .singleOrNull()
    }

    fun findAll(): List<Work> = transaction {
        Works.selectAll().map { toWork(it) }
    }

    fun update(id: Int, name: String): Boolean = transaction {
        Works.update({ Works.id eq id }) {
            it[Works.name] = name
        } > 0
    }

    fun delete(id: Int): Boolean = transaction {
        Works.deleteWhere { Works.id eq id } > 0
    }

    private fun toWork(row: ResultRow) = Work(
        id = row[Works.id],
        name = row[Works.name]
    )
}
