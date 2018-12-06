package com.grudus.adventofcode.day04

import com.grudus.adventofcode.findGroups
import com.grudus.adventofcode.readDayInput
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Day04 {

    private const val DATE_FORMAT = "yyyy-MM-dd HH:mm"
    private val INPUT_PATTERN = Regex("\\[(.*)] (.*)").toPattern()
    private val GUARD_PATTERN = Regex("Guard #(\\d+) begins shift").toPattern()

    enum class GuardState(val input: String) {
        BEGINS_SHIFT("begins shift"),
        FALLS_ASLEEP("falls asleep"),
        WAKES_UP("wakes up");

        companion object {
            fun find(string: String) = values()
                .find { string.contains(it.input) }!!
        }
    }

    data class InputRow(val date: LocalDateTime, val guardId: Int, val guardState: GuardState)
    data class MostSleepedMinute(val minute: Int, val numberOfSleeps: Int)


    fun firstStar(input: List<String>): Int {
        val inputRows = parseInput(input)
            .groupBy { it.guardId }

        val sleeper = inputRows
            .map { Pair(it.key, summarySleepTime(it.value)) }
            .sortedByDescending { it.second }[0]

        val mostCommonMinute = findMostCommonSleepMinute(inputRows[sleeper.first]!!)

        return sleeper.first * mostCommonMinute.minute
    }



    fun secondStar(input: List<String>): Int {
        return parseInput(input)
            .groupBy { it.guardId }
            .map { Pair(it.key, findMostCommonSleepMinute(it.value)) }
            .sortedByDescending { it.second.numberOfSleeps }[0]
            .let { it.first * it.second.minute }
    }

    private fun findMostCommonSleepMinute(guardRow: List<InputRow>): MostSleepedMinute {
        val minutes = Array(60) { 0 }

        tailrec fun calculate(remainingRows: List<InputRow>, lastSleepTime: LocalDateTime?): Array<Int> {
            if (remainingRows.isEmpty())
                return minutes

            val currentRow = remainingRows[0]

            return when (currentRow.guardState) {
                GuardState.BEGINS_SHIFT -> calculate(remainingRows.drop(1), null)
                GuardState.FALLS_ASLEEP -> calculate(remainingRows.drop(1), currentRow.date)
                GuardState.WAKES_UP -> {
                    (lastSleepTime!!.minute until currentRow.date.minute).forEach { i -> minutes[i]++ }
                    calculate(
                        remainingRows.drop(1),
                        null
                    )
                }
            }
        }

        calculate(guardRow, null)

        return minutes.foldIndexed(MostSleepedMinute(0, 0)) { index, mostSleeped, value ->
            if (value > mostSleeped.numberOfSleeps)
                MostSleepedMinute(index, value)
            else mostSleeped
        }
    }

    private fun summarySleepTime(guardRow: List<InputRow>): Int {

        tailrec fun calculate(remainingRows: List<InputRow>, sum: Int, lastSleepTime: LocalDateTime?): Int {
            if (remainingRows.isEmpty())
                return sum

            val currentRow = remainingRows[0]

            return when (currentRow.guardState) {
                GuardState.BEGINS_SHIFT -> calculate(remainingRows.drop(1), sum, null)
                GuardState.FALLS_ASLEEP -> calculate(remainingRows.drop(1), sum, currentRow.date)
                GuardState.WAKES_UP -> calculate(
                    remainingRows.drop(1),
                    sum + (currentRow.date.minute - lastSleepTime!!.minute),
                    null
                )
            }
        }

        return calculate(guardRow, 0, null)
    }


    private fun parseInput(input: List<String>): List<InputRow> {
        var currentGuardId: Int? = null

        return input.sorted()
            .map { line -> INPUT_PATTERN.matcher(line) }
            .map { matcher -> matcher.findGroups() }
            .map { groups ->
                val date = LocalDateTime.parse(groups[0], DateTimeFormatter.ofPattern(DATE_FORMAT))
                val stateString = groups[1]
                val guardState = GuardState.find(stateString)
                currentGuardId =
                        if (guardState == GuardState.BEGINS_SHIFT)
                            GUARD_PATTERN.matcher(stateString).findGroups()[0].toInt()
                        else currentGuardId

                InputRow(date, currentGuardId!!, GuardState.find(stateString))
            }
    }

}

fun main(args: Array<String>) {
    val file = readDayInput("04")

    println(Day04.firstStar(file))
    println(Day04.secondStar(file))
}
