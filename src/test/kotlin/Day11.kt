import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/*
--- Day 11: Seating System ---

Your plane lands with plenty of time to spare.
The final leg of your journey is a ferry that goes directly to the tropical island
where you can finally start your vacation.
As you reach the waiting area to board the ferry, you realize you're so early, nobody else has even arrived yet!

By modeling the process people use to choose (or abandon) their seat in the waiting area,
you're pretty sure you can predict the best place to sit.
You make a quick map of the seat layout (your puzzle input).

The seat layout fits neatly on a grid. Each position is either floor (.),
an empty seat (L), or an occupied seat (#).
For example, the initial seat layout might look like this:

L.LL.LL.LL
LLLLLLL.LL
L.L.L..L..
LLLL.LL.LL
L.LL.LL.LL
L.LLLLL.LL
..L.L.....
LLLLLLLLLL
L.LLLLLL.L
L.LLLLL.LL

Now, you just need to model the people who will be arriving shortly.
Fortunately, people are entirely predictable and always follow a simple set of rules.
All decisions are based on the number of occupied seats adjacent to a given seat
(one of the eight positions immediately up, down, left, right, or diagonal from the seat).
The following rules are applied to every seat simultaneously:

If a seat is empty (L) and there are no occupied seats adjacent to it, the seat becomes occupied.
If a seat is occupied (#) and four or more seats adjacent to it are also occupied, the seat becomes empty.
Otherwise, the seat's state does not change.
Floor (.) never changes; seats don't move, and nobody sits on the floor.

After one round of these rules, every seat in the example layout becomes occupied:

#.##.##.##
#######.##
#.#.#..#..
####.##.##
#.##.##.##
#.#####.##
..#.#.....
##########
#.######.#
#.#####.##

After a second round, the seats with four or more occupied adjacent seats become empty again:

#.LL.L#.##
#LLLLLL.L#
L.L.L..L..
#LLL.LL.L#
#.LL.LL.LL
#.LLLL#.##
..L.L.....
#LLLLLLLL#
#.LLLLLL.L
#.#LLLL.##

This process continues for three more rounds:

#.##.L#.##
#L###LL.L#
L.#.#..#..
#L##.##.L#
#.##.LL.LL
#.###L#.##
..#.#.....
#L######L#
#.LL###L.L
#.#L###.##
#.#L.L#.##
#LLL#LL.L#
L.L.L..#..
#LLL.##.L#
#.LL.LL.LL
#.LL#L#.##
..L.L.....
#L#LLLL#L#
#.LLLLLL.L
#.#L#L#.##
#.#L.L#.##
#LLL#LL.L#
L.#.L..#..
#L##.##.L#
#.#L.LL.LL
#.#L#L#.##
..L.L.....
#L#L##L#L#
#.LLLLLL.L
#.#L#L#.##

At this point, something interesting happens: the chaos stabilizes
and further applications of these rules cause no seats to change state!
Once people stop moving around, you count 37 occupied seats.

Simulate your seating area by applying the seating rules repeatedly until no seats change state.
How many seats end up occupied?

To begin, get your puzzle input.
 */

fun List<List<Char>>.countOccupied() = flatten().count { it == '#' }

fun repeatRoundsUntilNothingChanges(waitingArea: List<List<Char>>): Pair<List<List<Char>>, Int> {
    var recentWaitingArea = waitingArea
    var rounds = 0
    while(true) {
        val nextWaitingArea = recentWaitingArea.oneRound()
        if (nextWaitingArea == recentWaitingArea) return recentWaitingArea to rounds
        recentWaitingArea = nextWaitingArea
        rounds++
    }
}

fun List<List<Char>>.oneRound(): List<List<Char>> = mapIndexed { y, line ->
    line.mapIndexed { x, c->
        rule(c, neighbors8(x, y))
    }
}

fun rule(c: Char, neighbors: List<Char>) =
    when (c) {
        'L' -> if(neighbors.countOccupied() == 0) '#'
        else c
        '#' -> if(neighbors.countOccupied() >= 4) 'L'
        else c
        else -> c
    }

fun Collection<Char>.countOccupied() = count {  it == '#' }

fun List<List<Char>>.neighbors8(x: Int, y: Int): List<Char> =
    Coord2(x, y).neighbors8().mapNotNull { getOrNull(it) }

fun List<List<Char>>.toPrintableString(): String = joinToString("\n") { line ->
    line.joinToString("")
}

fun parseWaitingArea(waitingAreaString: String): List<List<Char>>
        = waitingAreaString.split("\n").map { it.trim().toList() }

class Day11_Part1 : FunSpec({
    val waitingAreaString = """
    L.LL.LL.LL
    LLLLLLL.LL
    L.L.L..L..
    LLLL.LL.LL
    L.LL.LL.LL
    L.LLLLL.LL
    ..L.L.....
    LLLLLLLLLL
    L.LLLLLL.L
    L.LLLLL.LL
    """.trimIndent()
    context("find all 8 neighbors") {
        val neighbors = Coord2(0, 0).neighbors8()
        test("should have found 8 neighbors") {
            neighbors.size shouldBe 8
        }
    }
    context("parse waiting area") {
        val waitingArea = parseWaitingArea(waitingAreaString)
        test("parsed should have size 10 x 10") {
            waitingArea.size shouldBe 10
            waitingArea.forEach { line ->
                line.size shouldBe 10
            }
        }
        context("print waiting area") {
            val waitingAreaStringConverted = waitingArea.toPrintableString()
            test("should be converted to correct string") {
                waitingAreaStringConverted shouldBe waitingAreaString
            }
        }
    }
    context("occupation") {
        var currentWaitingArea = parseWaitingArea(waitingAreaString)
        context("one occupation round") {
            currentWaitingArea = currentWaitingArea.oneRound()
            test("should be occupied correctly") {
                currentWaitingArea.toPrintableString() shouldBe """
                #.##.##.##
                #######.##
                #.#.#..#..
                ####.##.##
                #.##.##.##
                #.#####.##
                ..#.#.....
                ##########
                #.######.#
                #.#####.##
                """.trimIndent()
            }
        }
        context("second occupation round") {
            currentWaitingArea = currentWaitingArea.oneRound()
            test("should be occupied correctly") {
                currentWaitingArea.toPrintableString() shouldBe """
                #.LL.L#.##
                #LLLLLL.L#
                L.L.L..L..
                #LLL.LL.L#
                #.LL.LL.LL
                #.LLLL#.##
                ..L.L.....
                #LLLLLLLL#
                #.LLLLLL.L
                #.#LLLL.##
                """.trimIndent()
            }
        }
    }
    context("occupation until nothing changes") {
        val waitingArea = parseWaitingArea(waitingAreaString)
        val (result, rounds) = repeatRoundsUntilNothingChanges(waitingArea)
        test("should have taken 5 rounds") {
            rounds shouldBe 5
        }
        test("should have 37 occupied seats") {
            result.countOccupied() shouldBe 37
        }
    }
})


class Day11_Part1_Exercise: FunSpec({
    val input = readResource("day11Input.txt")!!
    val waitingArea = parseWaitingArea(input)
    val (result, _) = repeatRoundsUntilNothingChanges(waitingArea)
    test("should have correct number of occupied seats") {
        result.countOccupied() shouldBe 2476
    }
})
