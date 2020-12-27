import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe

/*
--- Day 13: Shuttle Search ---

See https://adventofcode.com/2020/day/13

 */

fun List<Int?>.findNextBus(earliest: Int): Pair<Int, Int> {
    val leavingNow = getOrNull(earliest)
    if (leavingNow != null) return leavingNow to 0 // Found a bus just leaving now
    val departureTimes = mapNotNull { busId ->
        if (busId == null) null
        else {
            val departureTime = (earliest + busId) / busId * busId
            busId to departureTime
        }
    }
    return departureTimes.minByOrNull { it.second }!!
}

fun parseTimeTable(timeTableString: String): List<Int?> = timeTableString.split(",").map { it.toIntOrNull()}

class Day13_Part1 : FunSpec({
    val busTimeTableString = "7,13,x,x,59,x,31,19"
    context("parse time table") {
        val timeTable = parseTimeTable(busTimeTableString)
        test("should have parsed 8 buses") {
            timeTable.size shouldBe 8
        }
        test("should have parsed the right time table") {
            timeTable shouldBe listOf(7, 13, null, null, 59, null, 31, 19)
        }
        context("find next bus") {
            val nextBus = timeTable.findNextBus(939)
            test("should have found the right bus") {
                nextBus.first shouldBe 59
                nextBus.second shouldBe 944
            }
            test("should have found solution") {
                calculateShuttleSolution(939, nextBus) shouldBe 295
            }
        }
    }
})

fun calculateShuttleSolution(earliest: Int, nextBus: Pair<Int, Int>) = (nextBus.second - earliest) * nextBus.first

fun calculateShuttleContest(timeTable: List<Int?>): Long {
    fun check(time: Long, timeTable: List<IndexedValue<Int?>>): Boolean {
        for(i in timeTable) {
            val busTime = i.value
            if (busTime != null && (time + i.index) % busTime != 0L) return false
        }
        return true
    }
    val timeTableWithIndex = timeTable.withIndex().filter { it.value != null }
    val checkBus = timeTableWithIndex.maxByOrNull { it.value!!}!! // Optimization: use highest id for loop
    println("checkBus=$checkBus")
    //var time = (checkBus.value!! - checkBus.index).toLong()
    var time = 539742675714343L // Was interrupted after this value
    var i = 0
    //TODO sort by bus id descending to start with the biggest
    // check only against the last bus
    while(true) {
        if (check(time, timeTableWithIndex)) return time
        time += checkBus.value!!
        if (i % 10_000_000 == 0) println("time=$time")
        i++
    }
}

class Day13_Part1_Exercise: FunSpec({
    val input = readResource("day13Input.txt")!!
    val inputLines = input.split("\n")
    val earliest = inputLines[0].toInt()
    val timeTable = parseTimeTable(inputLines[1])

    val nextBus = timeTable.findNextBus(earliest)
    val solution = calculateShuttleSolution(earliest, nextBus)
    test("should have found solution") {
        solution shouldBe 171
    }
})

class Day13_Part2 : FunSpec({
    xcontext("shuttle contest example") {
        val busTimeTableString = "7,13,x,x,59,x,31,19"
        val timeTable = parseTimeTable(busTimeTableString)
        val solution = calculateShuttleContest(timeTable)
        test("should have found solution") {
            solution shouldBe 1068781
        }
    }
    xcontext("more shuttle contest examples") {
        table(
            headers("bus ids", "expected"),
            row("17,x,13,19", 3417),
            row("67,7,59,61", 754018),
            row("67,x,7,59,61", 779210),
            row("67,7,x,59,61", 1261476),
            row("1789,37,47,1889", 1202161486),
        ).forAll { busTimeTableString, expected ->
            val timeTable = parseTimeTable(busTimeTableString)
            val solution = calculateShuttleContest(timeTable)
            solution shouldBe expected
        }
    }
})

class Day13_Part2_Exercise: FunSpec({
    val input = readResource("day13Input.txt")!!
    val inputLines = input.split("\n")
    val timeTable = parseTimeTable(inputLines[1])

    xcontext("test") {
        val solution = calculateShuttleContest(timeTable)
        test("should have found solution") {
            solution shouldBe 539746751134958L
        }
    }
})
