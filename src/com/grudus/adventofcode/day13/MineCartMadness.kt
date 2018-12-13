package com.grudus.adventofcode.day13

import com.grudus.adventofcode.day13.MineCartMadness.Direction.DOWN
import com.grudus.adventofcode.day13.MineCartMadness.Direction.UP
import com.grudus.adventofcode.day13.MineCartMadness.TurnOption.*
import com.grudus.adventofcode.readDayInput
import javafx.geometry.Pos

object MineCartMadness {
    enum class Direction(val dx: Int, val dy: Int, val visualisation: Char) {
        UP(0, -1, '^'), LEFT(-1, 0, '<'), DOWN(0, 1, 'v'), RIGHT(1, 0, '>');

        fun nextLeft() = values()[(ordinal + 1) % values().size]
        fun nextRight() = values()[if (ordinal == 0) values().size - 1 else ordinal - 1]

        companion object {
            fun fromVisualisation(visualisation: Char): Direction = values()
                .find { it.visualisation == visualisation }!!
        }
    }

    enum class TurnOption {
        LEFT, STRAIGHT, RIGHT;

        fun nextTurnOption() = values()[(ordinal + 1) % values().size]
    }


    data class Position(val x: Int, val y: Int) {
        operator fun plus(dir: Direction): Position = Position(x + dir.dx, y + dir.dy)
        override fun toString() = "$x,$y"
    }

    data class Cart(
        var currentPosition: Position,
        val direction: Direction,
        val previousTurnOption: TurnOption = RIGHT
    ) {
        fun defaultNextCard(): Cart = copy(currentPosition = nextPosition())
        fun nextPosition(): Position = currentPosition + direction
    }


    enum class Track(val visualisation: Char, val nextPosition: (Cart) -> Cart) {
        VERTICAL_PATH('|', { it.defaultNextCard() }),
        HORIZONTAL_PATH('-', { it.defaultNextCard() }),
        LEFT_CURVE('/', { cart ->
            val dir = when (cart.direction) {
                Direction.RIGHT -> Direction.UP
                Direction.LEFT -> Direction.DOWN
                Direction.UP -> Direction.RIGHT
                Direction.DOWN -> Direction.LEFT
            }
            cart.copy(currentPosition = cart.nextPosition(), direction = dir)
        }),
        RIGHT_CURVE('\\', { cart ->
            val dir = when (cart.direction) {
                Direction.RIGHT -> Direction.DOWN
                Direction.LEFT -> Direction.UP
                Direction.UP -> Direction.LEFT
                Direction.DOWN -> Direction.RIGHT
            }
            cart.copy(currentPosition = cart.nextPosition(), direction = dir)
        }),
        INTERSECTION('+', { cart ->
            val turnOption = cart.previousTurnOption.nextTurnOption()
            val nextDirection = when (turnOption) {
                LEFT -> cart.direction.nextLeft()
                RIGHT -> cart.direction.nextRight()
                STRAIGHT -> cart.direction
            }
            cart.copy(
                currentPosition = cart.nextPosition(),
                previousTurnOption = turnOption,
                direction = nextDirection
            )
        });

        override fun toString() = visualisation.toString()

        companion object {
            fun fromVisualisation(visualisation: Char): Track? = values()
                .find { it.visualisation == visualisation }
        }
    }


    fun firstStar(input: List<String>): Position {
        val trackSystem: Map<Position, Track> = createTrackSystem(input)
        var cards: MutableList<Cart> = findCards(input) as MutableList<Cart>

        val isCollision: (Position) -> Boolean = { pos -> cards.any { it.currentPosition == pos } }

        while (true) {
            cards.sortWith(compareBy({ it.currentPosition.y }, { it.currentPosition.x }))


            for (i in 0 until cards.size) {
                val nextPosition = cards[i].nextPosition()

                if (isCollision(nextPosition))
                    return nextPosition

                val track = trackSystem[nextPosition]
                val nextPosition1 = track!!.nextPosition(cards[i])
                cards[i] = nextPosition1
            }
        }

    }


    private fun findCards(input: List<String>): List<Cart> =
        input.mapIndexed { y, mapRow ->
            mapRow.mapIndexed { x, map ->
                if (map == ' ')
                    null
                else
                    Pair(Position(x, y), map)
            }
        }.flatten()
            .filter { it != null }
            .filter { pair -> Track.fromVisualisation(pair!!.second) == null }
            .map { pair -> Cart(pair!!.first, Direction.fromVisualisation(pair.second)) }

    private fun createTrackSystem(input: List<String>): Map<Position, Track> =
        input.mapIndexed { y, mapRow ->
            mapRow.mapIndexed { x, map ->
                if (map == ' ')
                    null
                else {
                    val point = Position(x, y)
                    val track = Track.fromVisualisation(map) ?: (if (map == '>' || map == '<')
                        Track.HORIZONTAL_PATH else Track.VERTICAL_PATH)
                    Pair(point, track)
                }
            }
        }.flatten()
            .filter { it != null }
            .groupBy({ it!!.first }, { it!!.second })
            .mapValues { it.value[0] }

}


fun main(args: Array<String>) {
    val input = readDayInput("13")

    println(MineCartMadness.firstStar(input))


}
