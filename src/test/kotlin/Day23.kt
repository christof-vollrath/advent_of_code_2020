import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.longs.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.mpp.timeInMillis
import org.magicwerk.brownies.collections.GapList

/*
--- Day 23: Crab Cups ---

See https://adventofcode.com/2020/day/23


 */

fun parseCrabCircle(crabCircleString: String): CrabCircle = CrabCircle(parseCrabCircleList(crabCircleString))

fun parseCrabCircleList(crabCircleString: String): List<Int> =
    crabCircleString.toList().map { it.toString().toInt() }


class CrabCircle(
    //val cups: GapList<Int>,
    val cups: GapList<Int>,
    val lowestCup: Int,
    val highestCup: Int,
    var currentPos: Int
) {
    constructor(initialCups: List<Int>): this(
        cups = GapList(initialCups),
        lowestCup = initialCups.minOrNull()!!,
        highestCup = initialCups.maxOrNull()!!,
        currentPos = 0
    )
    //val cupMap = mutableMapOf<Int, Int>()

    /*
    init {
        updateCupMap(0 until cups.size)
    }

    fun updateCupMap(modifiedRange: IntRange) {
        modifiedRange.forEach { i ->
            val cup = cups[i]
            cupMap[cup] = i
        }
    }

     */

    fun nextPos(pos: Int) = if (pos < cups.size-1) pos + 1 else 0
    fun toPrintableString(): String {
        var pos = cups.indexOf(1)
        return sequence {
            repeat(cups.size) {
                yield(cups[pos].toString())
                pos = nextPos(pos)
            }
        }.joinToString("")
    }
    var round = 0

    fun move() {
        round++
        val currentCup = cups[currentPos]
        val (toPick3Pos, toPick3) = toPick3(nextPos(currentPos))
        val destinationCup = findDestinationCup(currentCup, toPick3)
        val removedBeforeCurrentPos = toPick3Pos.filter { it <= currentPos }.size
        val destinationPos = cups.indexOf(destinationCup)
        val insertedBeforeCurrentPos = if (destinationPos >= currentPos) 0 else 3
        val calculatedCurrentPosAfterInsert = currentPos - removedBeforeCurrentPos + insertedBeforeCurrentPos
        removeAndInsert(toPick3, toPick3Pos, destinationPos)
        //val currentPosAfterInsert = cups.indexOf(currentCup)
        val currentPosAfterInsert = calculatedCurrentPosAfterInsert
        val nextPos = nextPos(currentPosAfterInsert)
        currentPos = nextPos
        if (round % 10_000 == 0) println(round)
    }

    fun findDestinationCup(cup: Int, picked3: List<Int>): Int {
        var nextTry = cup
        do {
            if (nextTry > lowestCup) nextTry--
            else nextTry = highestCup
        } while(nextTry in picked3)
        return nextTry
    }

    fun removeAndInsert(toPick3: List<Int>, toPick3Pos: List<Int>, destinationPos: Int) {
        val fullRange = 0 until cups.size
        //val destinationPos = cupMap[destinationCup]!!
        val modifiedRange = if (toPick3Pos[0] > toPick3Pos[2]) { // Removing things at the end makes it to complicted
            fullRange
        } else {
            if (destinationPos + 3 > cups.size) { // Also inserting at the end makes to complicated
                fullRange
            } else
                if (destinationPos < toPick3Pos.minOrNull()!!) destinationPos .. toPick3Pos.maxOrNull()!!
                else toPick3Pos.minOrNull()!! .. (destinationPos+2)
        }
        //println("Modified range $modifiedRange")
        val cupsRemovedBeforeDestinationPos = toPick3Pos.filter { it < destinationPos }.count()
        val insertPos = nextPos(destinationPos-cupsRemovedBeforeDestinationPos) // should insert right to the pos
        cups.removeAt(toPick3Pos)
        for(element in toPick3.reversed()) cups.add(insertPos, element)
        //updateCupMap(modifiedRange)
    }

    fun toPick3(startPos: Int): Pair<List<Int>, List<Int>> {
        var pos = startPos
        val posAndValueList = sequence {
            repeat(3) {
                yield(pos to cups[pos])
                pos = nextPos(pos)
            }
        }.toList()
        return posAndValueList.map { it.first } to posAndValueList.map { it.second }
    }
}

fun MutableList<Int>.removeAt(indexes: List<Int>) {
    var recentRemoveAt: Int? = null
    var offset: Int = 0
    indexes.forEachIndexed { index, pos ->
        val removeAt: Int
        if (recentRemoveAt != null && pos > recentRemoveAt!!) {
            removeAt = pos - offset
            offset++
        } else {
            removeAt = pos
            offset = 1
        }
        removeAt(removeAt)
        recentRemoveAt = removeAt
    }
}

class Day23_Part1 : FunSpec({
    val crabCircleString = "389125467"

    val crabCircle = parseCrabCircle(crabCircleString)
    context("parse and print crab circle") {
        test("should parse correctly to list of ints") {
            crabCircle.cups shouldBe listOf(3, 8, 9, 1, 2, 5, 4, 6, 7)
        }
        test("should print crab circle starting with label 1") {
            crabCircle.toPrintableString() shouldBe "125467389"
        }
        context("current cup should be 3 as the first cup") {
            crabCircle.cups[crabCircle.currentPos] shouldBe 3
        }
        context("move 1") {
            crabCircle.move()
            test("current cup should be 2") {
                crabCircle.cups[crabCircle.currentPos] shouldBe 2
            }
            test("cups 8, 9, 1 should have moved after 2") {
                crabCircle.toPrintableString() shouldBe "154673289"
            }
        }
        context("move 2") {
            crabCircle.move()
            test("current cup should be 5") {
                crabCircle.cups[crabCircle.currentPos] shouldBe 5
            }
            test("cups 8, 9, 1 should have moved after 7") {
                crabCircle.toPrintableString() shouldBe "132546789"
            }
        }
        context("8 more moves") {
            repeat(8) { crabCircle.move() }
            test("should have moved 8 times and match the example") {
                crabCircle.toPrintableString() shouldBe "192658374"
            }
        }
        context("90 more moves") {
            repeat(90) { crabCircle.move() }
            test("should have moved 90 times and match the example") {
                crabCircle.toPrintableString() shouldBe "167384529"
            }
        }
        context("try with a fresh start and do 100 moves") {
            val crabCircle = parseCrabCircle(crabCircleString)
            repeat(100) { crabCircle.move() }
            test("should have moved 100 times and match the example") {
                crabCircle.toPrintableString() shouldBe "167384529"
            }
            val solution = crabCircle.toPrintableString().drop(1).toInt()
            test("should have found solution") {
                solution shouldBe 67384529
            }
        }
    }

})

class Day23_Part1_Exercise: FunSpec({
    val crabCircle = parseCrabCircle("326519478")
    repeat(100) { crabCircle.move() }
    val solution = crabCircle.toPrintableString().drop(1).toInt()
    test("should have found solution") {
        solution shouldBe 25368479
    }
})

class Day23_Part2: FunSpec({
    context("measure performance") {
        val crabCircle = parseCrabCircleAndFill("326519478", 1000_000)
        val start = timeInMillis()
        repeat(10000) { crabCircle.move() }
        val took = timeInMillis() - start
        println("took=$took ms")
        took shouldBeLessThan 2000 // Otherwise it takes too long

    }


    xcontext("create a crab circle with 1000,000 cups") { // This will take about 13.5 hours using GapList as list implementation :-(
        // TODO try to improve performance with a custom implementation for shifting numbers in an ArrayList
        val crabCircle = parseCrabCircleAndFill("326519478", 1000_000)
        test("should have filed to 1000_000") {
            crabCircle.cups.size shouldBe 1000_000
            crabCircle.cups.maxOrNull() shouldBe 1000_000
        }
        context("play some rounds") {
            val start = timeInMillis()
            repeat(10_000_000) { crabCircle.move() }
            val took = timeInMillis() - start
            println("took=$took ms")
            val cup1pos = crabCircle.cups.indexOf(1)
            val next1 = crabCircle.cups[cup1pos + 1]
            val nextNext1 = crabCircle.cups[cup1pos + 2]
            println("next1=$next1")
            println("nextNext1=$nextNext1")
            val solution = next1.toLong() * nextNext1.toLong()
            println("solution=$solution")
            test("should have found solution") {
                solution shouldBe 44541319250L
            }
        }
    }
})

fun parseCrabCircleAndFill(crabCircleString: String, nr: Int): CrabCircle {
    val cupList = parseCrabCircleList(crabCircleString)
    val max = cupList.maxOrNull()!!
    val filledList = cupList + (max+1..nr).toList()
    return CrabCircle(filledList)
}
