package org.gaspar.construction_daily_tracker.i18n

/**
 * Supported languages in the app.
 */
enum class Language(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    PORTUGUESE("pt", "Português");

    companion object {
        fun fromCode(code: String): Language {
            return entries.find { it.code == code } ?: ENGLISH
        }
    }
}
