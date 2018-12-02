package com.grudus.adventofcode

import java.io.File

fun readDayInput(day: String): List<String> =
    File("src/com/grudus/adventofcode/day$day/input.txt").readLines()
