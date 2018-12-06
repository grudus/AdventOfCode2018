package com.grudus.adventofcode.day06

import com.grudus.adventofcode.Matrix
import com.grudus.adventofcode.day06.ChronalCoordinates.secondStar
import com.grudus.adventofcode.findGroups
import com.grudus.adventofcode.readDayInput
import kotlin.Int.Companion.MAX_VALUE

data class Coordinate(val x: Int, val y: Int, val name: String? = null)

data class Distance(val coordinate: Coordinate, val distance: Int) {
    constructor() : this(Coordinate(0, 0), MAX_VALUE)

    override fun toString() = "${coordinate.name ?: '.'}"
}


object ChronalCoordinates {
    private const val WORLD_SIZE = 500
    private val coordinatePattern = Regex("(\\d+), (\\d+)").toPattern()

    fun firstStar(input: List<String>): Int {
        val coordinates = parseInput(input)

        val worlds: List<Matrix<Distance>> = findDistancesForAllCoordinates(coordinates)

        val minimumDistancesToCoordinates: Matrix<Distance> = mapCoordinates { x, y ->

            worlds.fold(Distance()) { minDist: Distance, world: List<List<Distance>> ->
                val currentDistance = world[x][y]
                return@fold when {
                    currentDistance.distance < minDist.distance -> currentDistance
                    currentDistance.distance > minDist.distance -> minDist
                    else -> currentDistance.copy(coordinate = currentDistance.coordinate.copy(name = null))
                }
            }
        }

        val distancesWithoutInfiniteLocations: Matrix<Distance> = removeInfiniteLocations(minimumDistancesToCoordinates)


        return distancesWithoutInfiniteLocations.flatten()
            .filter { it.coordinate.name != null }
            .groupBy { it.coordinate.name }
            .values
            .map { it.size }
            .max()!!
    }


    fun secondStar(input: List<String>): Int {
        val coordinates = parseInput(input)
        val regionSize = 10000

        val worlds: List<Matrix<Distance>> = findDistancesForAllCoordinates(coordinates)

        val summedDistancesForAllCoordinates: Matrix<Int> = mapCoordinates { x, y ->
            worlds.map { world -> world[x][y].distance }.sum()
        }

        return summedDistancesForAllCoordinates.flatten()
            .count { it < regionSize }
    }


    private fun <T> mapCoordinates(mapper: (Int, Int) -> T): Matrix<T> =
        (0 until WORLD_SIZE).map { x ->
            (0 until WORLD_SIZE).map { y ->
                mapper(x, y)
            }
        }

    private fun findDistancesForAllCoordinates(coordinates: List<Coordinate>): List<Matrix<Distance>> =
        coordinates.map { coordinate ->
            mapCoordinates { x, y ->
                Distance(
                    coordinate.copy(x = x, y = y),
                    findMinimalDistance(coordinate, Coordinate(x, y))
                )
            }
        }

    private fun removeInfiniteLocations(minimumDistancesToCoordinates: Matrix<Distance>): Matrix<Distance> {
        val namesToRemove = findInfiniteLocationNames(minimumDistancesToCoordinates)

        return minimumDistancesToCoordinates.map { rowDistances ->
            rowDistances.map { distance ->
                if (namesToRemove.contains(distance.coordinate.name))
                    distance.copy(coordinate = distance.coordinate.copy(name = null))
                else distance
            }
        }
    }

    private fun findInfiniteLocationNames(minimumDistancesToCoordinates: Matrix<Distance>): Set<String> {
        val namesToRemove = mutableSetOf<String>()

        minimumDistancesToCoordinates.forEach { distances: List<Distance> ->
            distances.forEach { distance ->
                if (distance.coordinate.name != null && isOnBorder(distance))
                    namesToRemove.add(distance.coordinate.name)
            }
        }
        return namesToRemove
    }

    private fun isOnBorder(distance: Distance) =
        distance.coordinate.x == 0 || distance.coordinate.x == WORLD_SIZE - 1 || distance.coordinate.y == 0 || distance.coordinate.y == WORLD_SIZE - 1



    private fun findMinimalDistance(a: Coordinate, b: Coordinate) =
        Math.abs(a.x - b.x) + Math.abs(a.y - b.y)


    private fun parseInput(input: List<String>): List<Coordinate> =
        input.map { line -> coordinatePattern.matcher(line) }
            .map { matcher -> matcher.findGroups() }
            .mapIndexed { index, groups ->
                Coordinate(groups[0].toInt(), groups[1].toInt(), ('A' + index).toString())
            }

}

fun main(args: Array<String>) {
    val input = readDayInput("06")

    println(ChronalCoordinates.firstStar(input))
    println(secondStar(input))
}
