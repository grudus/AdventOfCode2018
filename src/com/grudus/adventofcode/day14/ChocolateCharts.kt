package com.grudus.adventofcode.day14

object ChocolateCharts {

    fun firstStar(numberOfRecipies: Int): String {
        val recipies = mutableListOf(3, 7)

        var elf1Index = 0
        var elf2Index = 1

        while (recipies.size < numberOfRecipies + 10) {
            val newRecipies = (recipies[elf1Index] + recipies[elf2Index]).toString()
                .map { Character.getNumericValue(it) }

            recipies += newRecipies

            elf1Index = (elf1Index + recipies[elf1Index] + 1) % recipies.size
            elf2Index = (elf2Index + recipies[elf2Index] + 1) % recipies.size
        }

        return recipies.drop(numberOfRecipies).take(10).joinToString("")
    }


    class Node(val value: Int) {
        var next: Node? = null
        var previous: Node? = null

        init {
            ++size
        }

        private fun addNext(value: Int): Node {
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


        companion object {
            var size = 0
            lateinit var last: Node

            fun addLast(value: Int) {
                last = last.addNext(value)
            }

            fun createCircular(value1: Int, value2: Int): Node {
                val node: Node = Node(value1).addNext(value2)
                node.next = node.previous
                node.previous!!.previous = node
                last = node
                return node
            }

            fun checkIfLastMatches(sequence: List<Int>): Boolean {
                var last = Node.last

                return sequence.reversed().all {
                    val value = last.value
                    last = last.previous!!
                    it == value
                }
            }
        }
    }


    fun secondStar(input: String): Int {
        val recipies = Node.createCircular(3, 7)
        val sequence = input.map { Character.getNumericValue(it) }

        var elf1 = recipies
        var elf2 = recipies.next!!

        while (true) {
            val newRecipies = (elf1.value + elf2.value).toString()
                .map { Character.getNumericValue(it) }

            Node.addLast(newRecipies[0])

            if (Node.checkIfLastMatches(sequence))
                return Node.size - sequence.size

            if (newRecipies.size == 2) {
                Node.addLast(newRecipies[1])

                if (Node.checkIfLastMatches(sequence))
                    return Node.size - sequence.size
            }

            (0..elf1.value).forEach {
                elf1 = elf1.next!!
            }

            (0..elf2.value).forEach {
                elf2 = elf2.next!!
            }
        }

    }


}


fun main(args: Array<String>) {
    val numberOfRecipies = 30121

    println(ChocolateCharts.firstStar(numberOfRecipies))
    println(ChocolateCharts.secondStar("030121"))
}
