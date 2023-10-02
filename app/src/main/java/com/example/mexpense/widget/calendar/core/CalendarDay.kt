package com.example.upbrain.shared.widget.calendar.core

import java.io.Serializable
import java.time.LocalDate
import javax.annotation.concurrent.Immutable

/**
 * Represents a day on the calendar.
 *
 * @param date the date for this day.
 * @param position the [DayPosition] for this day.
 */
@Immutable
data class CalendarDay(val date: LocalDate, val position: DayPosition) : Serializable
