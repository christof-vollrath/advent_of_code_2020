import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.lang.IllegalArgumentException

/*
--- Day 1: Report Repair ---

After saving Christmas five years in a row, you've decided to take a vacation at a nice resort on a tropical island.
Surely, Christmas will go on without you.

The tropical island has its own currency and is entirely cash-only.
The gold coins used there have a little picture of a starfish; the locals just call them stars.
None of the currency exchanges seem to have heard of them,
but somehow, you'll need to find fifty of these coins by the time you arrive so you can pay the deposit on your room.

To save your vacation, you need to get all fifty stars by December 25th.

Collect stars by solving puzzles. Two puzzles will be made available on each day in the Advent calendar;
the second puzzle is unlocked when you complete the first. Each puzzle grants one star. Good luck!

Before you leave, the Elves in accounting just need you to fix your expense report (your puzzle input);
apparently, something isn't quite adding up.

Specifically, they need you to find the two entries that sum to 2020 and then multiply those two numbers together.

For example, suppose your expense report contained the following:

1721
979
366
299
675
1456

In this list, the two entries that sum to 2020 are 1721 and 299.
Multiplying them together produces 1721 * 299 = 514579, so the correct answer is 514579.

Of course, your expense report is much larger.
Find the two entries that sum to 2020; what do you get if you multiply them together?

--- Part Two ---

The Elves in accounting are thankful for your help;
one of them even offers you a starfish coin they had left over from a past vacation.
They offer you a second one if you can find three numbers in your expense report that meet the same criteria.

Using the above example again, the three entries that sum to 2020 are 979, 366, and 675.
Multiplying them together produces the answer, 241861950.

In your expense report, what is the product of the three entries that sum to 2020?
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

