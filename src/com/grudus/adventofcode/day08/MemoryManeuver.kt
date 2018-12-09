package com.grudus.adventofcode.day08

import com.grudus.adventofcode.readDayInput
import java.util.*

object MemoryManeuver {

    data class Node(var childCount: Int, var metadataCount: Int? = null) {
        val children = mutableListOf<Node>()
        val metadata = mutableListOf<Int>()

        override fun toString() = metadata.toString()

        fun sumMeta(): Int =
            metadata.sum() + children.map { it.sumMeta() }.sum()

        fun sumMeta2(): Int {
            if (children.isEmpty())
                return metadata.sum()
            return metadata.map { it - 1 }.map { metaIndex ->
                if (metaIndex >= children.size)
                    0
                else
                    children[metaIndex].sumMeta2()
            }.sum()
        }
    }

    fun firstStar(input: List<Int>): Int =
        createTree(input)
            .sumMeta()


    fun secondStar(input: List<Int>): Int =
        createTree(input).sumMeta2()

    
    private fun createTree(input: List<Int>): Node {
        val root = Node(input[0], input[1])
        val stack = ArrayDeque<Node>().apply { push(root) }

        input.drop(2).forEach { num ->
            val current = stack.pop()

            if (current.metadataCount == null) {
                current.metadataCount = num
                stack.push(current)
            } else if (current.childCount > 0) {
                current.childCount--
                stack.push(current)
                val node = Node(num)
                stack.push(node)
                current.children += node
            } else if (current.childCount == 0) {
                current.metadataCount = current.metadataCount!! - 1
                current.metadata += num
                if (current.metadataCount!! > 0)
                    stack.push(current)
            }
        }
        return root
    }
}


fun main(args: Array<String>) {
    val input: List<Int> = readDayInput("08")[0]
        .split(Regex("\\s"))
        .map { it.toInt() }

    println(MemoryManeuver.firstStar(input))
    println(MemoryManeuver.secondStar(input))
}
