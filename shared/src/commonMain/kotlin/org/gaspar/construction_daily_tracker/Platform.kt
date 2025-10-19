package org.gaspar.construction_daily_tracker

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
