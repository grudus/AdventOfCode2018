package com.grudus.adventofcode.day14

object ChocolateCharts {

    fun firstStar(numberOfRecipies: Int): String {
        val recipies = mutableListOf(3, 7)

        var elf1Index = 0
        var elf2Index = 1

        while (recipies.size < numberOfRecipies + 10) {
            recipies += (recipies[elf1Index] + recipies[elf2Index]).toString().map(Character::getNumericValue)

            elf1Index = (elf1Index + recipies[elf1Index] + 1) % recipies.size
            elf2Index = (elf2Index + recipies[elf2Index] + 1) % recipies.size
        }

        return recipies.drop(numberOfRecipies).take(10).joinToString("")
    }


    fun secondStar(searchedSubsequence: String): Int {
        val recipies = mutableListOf(3, 7)

        var elf1Index = 0
        var elf2Index = 1

        while (searchedSubsequence !in recipies.takeLast(11).joinToString("")) {
            recipies += (recipies[elf1Index] + recipies[elf2Index]).toString().map(Character::getNumericValue)

            elf1Index = (elf1Index + recipies[elf1Index] + 1) % recipies.size
            elf2Index = (elf2Index + recipies[elf2Index] + 1) % recipies.size
        }

        return recipies.joinToString("").indexOf(searchedSubsequence)
    }
}


fun main(args: Array<String>) {
    println(ChocolateCharts.firstStar(30121))
    println(ChocolateCharts.secondStar("030121"))
}
