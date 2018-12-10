package com.grudus.adventofcode.day09

import com.grudus.adventofcode.findGroups
import com.grudus.adventofcode.readDayInput

object MarbleMania {

    class Node<T>(val value: T) {
        var next: Node<T>? = null
        var previous: Node<T>? = null

        fun addNext(value: T): Node<T> {
            val newNode = Node(value)
            if (this.next == null) {
                this.next = newNode
                newNode.previous = this
            } else {
                val previousNext = this.next

                this.next = newNode
                newNode.previous = this

                newNode.next = previousNext
                previousNext!!.previous = newNode
            }
            return this.next!!
        }

        fun removeItself(): Node<T> {
            val prev = this.previous
            val next = this.next
            next!!.previous = prev
            prev!!.next = next
            return next
        }

        companion object {
            fun <T> createCircular(value1: T, value2: T): Node<T> {
                val node: Node<T> = Node(value1).addNext(value2)
                node.next = node.previous
                node.previous!!.previous = node
                return node
            }
        }
    }

    fun allStars(input: MetaInfo): Long {
        val points: MutableMap<Int, Long> = (0 until input.numberOfPlayers)
            .groupBy({ it }, { 0 })
            .mapValues { it.value[0].toLong() } as MutableMap

        var currentMarble = Node.createCircular(0L, 1L)
        var marbleNumber = 2L
        var currentPlayer = 2

        while (marbleNumber <= input.lastMarblePoint) {
            if (marbleNumber % 23 == 0L) {
                val marbleToRemove = (0 until 7).fold(currentMarble) { a, _ -> a.previous!! }
                val marbleToRemovePoints = marbleToRemove.value

                val newPoints =
                    points[currentPlayer]!! + marbleNumber + marbleToRemovePoints

                points[currentPlayer] = newPoints
                currentMarble = marbleToRemove.removeItself()
            }

            else {
                val nextMarbleAfter = currentMarble.next!!
                currentMarble = nextMarbleAfter.addNext(marbleNumber)
            }

            ++marbleNumber
            currentPlayer = (currentPlayer + 1) % input.numberOfPlayers
        }
        return points.maxBy { it.value }!!.value
    }
}

data class MetaInfo(val numberOfPlayers: Int, val lastMarblePoint: Int)

private val pattern = Regex("(\\d+) players; last marble is worth (\\d+) points").toPattern()


fun main(args: Array<String>) {
    val info: MetaInfo = readDayInput("09")
        .map { line -> pattern.matcher(line).findGroups() }
        .map { groups -> MetaInfo(groups[0].toInt(), groups[1].toInt()) }
        .first()

    println(MarbleMania.allStars(info))
    println(MarbleMania.allStars(info.copy(lastMarblePoint = info.lastMarblePoint * 100)))
}
