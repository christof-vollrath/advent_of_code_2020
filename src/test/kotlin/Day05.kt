import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe

/*
--- Day 5: Binary Boarding ---

See https://adventofcode.com/2020/day/5

 */

fun decodeBoardingPass(passString: String): Int = decodeRows(passString.take(7)) * 8 + decodeColumns(passString.drop(7))

fun decodeRows(rowString: String) = decodeBinaryString( rowString.map {
    when(it) {
        'B' -> '1'
        'F' -> '0'
        else -> throw IllegalArgumentException("Unexpected row char $it")
    }
})

fun decodeColumns(colString: String) = decodeBinaryString( colString.map {
    when(it) {
        'R' -> '1'
        'L' -> '0'
        else -> throw IllegalArgumentException("Unexpected row char $it")
    }
})

fun decodeBinaryString(binaryString: List<Char>): Int = binaryString.fold(0) { current, n ->
    current * 2  + when(n) {
        '1' -> 1
        else -> 0
    }
}

class Day05_Part1 : FunSpec({
    context("decode row") {
        table(
            headers("row", "expected"),
            row("BFFFBBF", 70),
            row("FFFBBBF", 14),
            row("BBFFBBF", 102)
        ).forAll { rowString, expected ->
            val result = decodeRows(rowString)
            result shouldBe expected
        }
    }
    context("decode columns") {
        table(
            headers("columns", "expected"),
            row("RRR", 7),
            row("RLL", 4),
        ).forAll { colString, expected ->
            val result = decodeColumns(colString)
            result shouldBe expected
        }
    }
    context("decode boarding pass") {
        table(
            headers("boading pass", "expected"),
            row("BFFFBBFRRR", 567),
            row("FFFBBBFRRR", 119),
            row("BBFFBBFRLL", 820)
        ).forAll { passString, expected ->
            val result = decodeBoardingPass(passString)
            result shouldBe expected
        }
    }
})

class Day05_Part1_Exercise: FunSpec({
    val input = readResource("day05Input.txt")!!
    val passStrings = input.split("\n")
    val highestId = passStrings.map { decodeBoardingPass(it) }.maxOrNull()
    test("solution") {
        highestId shouldBe 978
    }
})

class Day05_Part2_Exercise: FunSpec({
    val input = readResource("day05Input.txt")!!
    val passStrings = input.split("\n")
    val ids = passStrings.map { decodeBoardingPass(it) }
    val idsInRowWithEmptySeat = ids.groupBy { id ->
        val row = id / 8
        row
    }
        .entries.filter { (_, value) ->
            value.size < 8
        }.first { (key, _) ->
            key != 1 && key != 122
        }
        .value
    val row = idsInRowWithEmptySeat.first() / 8
    val allSeatsInRow = (0..7).map { row * 8 + it }
    val freeSeat = (allSeatsInRow - idsInRowWithEmptySeat).first()
    test("solution") {
        freeSeat shouldBe 727
    }
})
