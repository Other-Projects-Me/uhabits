package org.isoron.uhabits.core.models

import org.isoron.uhabits.core.utils.DateUtils
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.*

class WeeklyStatsCalculator(private val habitList: List<Habit>) {

    fun calculateDailyCompletionPercentage(date: LocalDate): Double {
        val completed = habitList.count { it.isCompleted(date) }
        return if (habitList.isEmpty()) 0.0 else (completed.toDouble() / habitList.size) * 100
    }

    fun calculateWeeklyAverage(weekNumber: Int, year: Int): Double {
        val weekFields = WeekFields.of(Locale.getDefault())
        val startDate = LocalDate.now()
            .withYear(year)
            .with(weekFields.weekOfYear(), weekNumber.toLong())
            .with(weekFields.dayOfWeek(), 1) // Sunday
        
        val dailyPercentages = (0 until 7).map { day ->
            calculateDailyCompletionPercentage(startDate.plusDays(day.toLong()))
        }
        
        return dailyPercentages.average()
    }

    fun getWeeklyAveragesForGraph(weeks: Int): List<Pair<Int, Double>> {
        val currentWeek = LocalDate.now().get(WeekFields.of(Locale.getDefault()).weekOfYear())
        val currentYear = LocalDate.now().year
        
        return (0 until weeks).map { weekOffset ->
            val weekNumber = currentWeek - weekOffset
            val year = if (weekNumber < 1) currentYear - 1 else currentYear
            val adjustedWeek = if (weekNumber < 1) 52 + weekNumber else weekNumber
            Pair(adjustedWeek, calculateWeeklyAverage(adjustedWeek, year))
        }.reversed()
    }
}
