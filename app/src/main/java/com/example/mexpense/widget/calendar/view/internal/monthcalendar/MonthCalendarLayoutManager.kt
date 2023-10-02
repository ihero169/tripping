package com.example.upbrain.shared.widget.calendar.view.internal.monthcalendar

import com.example.upbrain.shared.widget.calendar.core.CalendarDay
import com.example.upbrain.shared.widget.calendar.view.CalendarView
import com.example.upbrain.shared.widget.calendar.view.MarginValues
import com.example.upbrain.shared.widget.calendar.view.internal.CalendarLayoutManager
import com.example.upbrain.shared.widget.calendar.view.internal.dayTag
import java.time.YearMonth

internal class MonthCalendarLayoutManager(private val calView: CalendarView) :
    CalendarLayoutManager<YearMonth, CalendarDay>(calView, calView.orientation) {

    private val adapter: MonthCalendarAdapter
        get() = calView.adapter as MonthCalendarAdapter

    override fun getaItemAdapterPosition(data: YearMonth): Int = adapter.getAdapterPosition(data)
    override fun getaDayAdapterPosition(data: CalendarDay): Int = adapter.getAdapterPosition(data)
    override fun getDayTag(data: CalendarDay): Int = dayTag(data.date)
    override fun getItemMargins(): MarginValues = calView.monthMargins
    override fun scrollPaged(): Boolean = calView.scrollPaged
    override fun notifyScrollListenerIfNeeded() = adapter.notifyMonthScrollListenerIfNeeded()
}
