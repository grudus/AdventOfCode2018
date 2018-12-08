package com.grudus.adventofcode.day07

import com.grudus.adventofcode.day07.TheSumOfItsParts.firstStar
import com.grudus.adventofcode.day07.TheSumOfItsParts.secondStar
import com.grudus.adventofcode.findGroups
import com.grudus.adventofcode.readDayInput

object TheSumOfItsParts {

    private val pattern = Regex("Step (\\w) must be finished before step (\\w) can begin\\.").toPattern()

    data class Instruction(val step: String, val stepAfter: String)

    class Node<T : Comparable<T>>(val value: T, val isVisited: Boolean = false) {
        private val parents = mutableListOf<Node<T>>()
        private val children = mutableListOf<Node<T>>()

        private var visited = isVisited
        private var taken = false

        override fun equals(other: Any?) = (other as Node<T>).value == value

        override fun toString() = value.toString()

        fun markAsVisited() {
            visited = true
        }

        fun take() {
            taken = true
        }

        fun isFree() = !taken

        fun addChild(node: Node<T>) {
            children += node
        }

        fun addParent(node: Node<T>) {
            parents += node
        }

        fun isRoot() = parents.isEmpty()
        private fun isLeaf() = children.isEmpty()

        fun allVisited(): Boolean =
            if (isLeaf()) visited
            else children.all { it.allVisited() }


        fun visit(toVisit: Set<Node<T>> = emptySet()): Set<Node<T>> =
            if (visited) {
                children.flatMap { it.visit(toVisit) }.toSet()
            } else {
                if (parents.all { it.visited })
                    toVisit + this
                else
                    toVisit
            }
    }


    fun firstStar(input: List<String>): String {
        val tree = createTree(input)

        return generateSequence { 0 }
            .takeWhile { !tree.allVisited() }
            .fold("") { order, _ ->
                val notVisitedNodes = tree.visit()
                val nodeToVisit = notVisitedNodes.minBy { it.value }!!
                nodeToVisit.markAsVisited()
                order + nodeToVisit.value
            }
    }

    class Worker<T> {
        var currentWork: T? = null
        private var endTime = Int.MAX_VALUE

        fun setWork(work: T, endTime: Int) {
            this.currentWork = work
            this.endTime = endTime;
        }

        fun cleanWork() {
            currentWork = null;
            endTime = Int.MAX_VALUE;
        }

        fun isWorking() = currentWork != null
        fun isSiesta(time: Int) = endTime <= time
    }

    fun secondStar(input: List<String>): Int {
        val tree = createTree(input)
        val numberOfWorkers = 5
        val baseTime = 60
        val workers = (0 until numberOfWorkers).map { Worker<Node<String>>() }

        return generateSequence({ 0 }, { it + 1 })
            .takeWhile { !tree.allVisited()}
            .fold(1) { _, second ->
                val availableWorkers = workers.filter { !it.isWorking() }

                val notVisitedNodes: List<Node<String>> = tree.visit().filter { it.isFree() }

                val nodesToVisit = notVisitedNodes.sortedBy { it.value }.take(availableWorkers.size)

                availableWorkers.take(nodesToVisit.size).forEachIndexed { i, worker ->
                    val workTime = nodesToVisit[i].value[0].toInt() - 65 + baseTime
                    nodesToVisit[i].take()
                    worker.setWork(nodesToVisit[i],
                    second + workTime)
                }

                workers.filter { it.isSiesta(second) }
                    .forEach {
                        val work = it.currentWork
                        work!!.markAsVisited()
                        it.cleanWork()
                    }

                second + 1
            }
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
        val root = Node("0", isVisited = true)

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
    println(secondStar(input))

}
