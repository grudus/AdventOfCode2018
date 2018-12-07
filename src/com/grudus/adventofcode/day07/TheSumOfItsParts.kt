package com.grudus.adventofcode.day07

import com.grudus.adventofcode.day07.TheSumOfItsParts.firstStar
import com.grudus.adventofcode.findGroups
import com.grudus.adventofcode.readDayInput

object TheSumOfItsParts {

    private val pattern = Regex("Step (\\w) must be finished before step (\\w) can begin\\.").toPattern()

    data class Instruction(val step: String, val stepAfter: String)

    class Node<T : Comparable<T>>(private val value: T) {
        private val parents = mutableListOf<Node<T>>()
        private val children = mutableListOf<Node<T>>()

        private var visited = false

        override fun toString() = value.toString()

        fun setVisited() {
            visited = true
        }

        fun addChild(node: Node<T>) {
            children += node
        }

        fun addParent(node: Node<T>) {
            parents += node
        }

        fun isRoot() = parents.isEmpty()
        fun isLeaf() = children.isEmpty()

        fun allVisited(): Boolean {
            if (isLeaf())
                return visited
            return children.all { it.allVisited() }
        }

        fun visitVal(value: T) {
            if (this.value == value)
                visited = true
            else
                children.forEach { it.visitVal(value) }
        }


        fun visit(toVisit: List<T>): List<T> {
            return if (visited) {
                children.flatMap { it.visit(toVisit) }
            } else {
                if (parents.all { it.visited })
                    toVisit + value
                else
                    toVisit
            }

        }
    }


    fun firstStar(input: List<String>): String {
        val tree = createTree(input)

        val order = mutableListOf<String>()

        while (!tree.allVisited()) {
            val x = tree.visit(emptyList())
            val min = x.min()!!
            tree.visitVal(min)
            order += min
        }
        return order.joinToString("")
    }


    private fun createTree(input: List<String>): Node<String> {
        val instructions = parseInput(input)

        val nodes: Map<String, Node<String>> = instructions.flatMap { listOf(it.step, it.stepAfter) }
            .groupBy({ it }, { Node(it) })
            .mapValues { Node(it.key) }


        instructions.forEach { instr ->
            val node = nodes[instr.step]!!
            val nodeAfter = nodes[instr.stepAfter]!!

            node.addChild(nodeAfter)
            nodeAfter.addParent(node)
        }

        val possibleRoots = nodes.values.filter { it.isRoot() }
        val root = Node("0")
        root.setVisited()

        possibleRoots.forEach {
            it.addParent(root)
            root.addChild(it)
        }

        return root
    }


    private fun parseInput(input: List<String>): List<Instruction> =
        input.map { line -> pattern.matcher(line).findGroups() }
            .map { groups -> Instruction(groups[0], groups[1]) }

}


fun main(args: Array<String>) {
    val input = readDayInput("07")

    println(firstStar(input))

}
