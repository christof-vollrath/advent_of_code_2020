import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/*
--- Day 11: Seating System ---

See https://adventofcode.com/2020/day/11


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
