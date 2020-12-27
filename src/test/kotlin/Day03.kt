import io.kotest.core.datatest.forAll
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/*
--- Day 3: Toboggan Trajectory ---

See https://adventofcode.com/2020/day/3

 */

fun parseTreeArray(input: String): TobogganTreeArray = TobogganTreeArray(
    input.split("\n").map {
        it.trim().toList()
    }
)

class TobogganTreeArray(val array: List<List<Char>>) {
    operator fun get(x: Int, y: Int) = array[y][x % array[y].size]
    fun countTrees(dx: Int = 3, dy: Int = 1) = (0 until size / dy).map { i ->
        get(i * dx, i * dy)
    }
        .count { it == '#' }
    fun multiplySlopeCounts(slopes: List<List<Int>>) = slopes.map { slope ->
        val (dx, dy) = slope
        countTrees(dx, dy)
    }.fold(1L) { m, count -> m * count }

    val size get() = array.size
}

val simpleExample = """
    ..##.......
    #...#...#..
    .#....#..#.
    ..#.#...#.#
    .#...##..#.
    ..#.##.....
    .#.#.#....#
    .#........#
    #.##...#...
    #...##....#
    .#..#...#.#    
    """.trimIndent()

class Day03_Part1_Example : FunSpec({
    val treeArray = parseTreeArray(simpleExample)
    context("parse") {
        test("should be parsed correctly") {
            treeArray.size shouldBe 11
            treeArray[0, 1] shouldBe '#'
        }
    }
    context("get") {
        test("pattern should be repeated to the right") {
            treeArray[11, 0] shouldBe '.'
            treeArray[30, 10] shouldBe '#'
        }
    }
    context("count trees") {
        val count = treeArray.countTrees()
        test("should have counted trees") {
            count shouldBe 7
        }
    }
})

class Day03_Part1: FunSpec({
    val input = readResource("day03Input.txt")!!
    val treeArray = parseTreeArray(input)
    val count = treeArray.countTrees()
    test("solution") {
        count shouldBe 292
    }
})

val slopes = listOf(
    listOf(1, 1),
    listOf(3, 1),
    listOf(5, 1),
    listOf(7, 1),
    listOf(1, 2),
)

class Day03_Part2_Example : FunSpec({
    val treeArray = parseTreeArray(simpleExample)
    context("count trees with different slopes") {
        data class SlopeTestCase(val dx: Int, val dy: Int, val expected: Int)
        context("count chars") {
            forAll(
                SlopeTestCase(1, 1, 2),
                SlopeTestCase(3, 1, 7),
                SlopeTestCase(5, 1, 3),
                SlopeTestCase(7, 1, 4),
                SlopeTestCase(1, 2, 2),
            ) { (dx, dy, expected) ->
                val count = treeArray.countTrees(dx, dy)
                count shouldBe expected
            }
        }

        val count = treeArray.countTrees()
        test("should have counted trees") {
            count shouldBe 7
        }
    }
    context("multiply tree count") {
        val multipliedCounts = treeArray.multiplySlopeCounts(slopes)
        multipliedCounts shouldBe 336
    }
})

class Day03_Part2: FunSpec({
    val input = readResource("day03Input.txt")!!
    val treeArray = parseTreeArray(input)
    val multipliedCounts = treeArray.multiplySlopeCounts(slopes)
    test("solution") {
        multipliedCounts shouldBe 9354744432L
    }
})
