package org.gaspar.construction_daily_tracker.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.gaspar.construction_daily_tracker.model.User
import java.util.*

class JwtService {

    private val secret = "construction-daily-tracker-secret"
    private val issuer = "construction-daily-tracker"
    private val audience = "construction-daily-tracker-users"
    private val validityInMs = 36_000_00 * 10 // 10 hours

    fun generateToken(user: User): String {
        return JWT.create()
            .withSubject("Authentication")
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("username", user.username)
            .withExpiresAt(Date(System.currentTimeMillis() + validityInMs))
            .sign(Algorithm.HMAC256(secret))
    }
}
