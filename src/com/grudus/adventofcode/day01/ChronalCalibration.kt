package com.grudus.adventofcode.day01

import com.grudus.adventofcode.day01.ChronalCalibration.firstStar
import com.grudus.adventofcode.day01.ChronalCalibration.secondStar
import com.grudus.adventofcode.readDayInput

object ChronalCalibration {

    fun firstStar(input: List<String>) =
        input.sumBy { it.toInt() }

    fun secondStar(input: List<String>): Int {
        val frequencies = mutableSetOf(0)
        val changes = input.map { it.toInt() }
        var currentFrequency = 0

        while (true) {
            for (frequencyChange in changes) {
                currentFrequency += frequencyChange

                val newItem = frequencies.add(currentFrequency)

                if (!newItem)
                    return currentFrequency
            }
        }
    }
}


fun main(args: Array<String>) {
    val file = readDayInput("01")

    println(firstStar(file))
    println(secondStar(file))

}
