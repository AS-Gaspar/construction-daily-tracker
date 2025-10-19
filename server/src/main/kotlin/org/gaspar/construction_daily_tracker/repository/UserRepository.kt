package org.gaspar.construction_daily_tracker.repository

import org.gaspar.construction_daily_tracker.database.Users
import org.gaspar.construction_daily_tracker.model.User
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

class UserRepository {

    fun create(username: String, password: String): User = transaction {
        val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
        val id = Users.insert {
            it[Users.username] = username
            it[Users.password] = hashedPassword
        }[Users.id]
        User(id, username)
    }

    fun findByUsername(username: String): Pair<User, String>? = transaction {
        Users.select { Users.username eq username }
            .map { toUser(it) to it[Users.password] }
            .singleOrNull()
    }

    private fun toUser(row: ResultRow) = User(
        id = row[Users.id],
        username = row[Users.username]
    )
}
