package ch.heigvd.fitmeet.data.activityCreation

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime

fun formatDate(millis: Long): String {
    val instant = Instant.fromEpochMilliseconds(millis)

    val date = instant
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date

    return "${date.day.toString().padStart(2, '0')}/" +
            "${date.month.number.toString().padStart(2, '0')}/" +
            date.year
}