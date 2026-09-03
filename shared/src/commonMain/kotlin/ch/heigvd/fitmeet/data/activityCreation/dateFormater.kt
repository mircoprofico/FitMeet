package ch.heigvd.fitmeet.data.activityCreation

import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.LocalDate


fun formatDate(millis: Long): String {
    val instant = kotlin.time.Instant.fromEpochMilliseconds(millis)

    val date = instant
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date

    return "${date.day.toString().padStart(2, '0')}/" +
            "${date.month.number.toString().padStart(2, '0')}/" +
            date.year
}


fun parseDate(date: String): LocalDate {
    val parts = date.split("/")

    return LocalDate(
        year = parts[2].toInt(),
        month = parts[1].toInt(),
        day = parts[0].toInt()
    )
}