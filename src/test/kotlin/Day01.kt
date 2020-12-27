import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.lang.IllegalArgumentException

/*
--- Day 1: Report Repair ---

See https://adventofcode.com/2020/day/1

*/

fun findMatchingEntries(stars: List<Int>): Set<Int> {
    stars.forEach { star1 ->
        stars.forEach { star2 ->
            if (star1 + star2 == 2020) return setOf(star1, star2)
        }
    }
    throw IllegalArgumentException("No matching entries found")
}

fun find3MatchingEntries(stars: List<Int>): Set<Int> {
    stars.forEach { star1 ->
        stars.forEach { star2 ->
            if (star1 + star2 <= 2020) {
                stars.forEach { star3 ->
                    if (star1 + star2 + star3 == 2020) return setOf(star1, star2, star3)
                }
            }
        }
    }
    throw IllegalArgumentException("No matching entries found")
}

fun parseStars(inputString: String): List<Int> =
    inputString.split("\n").map { it.toInt() }

val exampleInput = """
        1721
        979
        366
        299
        675
        1456
    """.trimIndent()

class Day01_ReadInput : FunSpec({
    val stars = parseStars(exampleInput)

    test("should be parsed correctly") {
        stars.size shouldBe 6
        stars[0] shouldBe 1721
    }
})

class Day01_FindMatchingEntries : DescribeSpec({
    describe("find matching pair") {
        val matching = findMatchingEntries(parseStars(exampleInput))
        it("should find matching entries") {
            matching shouldBe setOf(1721 ,299)
        }
        describe("find solution") {
            val solution = calculateSolution(matching)
            it("should have calculated solution") {
                solution shouldBe 514579
            }
        }
    }
})

fun calculateSolution(entries: Set<Int>) = entries.fold(1) { x, y -> x * y }

class Day01_Part1: FunSpec({
    val inputStrings = readResource("day01Input.txt")!!
    val solution = calculateSolution(findMatchingEntries(parseStars(inputStrings)))
    test("solution") {
        solution shouldBe 381699
    }
})

class Day01_Find3MatchingEntries : DescribeSpec({
    describe("find matching pair") {
        val matching = find3MatchingEntries(parseStars(exampleInput))
        it("should find matching entries") {
            matching shouldBe setOf(979, 366, 675)
        }
        describe("find solution") {
            val solution = calculateSolution(matching)
            it("should have calculated solution") {
                solution shouldBe 241861950
            }
        }
    }
})

class Day01_Part2: FunSpec({
    val inputStrings = readResource("day01Input.txt")!!
    test("how big is the input") {
        parseStars(inputStrings).size shouldBe 200
    }
    val solution = calculateSolution(find3MatchingEntries(parseStars(inputStrings)))
    test("solution") {
        solution shouldBe 111605670
    }
})

