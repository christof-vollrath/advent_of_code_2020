import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe

/*

--- Day 15: Rambunctious Recitation ---

You catch the airport shuttle and try to book a new flight to your vacation island.
Due to the storm, all direct flights have been cancelled, but a route is available to get around the storm. You take it.

While you wait for your flight, you decide to check in with the Elves back at the North Pole.
They're playing a memory game and are ever so excited to explain the rules!

In this game, the players take turns saying numbers.
They begin by taking turns reading from a list of starting numbers (your puzzle input).
Then, each turn consists of considering the most recently spoken number:

If that was the first time the number has been spoken, the current player says 0.
Otherwise, the number had been spoken before; the current player announces
how many turns apart the number is from when it was previously spoken.
So, after the starting numbers, each turn results in that player speaking aloud either 0
(if the last number is new) or an age (if the last number is a repeat).

For example, suppose the starting numbers are 0,3,6:

Turn 1: The 1st number spoken is a starting number, 0.
Turn 2: The 2nd number spoken is a starting number, 3.
Turn 3: The 3rd number spoken is a starting number, 6.

Turn 4: Now, consider the last number spoken, 6. Since that was the first time the number had been spoken,
the 4th number spoken is 0.
Turn 5: Next, again consider the last number spoken, 0. Since it had been spoken before,
the next number to speak is the difference between the turn number when it was last spoken (the previous turn, 4)
and the turn number of the time it was most recently spoken before then (turn 1). Thus, the 5th number spoken is 4 - 1, 3.
Turn 6: The last number spoken, 3 had also been spoken before, most recently on turns 5 and 2.
So, the 6th number spoken is 5 - 2, 3.
Turn 7: Since 3 was just spoken twice in a row, and the last two turns are 1 turn apart, the 7th number spoken is 1.
Turn 8: Since 1 is new, the 8th number spoken is 0.
Turn 9: 0 was last spoken on turns 8 and 4, so the 9th number spoken is the difference between them, 4.
Turn 10: 4 is new, so the 10th number spoken is 0.

(The game ends when the Elves get sick of playing or dinner is ready, whichever comes first.)

Their question for you is: what will be the 2020th number spoken? In the example above,
the 2020th number spoken will be 436.

Here are a few more examples:

Given the starting numbers 1,3,2, the 2020th number spoken is 1.
Given the starting numbers 2,1,3, the 2020th number spoken is 10.
Given the starting numbers 1,2,3, the 2020th number spoken is 27.
Given the starting numbers 2,3,1, the 2020th number spoken is 78.
Given the starting numbers 3,2,1, the 2020th number spoken is 438.
Given the starting numbers 3,1,2, the 2020th number spoken is 1836.
Given your starting numbers, what will be the 2020th number spoken?

Your puzzle input is 2,0,1,9,5,19.

--- Part Two ---

Impressed, the Elves issue you a challenge: determine the 30000000th number spoken.
For example, given the same starting numbers as above:

Given 0,3,6, the 30000000th number spoken is 175594.
Given 1,3,2, the 30000000th number spoken is 2578.
Given 2,1,3, the 30000000th number spoken is 3544142.
Given 1,2,3, the 30000000th number spoken is 261214.
Given 2,3,1, the 30000000th number spoken is 6895259.
Given 3,2,1, the 30000000th number spoken is 18.
Given 3,1,2, the 30000000th number spoken is 362.

Given your starting numbers, what will be the 30000000th number spoken?

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