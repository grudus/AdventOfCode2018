package com.grudus.adventofcode.day11

import com.grudus.adventofcode.Matrix
import kotlin.streams.toList

object ChronalCharge {

    private const val FUEL_GRID_SIZE = 300

    data class Point(val x: Int, val y: Int) {
        override fun toString() = "$x,$y"
    }

    data class MaxRegion(val topLeft: Point, val sum: Int, val squareSize: Int) {
        override fun toString() = "$topLeft,$squareSize"
    }

    fun firstStar(gridSerialNumber: Int): Point {
        val fuelGrid = createFuelGrid(gridSerialNumber)

        return findMaxInSquare(fuelGrid, squareSize = 3).topLeft
    }

    fun secondStar(gridSerialNumber: Int): MaxRegion {
        val fuelGrid = createFuelGrid(gridSerialNumber)

        return (1 until FUEL_GRID_SIZE - 1)
            .toList()
            .parallelStream()
            .map { squareSize -> findMaxInSquare(fuelGrid, squareSize) }
            .toList()
            .maxBy { it.sum }!!
    }


    private fun findMaxInSquare(fuelGrid: Matrix<Int>, squareSize: Int): MaxRegion =
        (0 until FUEL_GRID_SIZE - squareSize - 1).map { x ->
            (0 until FUEL_GRID_SIZE - squareSize - 1).map { y ->

                val sum = (x until x + squareSize).map { xx ->
                    (y until y + squareSize).map { yy ->
                        fuelGrid[xx][yy]
                    }.sum()
                }.sum()

                MaxRegion(Point(x + 1, y + 1), sum, squareSize)
            }
        }.map { it.maxBy { point -> point.sum }!! }
            .maxBy { it.sum }!!


    private fun createFuelGrid(gridSerialNumber: Int): Matrix<Int> =
        (1..FUEL_GRID_SIZE).map { x ->
            (1..FUEL_GRID_SIZE).map { y ->
                val rackId = x + 10
                val rackIdPowerLvl = rackId * y + gridSerialNumber
                val powerLvl = rackIdPowerLvl * rackId
                findHundredDigit(powerLvl) - 5
            }
        }

    private fun findHundredDigit(number: Int): Int = number.toString()
        .let { if (it.length < 3) 0 else Character.getNumericValue(it[it.length - 3]) }

}

fun main(args: Array<String>) {
    val gridSerialNumber = 9306

    println(ChronalCharge.firstStar(gridSerialNumber))
    println(ChronalCharge.secondStar(gridSerialNumber))
}
