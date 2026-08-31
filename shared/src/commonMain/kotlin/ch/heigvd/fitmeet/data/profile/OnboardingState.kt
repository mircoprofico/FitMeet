package ch.heigvd.fitmeet.data.profile

data class OnboardingState(
    val name: String = "",
    val birthdate: String = "",
    val selectedSports: Set<String> = emptySet(),
    val complete: Boolean = false,
)

fun birthdateToIso(value: String): String? {
    val digits = value.filter(Char::isDigit)
    if (digits.length != 8) return null

    val day = digits.substring(0, 2).toIntOrNull() ?: return null
    val month = digits.substring(2, 4).toIntOrNull() ?: return null
    val year = digits.substring(4, 8).toIntOrNull() ?: return null
    if (year !in 1900..2100 || month !in 1..12) return null

    val daysInMonth = when (month) {
        2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
        4, 6, 9, 11 -> 30
        else -> 31
    }
    if (day !in 1..daysInMonth) return null

    return buildString {
        append(year)
        append('-')
        if (month < 10) append('0')
        append(month)
        append('-')
        if (day < 10) append('0')
        append(day)
    }
}

fun isoBirthdateToDisplay(value: String?): String {
    if (value == null) return ""
    val parts = value.split('-')
    if (parts.size != 3) return value
    return "${parts[2]}/${parts[1]}/${parts[0]}"
}
