package com.grudus.adventofcode.day12

import com.grudus.adventofcode.findGroups
import com.grudus.adventofcode.readDayInput


object SubterraneanSustainability {

    fun string(value: Boolean) = if (value) "#" else "."

    data class Transform(
        val value: Boolean,
        val left: List<Boolean>,
        val right: List<Boolean>,
        val nextState: Boolean
    ) {
        override fun toString() =
            left.joinToString("") { string(it) } + string(value) + right.joinToString("") { string(it) } + " -> " + string(
                nextState
            )
    }

    data class Plant(val value: Boolean, val number: Int) {
        override fun toString() = string(value)
    }

    fun firstStar(initialState: List<Boolean>, transforms: List<Transform>): Int {
        val offset = 100
        var state = (List(offset) { false } + initialState + List(offset) { false })
            .mapIndexed { index, value -> Plant(value, index - offset) }

        for (i in 0 until 20) {
            state = state.mapIndexed { index, plant ->
                if (index < 3 || index > state.size - 3)
                    return@mapIndexed plant

                val left = (1..2).map { state[index - 3 + it].value }
                val right = (1..2).map { state[index + it].value }

                transforms.find { t -> t.left == left && t.right == right && t.value == plant.value }
                    ?.let { Plant(it.nextState, plant.number) } ?: Plant(false, plant.number)
            }

        }

        return state.filter { it.value }
            .sumBy { it.number }

    }


    fun secondStar(initialState: List<Boolean>, transforms: List<Transform>): Long {
        val offset = 10_000
        var state = (List(offset) { false } + initialState + List(offset) { false })
            .mapIndexed { index, value -> Plant(value, index - offset) }


        val numberOfGenerations = 50000000000L

        var previousPreviousDelta = -1L
        var previousDelta = 0L
        var delta = 1L
        var previousSum = 0L
        var sum = 0L

        for (i in 0 until numberOfGenerations) {

            // hack af
            if (previousPreviousDelta == delta && delta == previousDelta) {
                return sum + (delta * (numberOfGenerations - i))
            }

            state = state.mapIndexed { index, plant ->
                if (index < 3 || index > state.size - 3)
                    return@mapIndexed plant

                val left = (1..2).map { state[index - 3 + it].value }
                val right = (1..2).map { state[index + it].value }

                transforms.find { t -> t.left == left && t.right == right && t.value == plant.value }
                    ?.let { Plant(it.nextState, plant.number) } ?: Plant(false, plant.number)
            }

            previousSum = sum
            previousPreviousDelta = previousDelta
            previousDelta = delta
            sum = state.filter { it.value }
                .sumBy { it.number }.toLong()
            delta = sum - previousSum
        }

        return sum
    }
}


private val pattern = Regex("(.*) => (.)").toPattern()

fun main(args: Array<String>) {
    val input = readDayInput("12")

    val initialState: List<Boolean> = input[0].split(": ")[1]
        .map { it == '#' }

    val transforms = input.drop(2)
        .map { line -> pattern.matcher(line).findGroups() }
        .map { groups ->
            val state = groups[0].map { it == '#' }
            val nextVal = groups[1] == "#"
            SubterraneanSustainability.Transform(state[2], state.subList(0, 2), state.subList(3, 5), nextVal)
        }

    println(SubterraneanSustainability.firstStar(initialState, transforms))
    println(SubterraneanSustainability.secondStar(initialState, transforms))
}
