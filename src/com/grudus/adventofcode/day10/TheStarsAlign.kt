package com.grudus.adventofcode.day10

import com.grudus.adventofcode.findGroups
import com.grudus.adventofcode.readDayInput

object TheStarsAlign {
    private val pattern = Regex("position=<\\s?(-?\\d+),\\s+(-?\\d+)> velocity=<\\s?(-?\\d+),\\s+(-?\\d+)>").toPattern()

    data class Position(val x: Int, val y: Int) {
        operator fun plus(velocity: Velocity) =
                Position(x + velocity.dx, y + velocity.dy)

    }
    data class Velocity(val dx: Int, val dy: Int)
    data class Point(val position: Position, val velocity: Velocity)


    fun firstStar(input: List<String>): Int {
        var positions = parseInput(input)

        val wordHeight = 10
        var second = 0

        while (true) {
            val maxY = positions.maxBy { it.position.y }!!.position.y
            val minY = positions.minBy { it.position.y }!!.position.y
            val yDelta = Math.abs(maxY - minY)

            if (yDelta < wordHeight) {
                val maxX = positions.maxBy { it.position.x }!!.position.x
                val minX = positions.minBy { it.position.x }!!.position.x
                val xDelta = Math.abs(maxX - minX)

                val sky = Array(yDelta + 1) {Array(xDelta + 1) {false}}

                positions.forEach { sky[it.position.y - minY][it.position.x - minX] = true }

                sky.forEach {
                    row -> row.forEach { print(if (it) "x" else ".") }
                    println()
                }

                return second
            }

            positions = positions.map { it.copy(position = it.position + it.velocity) }
            second++
        }
    }

    private fun parseInput(input: List<String>): List<Point> =
        input.map { line ->
            pattern.matcher(line).findGroups() }
            .map { groups -> groups.map { it.toInt() } }
            .map { groups -> Point(Position(groups[0], groups[1]), Velocity(groups[2], groups[3])) }


}


fun main(args: Array<String>) {
    val input = readDayInput("10")


    println(TheStarsAlign.firstStar(input))
}
