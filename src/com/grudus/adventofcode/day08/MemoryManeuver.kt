package com.grudus.adventofcode.day08

import com.grudus.adventofcode.readDayInput
import java.util.*

object MemoryManeuver {

    data class Node(var childCount: Int, var metadataCount: Int? = null)

    fun firstStar(input: List<Int>): Int {
        val stack = ArrayDeque<Node>().apply { push(Node(input[0], input[1])) }

        return input.drop(2).fold(0) { metaSum, num ->
            val current = stack.pop()

            if (current.metadataCount == null) {
                current.metadataCount = num
                stack.push(current)
                return@fold metaSum
            }

            if (current.childCount > 0) {
                current.childCount--
                stack.push(current)
                stack.push(Node(num))
                return@fold metaSum
            }

            if (current.childCount == 0) {
                current.metadataCount = current.metadataCount!! - 1
                if (current.metadataCount!! > 0)
                    stack.push(current)

                return@fold metaSum + num
            }

            metaSum
        }
    }

}


fun main(args: Array<String>) {
    val input: List<Int> = readDayInput("08")[0]
        .split(Regex("\\s"))
        .map { it.toInt() }

    println(MemoryManeuver.firstStar(input))
}
