package com.grudus.adventofcode.day14

object ChocolateCharts {

    fun firstStar(numberOfRecipes: Int): String {
        val recipes = mutableListOf(3, 7)

        var elf1Index = 0
        var elf2Index = 1

        while (recipes.size < numberOfRecipes + 10) {
            recipes += (recipes[elf1Index] + recipes[elf2Index]).toString().map(Character::getNumericValue)

            elf1Index = (elf1Index + recipes[elf1Index] + 1) % recipes.size
            elf2Index = (elf2Index + recipes[elf2Index] + 1) % recipes.size
        }

        return recipes.drop(numberOfRecipes).take(10).joinToString("")
    }


    fun secondStar(searchedSubsequence: String): Int {
        val recipes = mutableListOf(3, 7)

        var elf1Index = 0
        var elf2Index = 1

        while (searchedSubsequence !in recipes.takeLast(11).joinToString("")) {
            recipes += (recipes[elf1Index] + recipes[elf2Index]).toString().map(Character::getNumericValue)

            elf1Index = (elf1Index + recipes[elf1Index] + 1) % recipes.size
            elf2Index = (elf2Index + recipes[elf2Index] + 1) % recipes.size
        }

        return recipes.joinToString("").indexOf(searchedSubsequence)
    }
}


fun main(args: Array<String>) {
    println(ChocolateCharts.firstStar(30121))
    println(ChocolateCharts.secondStar("030121"))
}
