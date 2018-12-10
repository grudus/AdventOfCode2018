package com.grudus.adventofcode.day09

import com.grudus.adventofcode.findGroups
import com.grudus.adventofcode.readDayInput

object MarbleMania {

    data class Snapshot(val currentMarbleIndex: Int, val points: Map<Int, Int>, val marbles: List<Int>) {

        fun insertMarbleAfter(index1: Int, marble: Int) =
            marbles.subList(0, index1) + marble + marbles.subList(index1, marbles.size)

    }

    data class Move(val currentPlayer: Int, val marbleNumber: Int)

    fun firstStar(input: MetaInfo): Int {
        val points = (0 until input.numberOfPlayers).groupBy({ it }, { 0 }).mapValues { it.value[0] }

        return generateSequence(Move(3, 3)) {
            Move((it.currentPlayer + 1) % input.numberOfPlayers, it.marbleNumber + 1)
        }
            .takeWhile { it.marbleNumber <= input.lastMarblePoint }
            .fold(Snapshot(1, points, listOf(0, 2, 1))) { snapshot, (currentPlayer, marbleNumber) ->

                if (marbleNumber % 23 == 0) {
                    val marbleToRemoveIndex = (snapshot.currentMarbleIndex - 7)
                        .let { if (it < 0) snapshot.marbles.size + it else it }
                    val nextMarbles = snapshot.marbles - snapshot.marbles[marbleToRemoveIndex]


                    val newPoints =
                        snapshot.points[currentPlayer]!! + marbleNumber + snapshot.marbles[marbleToRemoveIndex]

                    return@fold snapshot.copy(
                        currentMarbleIndex = marbleToRemoveIndex,
                        marbles = nextMarbles,
                        points = snapshot.points + (currentPlayer to newPoints)
                    )
                }

                val nextMarbleIndex = (snapshot.currentMarbleIndex + 2) % snapshot.marbles.size

                if (nextMarbleIndex == 0)
                    return@fold snapshot.copy(
                        currentMarbleIndex = snapshot.marbles.size,
                        marbles = snapshot.marbles + marbleNumber
                    )

                return@fold snapshot.copy(
                    currentMarbleIndex = nextMarbleIndex,
                    marbles = snapshot.insertMarbleAfter(nextMarbleIndex, marbleNumber)
                )


            }.points.maxBy { it.value }!!.value
    }

    

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
    }

    fun secondStar(input: MetaInfo): Long {
        val points: MutableMap<Int, Long> = (0 until input.numberOfPlayers).groupBy({ it }, { 0 }).mapValues { it.value[0].toLong() } as MutableMap

        val first = Node(0L)
        val marbles = first.addNext(2L).addNext(1L)
        marbles.next = first
        first.previous = marbles

        var currentMarble: Node<Long> = marbles.previous!!

        var marbleNumber = 3L
        var currentPlayer = 3

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

    println(MarbleMania.firstStar(info))
    println(MarbleMania.secondStar(info.copy(lastMarblePoint = info.lastMarblePoint * 100)))
}
