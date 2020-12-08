import io.kotest.core.datatest.forAll
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/*
--- Day 3: Toboggan Trajectory ---

With the toboggan login problems resolved, you set off toward the airport.
While travel by toboggan might be easy, it's certainly not safe:
there's very minimal steering and the area is covered in trees.
You'll need to see which angles will take you near the fewest trees.

Due to the local geology, trees in this area only grow on exact integer coordinates in a grid.
You make a map (your puzzle input) of the open squares (.) and trees (#) you can see.

For example:

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

These aren't the only trees, though;
due to something you read about once involving arboreal genetics and biome stability,
the same pattern repeats to the right many times:

..##.........##.........##.........##.........##.........##.......  --->
#...#...#..#...#...#..#...#...#..#...#...#..#...#...#..#...#...#..
.#....#..#..#....#..#..#....#..#..#....#..#..#....#..#..#....#..#.
..#.#...#.#..#.#...#.#..#.#...#.#..#.#...#.#..#.#...#.#..#.#...#.#
.#...##..#..#...##..#..#...##..#..#...##..#..#...##..#..#...##..#.
..#.##.......#.##.......#.##.......#.##.......#.##.......#.##.....  --->
.#.#.#....#.#.#.#....#.#.#.#....#.#.#.#....#.#.#.#....#.#.#.#....#
.#........#.#........#.#........#.#........#.#........#.#........#
#.##...#...#.##...#...#.##...#...#.##...#...#.##...#...#.##...#...
#...##....##...##....##...##....##...##....##...##....##...##....#
.#..#...#.#.#..#...#.#.#..#...#.#.#..#...#.#.#..#...#.#.#..#...#.#  --->

You start on the open square (.) in the top-left corner and need to reach the bottom
(below the bottom-most row on your map).

The toboggan can only follow a few specific slopes (you opted for a cheaper model that prefers rational numbers);
start by counting all the trees you would encounter for the slope right 3, down 1:

From your starting position at the top-left, check the position that is right 3 and down 1.
Then, check the position that is right 3 and down 1 from there, and so on until you go past the bottom of the map.

The locations you'd check in the above example are marked here with O
where there was an open square and X where there was a tree:

..##.........##.........##.........##.........##.........##.......  --->
#..O#...#..#...#...#..#...#...#..#...#...#..#...#...#..#...#...#..
.#....X..#..#....#..#..#....#..#..#....#..#..#....#..#..#....#..#.
..#.#...#O#..#.#...#.#..#.#...#.#..#.#...#.#..#.#...#.#..#.#...#.#
.#...##..#..X...##..#..#...##..#..#...##..#..#...##..#..#...##..#.
..#.##.......#.X#.......#.##.......#.##.......#.##.......#.##.....  --->
.#.#.#....#.#.#.#.O..#.#.#.#....#.#.#.#....#.#.#.#....#.#.#.#....#
.#........#.#........X.#........#.#........#.#........#.#........#
#.##...#...#.##...#...#.X#...#...#.##...#...#.##...#...#.##...#...
#...##....##...##....##...#X....##...##....##...##....##...##....#
.#..#...#.#.#..#...#.#.#..#...X.#.#..#...#.#.#..#...#.#.#..#...#.#  --->

In this example, traversing the map using this slope would cause you to encounter 7 trees.

Starting at the top-left corner of your map and following a slope of right 3 and down 1,
how many trees would you encounter?

--- Part Two ---

Time to check the rest of the slopes - you need to minimize the probability of a sudden arboreal stop, after all.

Determine the number of trees you would encounter if,
for each of the following slopes, you start at the top-left corner and traverse the map all the way to the bottom:

Right 1, down 1.
Right 3, down 1. (This is the slope you already checked.)
Right 5, down 1.
Right 7, down 1.
Right 1, down 2.

In the above example, these slopes would find 2, 7, 3, 4, and 2 tree(s) respectively;
multiplied together, these produce the answer 336.

What do you get if you multiply together the number of trees encountered on each of the listed slopes?


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
