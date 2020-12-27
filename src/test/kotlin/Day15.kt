import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe

/*

--- Day 15: Rambunctious Recitation ---

See https://adventofcode.com/2020/day/15


 */

fun playMemGame(start: List<Int>): Sequence<Int> {
    val nrPos = mutableMapOf<Int,Int>()
    return sequence {
        var recentNr: Int? = null
        for (index in start.indices) {
            val n = start[index]
            if (recentNr != null) nrPos[recentNr] = index-1
            recentNr = n
            yield(n)
        }
        for (index in start.size until Int.MAX_VALUE) {
            val foundPos = nrPos[recentNr]
            val n = if (foundPos != null) {
                index - foundPos - 1
            } else 0
            nrPos[recentNr!!] = index-1
            recentNr = n
            yield(n)
        }
    }
}

class Day15_Part1 : FunSpec({
    context("play example game") {
        val result = playMemGame(listOf(0, 3, 6))
        val first2020 = result.take(2020).toList()
        test("should generate the right values") {
            first2020.take(10).toList() shouldBe listOf(0, 3, 6, 0, 3, 3, 1, 0, 4, 0)
        }
        test("should generate the right value at 2020") {
            first2020.last() shouldBe 436
        }
    }
    context("more examples") {
        table(
            headers("starting numbers", "expected"),
            row(listOf(1,3,2),    1),
            row(listOf(2,1,3),   10),
            row(listOf(1,2,3),   27),
            row(listOf(2,3,1),   78),
            row(listOf(3,2,1),  438),
            row(listOf(3,1,2), 1836),
        ).forAll { start, expected ->
            val result = playMemGame(start)
            result.drop(2019).first() shouldBe expected
        }
    }
})

class Day15_Part1_Exercise: FunSpec({
    val solution = playMemGame(listOf(2,0,1,9,5,19)).drop(2019).first()
    test("should have found solution") {
        solution shouldBe 1009
    }
})

class Day15_Part2 : FunSpec({
    xcontext("longer examples") { // This will take 90s
        table(
            headers("starting numbers", "expected"),
            row(listOf(0,3,6), 175594),
            row(listOf(1,3,2), 2578),
            row(listOf(2,1,3), 3544142),
            row(listOf(1,2,3), 261214),
            row(listOf(2,3,1), 6895259),
            row(listOf(3,2,1), 18),
            row(listOf(3,1,2), 362),
        ).forAll { start, expected ->
            val result = playMemGame(start)
            result.drop(30_000_000 - 1).first() shouldBe expected
        }
    }
})

class Day15_Part2_Exercise: FunSpec({
    xcontext("part 2") {// will take 22s
        val solution = playMemGame(listOf(2,0,1,9,5,19)).drop(30_000_000 - 1).first()
        test("should have found solution") {
            solution shouldBe 62714
        }
    }
})
