package com.grudus.adventofcode.day05

import com.grudus.adventofcode.day05.AlchemicalReduction.firstStar
import com.grudus.adventofcode.day05.AlchemicalReduction.secondStar
import com.grudus.adventofcode.readDayInput
import kotlin.streams.toList

object AlchemicalReduction {
    private const val DIFF_BETWEEN_CAPITAL_LETTER = 32

    fun firstStar(initialPolymer: String) = startReactions(initialPolymer).length

    fun secondStar(initialPolymer: String) = ('a'..'z')
        .map { char -> initialPolymer.replace(Regex("[$char${char.toUpperCase()}]"), "") }
        .parallelStream()
        .map { polymer -> startReactions(polymer) }
        .map { it.length }
        .toList().min()

    private tailrec fun startReactions(polymer: String): String {
        val explosionPosition = findExplosionIndex(polymer)

        return if (explosionPosition == null)
            polymer
        else startReactions(polymer.removeRange(explosionPosition, explosionPosition + 2))
    }

    private fun findExplosionIndex(polymer: String): Int? =
        (0 until polymer.length - 1)
            .firstOrNull { Math.abs(polymer[it].toInt() - polymer[it + 1].toInt()) == DIFF_BETWEEN_CAPITAL_LETTER }
}

fun main(args: Array<String>) {
    val polymer = readDayInput("05")[0]

    println(firstStar(polymer))
    println(secondStar(polymer))

}
