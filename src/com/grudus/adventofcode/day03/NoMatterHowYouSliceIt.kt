package com.grudus.adventofcode.day03

import com.grudus.adventofcode.day03.Day03.firstStar
import com.grudus.adventofcode.day03.Day03.secondStar
import com.grudus.adventofcode.findGroups
import com.grudus.adventofcode.readDayInput
import java.util.regex.Pattern

data class Claim(val id: Int, val left: Int, val top: Int, val width: Int, val height: Int)

object Day03 {

    private val pattern: Pattern = Regex("#(\\d+) @ (\\d+),(\\d+): (\\d+)x(\\d+)").toPattern()

    fun firstStar(input: List<String>): Int {
        val area = Array(5000) {Array(5000) {0}}
        val claims = parseClaims(input)

        claims.forEach { (_, left, top, width, height) ->

            for (w in 0 until width)
                for (h in 0 until height)
                    area[top + h][left + w] += 1
        }

        return area.fold(0) {sum, array ->
            sum + array.filter { it >= 2 }.size
        }
    }

    fun secondStar(input: List<String>): Int {
        val area = Array(5000) {Array<Int?>(5000) {null}}
        val claims = parseClaims(input)
        val overlappingIds = mutableSetOf<Int>()

        claims.forEach { (id, left, top, width, height) ->

            for (w in 0 until width)
                for (h in 0 until height) {
                    val existingId = area[top + h][left + w]

                    if (existingId != null) {
                        overlappingIds.add(id)
                        overlappingIds.add(existingId)
                    }
                    area[top + h][left + w] = id
                }
        }

        return claims.find { !overlappingIds.contains(it.id) }!!.id
    }


    private fun parseClaims(input: List<String>): List<Claim> {
        return input.map { line -> pattern.matcher(line) }
            .map { matcher -> matcher.findGroups() }
            .map { group -> group.map { it.toInt() } }
            .map { Claim(it[0], it[1], it[2], it[3], it[4]) }
    }


}

fun main(args: Array<String>) {
    val file = readDayInput("03")

    println(firstStar(file))
    println(secondStar(file))
}
