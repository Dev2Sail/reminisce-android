package studio.hcmc.reminisce.util

fun String.isEmoji(): Boolean {
    return when (length) {
        1 -> when (this[0]) {
            '\u00a9',
            '\u00ae',
            in '\u2000'..'\u3300' -> true
            else -> false
        }
        2 -> when (this[0]) {
            '\ud83c',
            '\ud83d',
            '\ud83e' -> this[1] in '\ud000'..'\udfff'
            else -> false
        }
        else -> false
    }
}