package com.gorman.chatroom.ui.components

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import java.util.Locale

fun formatTimestamp(isoString: String?): String {
    val instant = Instant.parse(isoString)
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
        .withZone(ZoneId.systemDefault())
    return formatter.format(instant)
}

fun formatDate(date: LocalDate): String {
    val today = LocalDate.now(ZoneId.systemDefault())

    return when (ChronoUnit.DAYS.between(date, today)) {
        1L -> "вчера"
        0L -> "сегодня"
        else -> DateTimeFormatter.ofPattern("dd.MM.yyyy").format(date)
    }
}

fun formatMessageTimestamp(isoOrEpoch: String?): String {
    if (isoOrEpoch.isNullOrBlank()) return ""

    val locale = Locale.getDefault()
    val zone = ZoneId.systemDefault()

    val instant = runCatching {
        Instant.parse(isoOrEpoch)
    }.getOrElse {
        runCatching { Instant.ofEpochMilli(isoOrEpoch.toLong()) }.getOrElse { return "" }
    }

    val dt = instant.atZone(zone)
    val today = LocalDate.now(zone)
    val date = dt.toLocalDate()

    return when (ChronoUnit.DAYS.between(date, today)) {
        0L -> DateTimeFormatter.ofPattern("HH:mm", locale).format(dt)                 // сегодня → время
        in 1..6 -> {
            val w = DateTimeFormatter.ofPattern("eee", locale).format(dt)
            w.replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
        }
        else -> DateTimeFormatter.ofPattern("dd.MM.yyyy", locale).format(dt)          // иначе → дата
    }
}

fun parseIso(iso: String?): Long =
    try {
        Instant.parse(iso).toEpochMilli()
    } catch (_: DateTimeParseException) {
        Long.MAX_VALUE
    }