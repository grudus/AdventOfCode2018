package com.grudus.adventofcode.day02

import com.grudus.adventofcode.day02.Day02.firstStar
import com.grudus.adventofcode.day02.Day02.secondStar
import com.grudus.adventofcode.readDayInput


object Day02 {

    fun firstStar(input: List<String>) =
        input
            .map { boxId -> boxId.groupBy { char -> char }.map { Pair(it.key, it.value.size) } }
            .fold(Pair(0, 0)) {(twoLetters, threeLetters), occurrences ->
                val twoLettersPlus = if (occurrences.any { it.second == 2 }) 1 else 0
                val threeLettersPlus = if (occurrences.any { it.second == 3 }) 1 else 0

                Pair(twoLetters + twoLettersPlus, threeLetters + threeLettersPlus)
            } .let { it.first * it.second }


    fun secondStar(input: List<String>): String {
        val idLength = input[0].length

        return  input.flatMap{ boxId -> input.map { Pair(boxId, it) } }
            .map { (first, second) -> first.commonPrefixWith(second) + first.commonSuffixWith(second) }
            .find { it.length == idLength - 1 }!!
    }

}

fun main(args: Array<String>) {
    val file = readDayInput("02")

    println(firstStar(file))
    println(secondStar(file))
}
