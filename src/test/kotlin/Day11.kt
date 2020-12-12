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

--- Part Two ---

As soon as people start to arrive, you realize your mistake.
People don't just care about adjacent seats
- they care about the first seat they can see in each of those eight directions!

Now, instead of considering just the eight immediately adjacent seats,
consider the first seat in each of those eight directions.
For example, the empty seat below would see eight occupied seats:

.......#.
...#.....
.#.......
.........
..#L....#
....#....
.........
#........
...#.....

The leftmost empty seat below would only see one empty seat, but cannot see any of the occupied ones:

.............
.L.L.#.#.#.#.
.............

The empty seat below would see no occupied seats:

.##.##.
#.#.#.#
##...##
...L...
##...##
#.#.#.#
.##.##.

Also, people seem to be more tolerant than you expected:
it now takes five or more visible occupied seats for an occupied seat to become empty
(rather than four or more from the previous rules).
The other rules still apply: empty seats that see no occupied seats become occupied,
seats matching no rule don't change, and floor never changes.

Given the same starting layout as above, these new rules cause the seating area to shift around as follows:

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

#.LL.LL.L#
#LLLLLL.LL
L.L.L..L..
LLLL.LL.LL
L.LL.LL.LL
L.LLLLL.LL
..L.L.....
LLLLLLLLL#
#.LLLLLL.L
#.LLLLL.L#

#.L#.##.L#
#L#####.LL
L.#.#..#..
##L#.##.##
#.##.#L.##
#.#####.#L
..#.#.....
LLL####LL#
#.L#####.L
#.L####.L#

#.L#.L#.L#
#LLLLLL.LL
L.L.L..#..
##LL.LL.L#
L.LL.LL.L#
#.LLLLL.LL
..L.L.....
LLLLLLLLL#
#.LLLLL#.L
#.L#LL#.L#

#.L#.L#.L#
#LLLLLL.LL
L.L.L..#..
##L#.#L.L#
L.L#.#L.L#
#.L####.LL
..#.#.....
LLL###LLL#
#.LLLLL#.L
#.L#LL#.L#

#.L#.L#.L#
#LLLLLL.LL
L.L.L..#..
##L#.#L.L#
L.L#.LL.L#
#.LLLL#.LL
..#.L.....
LLL###LLL#
#.LLLLL#.L
#.L#LL#.L#

Again, at this point, people stop shifting around and the seating area reaches equilibrium.
Once this occurs, you count 26 occupied seats.

Given the new visibility method and the rule change for occupied seats becoming empty,
once equilibrium is reached, how many seats end up occupied?

 */

fun List<List<Char>>.countOccupied() = flatten().count { it == '#' }

typealias OneRound = List<List<Char>>.() -> List<List<Char>>

fun repeatRoundsUntilNothingChanges(waitingArea: List<List<Char>>, oneRoundPar: OneRound = List<List<Char>>::oneRound1): Pair<List<List<Char>>, Int> {
    var recentWaitingArea = waitingArea
    var round = 0
    while(true) {
        val nextWaitingArea = recentWaitingArea.oneRoundPar()
        if (nextWaitingArea == recentWaitingArea) return recentWaitingArea to round
        recentWaitingArea = nextWaitingArea
        round++
    }
}

typealias NeighborFinder = List<List<Char>>.(x: Int, y: Int) -> List<Char>
typealias Rule = (c: Char, neighbors: List<Char>) -> Char

fun List<List<Char>>.oneRound1(): List<List<Char>> = oneRound(List<List<Char>>::neighbors8, ::rule)

fun List<List<Char>>.oneRound(neighborsFinder: NeighborFinder = (List<List<Char>>::neighbors8), rule: Rule = ::rule): List<List<Char>> = mapIndexed { y, line ->
    line.mapIndexed { x, c->
        rule(c, neighborsFinder(x, y))
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

fun findVisibleOccupiedSeats(coord2: Coord2, waitingArea: List<List<Char>>): List<Char> {
    fun followOffset(coord2: Coord2, offset: Coord2): Char? {
        var currentPos = coord2 + offset
        while(true) {
            val c = waitingArea.getOrNull(currentPos) ?: return null
            if (c in setOf('L', '#')) return c
            currentPos += offset
        }
    }
    return Coord2.neighbor8Offsets.mapNotNull { offset ->
        followOffset(coord2, offset)
    }
}

fun List<List<Char>>.visibleNeighbors(x: Int, y: Int): List<Char> =
    findVisibleOccupiedSeats(Coord2(x, y), this)


fun rule2(c: Char, neighbors: List<Char>) =
    when (c) {
        'L' -> if(neighbors.countOccupied() == 0) '#'
        else c
        '#' -> if(neighbors.countOccupied() >= 5) 'L'
        else c
        else -> c
    }

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

class Day11_Part2 : FunSpec({
    context("find visible occupied seats") {
        context("occupied seats in every direction") {
            val waitingArea = parseWaitingArea("""
            .......#.
            ...#.....
            .#.......
            .........
            ..#L....#
            ....#....
            .........
            #........
            ...#.....       
            """.trimIndent())
            test("should find all eight seats") {
                val visibleSeats = findVisibleOccupiedSeats(Coord2(3, 4), waitingArea)
                visibleSeats.size shouldBe 8
            }
        }
        context("one occupied seat visible") {
            val waitingArea = parseWaitingArea("""
            .............
            .L.L.#.#.#.#.
            .............
            """.trimIndent())
            test("should see one occupied seats") {
                val visibleSeats = findVisibleOccupiedSeats(Coord2(1, 1), waitingArea)
                visibleSeats.size shouldBe 1
                visibleSeats[0] shouldBe 'L'
            }
        }
        context("no occupied seat visible") {
            val waitingArea = parseWaitingArea("""
                .##.##.
                #.#.#.#
                ##...##
                ...L...
                ##...##
                #.#.#.#
                .##.##.
            """.trimIndent())
            test("should find all eight seats") {
                val visibleSeats = findVisibleOccupiedSeats(Coord2(3, 3), waitingArea)
                visibleSeats.size shouldBe 0
            }
        }
    }
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
    context("occupation 2") {
        var currentWaitingArea = parseWaitingArea(waitingAreaString)
        context("one occupation round") {
            currentWaitingArea = currentWaitingArea.oneRound(List<List<Char>>::visibleNeighbors, ::rule2)
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
            currentWaitingArea = currentWaitingArea.oneRound(List<List<Char>>::visibleNeighbors, ::rule2)
            test("should be occupied correctly") {
                currentWaitingArea.toPrintableString() shouldBe """
                #.LL.LL.L#
                #LLLLLL.LL
                L.L.L..L..
                LLLL.LL.LL
                L.LL.LL.LL
                L.LLLLL.LL
                ..L.L.....
                LLLLLLLLL#
                #.LLLLLL.L
                #.LLLLL.L#
                """.trimIndent()
            }
        }
        context("third occupation round") {
            currentWaitingArea = currentWaitingArea.oneRound(List<List<Char>>::visibleNeighbors, ::rule2)
            test("should be occupied correctly") {
                currentWaitingArea.toPrintableString() shouldBe """
                #.L#.##.L#
                #L#####.LL
                L.#.#..#..
                ##L#.##.##
                #.##.#L.##
                #.#####.#L
                ..#.#.....
                LLL####LL#
                #.L#####.L
                #.L####.L#
                """.trimIndent()
            }
        }
    }
    context("occupation until nothing changes") {
        fun List<List<Char>>.oneRound2(): List<List<Char>> = oneRound(List<List<Char>>::visibleNeighbors, ::rule2)
        val waitingArea = parseWaitingArea(waitingAreaString)
        val (result, rounds) = repeatRoundsUntilNothingChanges(waitingArea, List<List<Char>>::oneRound2)
        test("should have taken 6 rounds") {
            rounds shouldBe 6
        }
        test("should have 26 occupied seats") {
            result.countOccupied() shouldBe 26
        }
    }
})

class Day11_Part2_Exercise: FunSpec({
    val input = readResource("day11Input.txt")!!
    val waitingArea = parseWaitingArea(input)
    fun List<List<Char>>.oneRound2(): List<List<Char>> = oneRound(List<List<Char>>::visibleNeighbors, ::rule2)
    val (result, _) = repeatRoundsUntilNothingChanges(waitingArea, List<List<Char>>::oneRound2)
    test("should have correct number of occupied seats") {
        result.countOccupied() shouldBe 2257
    }
})
