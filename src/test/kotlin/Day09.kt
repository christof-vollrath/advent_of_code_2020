import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/*
--- Day 9: Encoding Error ---

See https://adventofcode.com/2020/day/9

 */

fun List<Long>.checkProperty(preamble: Int): List<Long> {
    fun checkNumber(nr: Long, numbersToCheck: Set<Long>): Boolean {
        numbersToCheck.forEach { numberToCheck ->
            val diff = nr - numberToCheck
            if (diff != numberToCheck) {
                if (numbersToCheck.contains(diff)) return true
            }
        }
        return false
    }
    return sequence {
        for (i in preamble until size) {
            val numbersToCheck = drop(i-preamble).take(preamble).toSet()
            val number = get(i)
            if (! checkNumber(number, numbersToCheck)) yield(number)
        }
    }.toList()
}

fun List<Long>.findWeakness(wrongNumber: Long): Set<Long> {
    fun checkContiguousSet(from: Int): Set<Long>? {
        var sum = 0L
        val contiguousSet = sequence {
            for (i in from until size) {
                if (sum >= wrongNumber) break
                val current = get(i)
                yield(current)
                sum += current
            }
        }.toSet()
        return if (sum == wrongNumber) contiguousSet
        else null
    }
    for(from in 0 until size-1) {
        val contiguousSet = checkContiguousSet(from)
        if (contiguousSet != null) return contiguousSet
    }
    throw IllegalArgumentException("no solution found")
}

fun parseNumbers(numbersString: String): List<Long> =
    numbersString.split("\n").map{ it.toLong() }

val numbersString = """
        35
        20
        15
        25
        47
        40
        62
        55
        65
        95
        102
        117
        150
        182
        127
        219
        299
        277
        309
        576
    """.trimIndent()

class Day09_Part1 : FunSpec({
    val numbers = parseNumbers(numbersString)
    context("parse numbers") {
        test("numbers parsed correctly") {
            numbers.size shouldBe 20
        }
    }
    context("check numbers which have not this property") {
        val exampleSolution = numbers.checkProperty(5)
        exampleSolution shouldBe setOf(127)
    }
})

class Day09_Part1_Exercise: FunSpec({
    val input = readResource("day09Input.txt")!!
    val numbers = parseNumbers(input)
    val result = numbers.checkProperty(25)
    test("solution") {
        result.first() shouldBe 1721308972L
    }
})

class Day09_Part2 : FunSpec({
    val numbers = parseNumbers(numbersString)
    context("find weakness") {
        val wrongNumber = numbers.checkProperty(5).first()
        val weakness = numbers.findWeakness(wrongNumber)
        weakness.minOrNull() shouldBe 15
        weakness.maxOrNull() shouldBe 47
    }
})

class Day09_Part2_Exercise : FunSpec({
    val input = readResource("day09Input.txt")!!
    val numbers = parseNumbers(input)
    val wrongNumber = numbers.checkProperty(25).first()
    val weakness = numbers.findWeakness(wrongNumber)
    val min = weakness.minOrNull()!!
    val max = weakness.maxOrNull()!!
    val solution = min + max
    test("solution") {
        solution shouldBe 209694133L
    }
})
