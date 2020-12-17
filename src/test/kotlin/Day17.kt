import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.collections.shouldNotBeIn
import io.kotest.matchers.shouldBe

/*
--- Day 17: Conway Cubes ---

As your flight slowly drifts through the sky,
the Elves at the Mythical Information Bureau at the North Pole contact you.
They'd like some help debugging a malfunctioning experimental energy source
aboard one of their super-secret imaging satellites.

The experimental energy source is based on cutting-edge technology:
a set of Conway Cubes contained in a pocket dimension!
When you hear it's having problems, you can't help but agree to take a look.

The pocket dimension contains an infinite 3-dimensional grid.
At every integer 3-dimensional coordinate (x,y,z), there exists a single cube which is either active or inactive.

In the initial state of the pocket dimension, almost all cubes start inactive.
The only exception to this is a small flat region of cubes (your puzzle input);
the cubes in this region start in the specified active (#) or inactive (.) state.

The energy source then proceeds to boot up by executing six cycles.

Each cube only ever considers its neighbors:
any of the 26 other cubes where any of their coordinates differ by at most 1.
For example, given the cube at x=1,y=2,z=3, its neighbors include the cube at x=2,y=2,z=2,
the cube at x=0,y=2,z=3, and so on.

During a cycle, all cubes simultaneously change their state according to the following rules:

If a cube is active and exactly 2 or 3 of its neighbors are also active, the cube remains active.
Otherwise, the cube becomes inactive.
If a cube is inactive but exactly 3 of its neighbors are active, the cube becomes active.
Otherwise, the cube remains inactive.
The engineers responsible for this experimental energy source would like you to simulate the pocket dimension
and determine what the configuration of cubes should be at the end of the six-cycle boot process.

For example, consider the following initial state:

.#.
..#
###

Even though the pocket dimension is 3-dimensional, this initial state represents a small 2-dimensional slice of it.
(In particular, this initial state defines a 3x3x1 region of the 3-dimensional space.)

Simulating a few cycles from this initial state produces the following configurations,
where the result of each cycle is shown layer-by-layer at each given z coordinate
(and the frame of view follows the active cells in each cycle):

Before any cycles:

z=0
.#.
..#
###


After 1 cycle:

z=-1
#..
..#
.#.

z=0
#.#
.##
.#.

z=1
#..
..#
.#.


After 2 cycles:

z=-2
.....
.....
..#..
.....
.....

z=-1
..#..
.#..#
....#
.#...
.....

z=0
##...
##...
#....
....#
.###.

z=1
..#..
.#..#
....#
.#...
.....

z=2
.....
.....
..#..
.....
.....


After 3 cycles:

z=-2
.......
.......
..##...
..###..
.......
.......
.......

z=-1
..#....
...#...
#......
.....##
.#...#.
..#.#..
...#...

z=0
...#...
.......
#......
.......
.....##
.##.#..
...#...

z=1
..#....
...#...
#......
.....##
.#...#.
..#.#..
...#...

z=2
.......
.......
..##...
..###..
.......
.......
.......

After the full six-cycle boot process completes, 112 cubes are left in the active state.

Starting with your given initial configuration, simulate six cycles.
How many cubes are left in the active state after the sixth cycle?

--- Part Two ---

For some reason, your simulated results don't match what the experimental energy source engineers expected.
Apparently, the pocket dimension actually has four spatial dimensions, not three.

The pocket dimension contains an infinite 4-dimensional grid.
At every integer 4-dimensional coordinate (x,y,z,w),
there exists a single cube (really, a hypercube) which is still either active or inactive.

Each cube only ever considers its neighbors: any of the 80 other cubes where any of their coordinates differ by at most 1.
For example, given the cube at x=1,y=2,z=3,w=4, its neighbors include the cube at x=2,y=2,z=3,w=3,
the cube at x=0,y=2,z=3,w=4, and so on.

The initial state of the pocket dimension still consists of a small flat region of cubes.
Furthermore, the same rules for cycle updating still apply: during each cycle,
consider the number of active neighbors of each cube.

For example, consider the same initial state as in the example above.
Even though the pocket dimension is 4-dimensional, this initial state represents a small 2-dimensional slice of it.
(In particular, this initial state defines a 3x3x1x1 region of the 4-dimensional space.)

Simulating a few cycles from this initial state produces the following configurations,
 where the result of each cycle is shown layer-by-layer at each given z and w coordinate:

Before any cycles:

z=0, w=0
.#.
..#
###


After 1 cycle:

z=-1, w=-1
#..
..#
.#.

z=0, w=-1
#..
..#
.#.

z=1, w=-1
#..
..#
.#.

z=-1, w=0
#..
..#
.#.

z=0, w=0
#.#
.##
.#.

z=1, w=0
#..
..#
.#.

z=-1, w=1
#..
..#
.#.

z=0, w=1
#..
..#
.#.

z=1, w=1
#..
..#
.#.


After 2 cycles:

z=-2, w=-2
.....
.....
..#..
.....
.....

z=-1, w=-2
.....
.....
.....
.....
.....

z=0, w=-2
###..
##.##
#...#
.#..#
.###.

z=1, w=-2
.....
.....
.....
.....
.....

z=2, w=-2
.....
.....
..#..
.....
.....

z=-2, w=-1
.....
.....
.....
.....
.....

z=-1, w=-1
.....
.....
.....
.....
.....

z=0, w=-1
.....
.....
.....
.....
.....

z=1, w=-1
.....
.....
.....
.....
.....

z=2, w=-1
.....
.....
.....
.....
.....

z=-2, w=0
###..
##.##
#...#
.#..#
.###.

z=-1, w=0
.....
.....
.....
.....
.....

z=0, w=0
.....
.....
.....
.....
.....

z=1, w=0
.....
.....
.....
.....
.....

z=2, w=0
###..
##.##
#...#
.#..#
.###.

z=-2, w=1
.....
.....
.....
.....
.....

z=-1, w=1
.....
.....
.....
.....
.....

z=0, w=1
.....
.....
.....
.....
.....

z=1, w=1
.....
.....
.....
.....
.....

z=2, w=1
.....
.....
.....
.....
.....

z=-2, w=2
.....
.....
..#..
.....
.....

z=-1, w=2
.....
.....
.....
.....
.....

z=0, w=2
###..
##.##
#...#
.#..#
.###.

z=1, w=2
.....
.....
.....
.....
.....

z=2, w=2
.....
.....
..#..
.....
.....

After the full six-cycle boot process completes, 848 cubes are left in the active state.

Starting with your given initial configuration, simulate six cycles in a 4-dimensional space.
How many cubes are left in the active state after the sixth cycle?

 */

fun Map<Coord3, Char>.cycle(): Map<Coord3, Char> {
    val survivers = keys.mapNotNull { coord3 ->
        val neighborCount = coord3.neighbors26().count { get(it) == '#' }
        if (neighborCount in 2..3) coord3 to '#'
        else null
    }
    val newborns = keys.flatMap { coord3 ->
        coord3.neighbors26().mapNotNull { neighbor -> // check neighbors for new born
            val neighborCountOfNeighbor = neighbor.neighbors26().count { get(it) == '#' }
            if (neighborCountOfNeighbor == 3) neighbor to '#'
            else null
        }
    }
    return (survivers + newborns).toMap()
}

fun Map<Coord3, Char>.toPrintableString(): String {
    val maxX = keys.map { it.x }.maxOrNull()!!
    val minX = keys.map { it.x }.minOrNull()!!
    val maxY = keys.map { it.y }.maxOrNull()!!
    val minY = keys.map { it.y }.minOrNull()!!
    val maxZ = keys.map { it.z }.maxOrNull()!!
    val minZ = keys.map { it.z }.minOrNull()!!
    return this.toPrintableString(minX..maxX, minY..maxY, minZ..maxZ)
}

fun Map<Coord3, Char>.toPrintableString(xRange: IntRange, yRange: IntRange, zRange: IntRange) =
    zRange.map { z->
        "z=$z\n" +
                yRange.map { y ->
                    xRange.map  { x ->
                        getOrDefault(Coord3(x, y, z), '.')
                    }.joinToString("")
                }.joinToString("\n")
    }.joinToString("\n\n")

fun parseCubeLayer(input: String): Map<Coord3, Char> {
    val inputLines = input.split("\n")
    val sizeY = inputLines.size
    val offsetY = sizeY / 2
    val sizeX = inputLines[0].length
    val offsetX = sizeX / 2
    return inputLines.flatMapIndexed { y, line ->
        line.mapIndexedNotNull { x, c ->
            if (c == '#') Coord3(x - offsetX, y - offsetY, 0) to c
            else null
        }
    }.toMap()
}

fun Map<Coord4, Char>.cycle4(): Map<Coord4, Char> {
    val survivers = keys.mapNotNull { coord4 ->
        val neighborCount = coord4.neighbors80().count { get(it) == '#' }
        if (neighborCount in 2..3) coord4 to '#'
        else null
    }
    val newborns = keys.flatMap { coord4 ->
        coord4.neighbors80().mapNotNull { neighbor -> // check neighbors for new born
            val neighborCountOfNeighbor = neighbor.neighbors80().count { get(it) == '#' }
            if (neighborCountOfNeighbor == 3) neighbor to '#'
            else null
        }
    }
    return (survivers + newborns).toMap()
}

fun Map<Coord4, Char>.toPrintableString4(): String {
    val maxX = keys.map { it.x }.maxOrNull()!!
    val minX = keys.map { it.x }.minOrNull()!!
    val maxY = keys.map { it.y }.maxOrNull()!!
    val minY = keys.map { it.y }.minOrNull()!!
    val maxZ = keys.map { it.z }.maxOrNull()!!
    val minZ = keys.map { it.z }.minOrNull()!!
    val maxW = keys.map { it.w }.maxOrNull()!!
    val minW = keys.map { it.w }.minOrNull()!!
    return this.toPrintableString4(minX..maxX, minY..maxY, minZ..maxZ, minW..maxW)
}

fun Map<Coord4, Char>.toPrintableString4(xRange: IntRange, yRange: IntRange, zRange: IntRange, wRange: IntRange) =
    wRange.map { w ->
        zRange.map { z ->
            "z=$z, w=$w\n" +
                    yRange.map { y ->
                        xRange.map { x ->
                            getOrDefault(Coord4(x, y, z, w), '.')
                        }.joinToString("")
                    }.joinToString("\n")
        }.joinToString("\n\n")
    }.joinToString("\n\n")

fun parseCubeLayer4(input: String): Map<Coord4, Char> {
    val inputLines = input.split("\n")
    val sizeY = inputLines.size
    val offsetY = sizeY / 2
    val sizeX = inputLines[0].length
    val offsetX = sizeX / 2
    return inputLines.flatMapIndexed { y, line ->
        line.mapIndexedNotNull { x, c ->
            if (c == '#') Coord4(x - offsetX, y - offsetY, 0, 0) to c
            else null
        }
    }.toMap()
}

class Day17_Part1 : FunSpec({
    context("neighbors 3d") {
        context("find neighbors for 0, 0, 0") {
            val neighbors = Coord3(0, 0, 0).neighbors26()
            test("should have found neighbors") {
                neighbors.size shouldBe 26
                Coord3(0, 0, 0) shouldNotBeIn neighbors
                Coord3(1, 0, 0) shouldBeIn neighbors
            }
        }
        context("find neighbors for 1, 2, 3") {
            val neighbors = Coord3(1, 2, 3).neighbors26()
            test("should have found neighbors") {
                neighbors.size shouldBe 26
                Coord3(2, 2, 2) shouldBeIn neighbors
                Coord3(0, 2, 3) shouldBeIn neighbors
            }
        }
    }
    context("parse cube layer") {
        val input = """
        .#.
        ..#
        ###
        """.trimIndent()
        val cube = parseCubeLayer(input)
        test("cube should have the right active points") {
            cube[Coord3(1, 1, 0)] shouldBe '#'
            cube[Coord3(0, -1, 0)] shouldBe '#'
            cube[Coord3(0, 0, 0)] shouldBe null
        }
    }
    context("print cube ") {
        val input = """
        .#.
        ..#
        ###
        """.trimIndent()
        val cube = parseCubeLayer(input)
        test("cube should be printed correctly") {
            cube.toPrintableString(-1..1, -1..1, 0..0) shouldBe """
        z=0
        .#.
        ..#
        ###
        """.trimIndent()
        }
    }
    context("apply cycles") {
        var cube = parseCubeLayer("""
        .#.
        ..#
        ###
        """.trimIndent())
        context("apply one cycle") {
            cube = cube.cycle()
            cube.toPrintableString() shouldBe """
            z=-1
            #..
            ..#
            .#.
            
            z=0
            #.#
            .##
            .#.
            
            z=1
            #..
            ..#
            .#.
            """.trimIndent()
        }
        context("apply second cycle") {
            cube = cube.cycle()
            cube.toPrintableString() shouldBe """
            z=-2
            .....
            .....
            ..#..
            .....
            .....
            
            z=-1
            ..#..
            .#..#
            ....#
            .#...
            .....
            
            z=0
            ##...
            ##...
            #....
            ....#
            .###.
            
            z=1
            ..#..
            .#..#
            ....#
            .#...
            .....
            
            z=2
            .....
            .....
            ..#..
            .....
            .....
            """.trimIndent()
        }
        context("apply third cycle") {
            cube = cube.cycle()
            cube.toPrintableString() shouldBe """
            z=-2
            .......
            .......
            ..##...
            ..###..
            .......
            .......
            .......
            
            z=-1
            ..#....
            ...#...
            #......
            .....##
            .#...#.
            ..#.#..
            ...#...
            
            z=0
            ...#...
            .......
            #......
            .......
            .....##
            .##.#..
            ...#...
            
            z=1
            ..#....
            ...#...
            #......
            .....##
            .#...#.
            ..#.#..
            ...#...
            
            z=2
            .......
            .......
            ..##...
            ..###..
            .......
            .......
            .......
            """.trimIndent()
        }
        context("apply three more cycle") {
            repeat(3) {
                cube = cube.cycle()
            }
            test("should have right number of active cubes") {
                cube.size shouldBe 112
            }
        }
    }
})

class Day17_Part1_Exercise: FunSpec({
    val input = readResource("day17Input.txt")!!
    var cube = parseCubeLayer(input)
    repeat(6) {
        cube = cube.cycle()
    }
    test("should have right number of active cubes") {
        cube.size shouldBe 338
    }
})

class Day17_Part2 : FunSpec({
    context("neighbors 4d") {
        context("find neighbors for 0, 0, 0, 0") {
            val neighbors = Coord4(0, 0, 0, 0).neighbors80()
            test("should have found neighbors") {
                neighbors.size shouldBe 80
                Coord4(0, 0, 0, 0) shouldNotBeIn neighbors
                Coord4(1, 0, 0, 1) shouldBeIn neighbors
            }
        }
        context("find neighbors for 1, 2, 3, 4") {
            val neighbors = Coord4(1, 2, 3, 4).neighbors80()
            test("should have found neighbors") {
                neighbors.size shouldBe 80
                Coord4(2, 2, 2, 3) shouldBeIn neighbors
                Coord4(0, 2, 3, 5) shouldBeIn neighbors
            }
        }
    }
    context("parse cube layer 4d") {
        val input = """
        .#.
        ..#
        ###
        """.trimIndent()
        val cube = parseCubeLayer4(input)
        test("cube should have the right active points") {
            cube[Coord4(1, 1, 0, 0)] shouldBe '#'
            cube[Coord4(0, -1, 0, 0)] shouldBe '#'
            cube[Coord4(0, 0, 0, 0)] shouldBe null
        }
    }
    context("print cube 4d") {
        val input = """
        .#.
        ..#
        ###
        """.trimIndent()
        val cube = parseCubeLayer4(input)
        test("cube should be printed correctly") {
            cube.toPrintableString4(-1..1, -1..1, 0..0, 0..0) shouldBe """
        z=0, w=0
        .#.
        ..#
        ###
        """.trimIndent()
        }
    }
    context("apply cycles 4d") {
        var cube = parseCubeLayer4("""
        .#.
        ..#
        ###
        """.trimIndent())
        context("apply one cycle") {
            cube = cube.cycle4()
            cube.toPrintableString4() shouldBe """
            z=-1, w=-1
            #..
            ..#
            .#.
            
            z=0, w=-1
            #..
            ..#
            .#.
            
            z=1, w=-1
            #..
            ..#
            .#.
            
            z=-1, w=0
            #..
            ..#
            .#.
            
            z=0, w=0
            #.#
            .##
            .#.
            
            z=1, w=0
            #..
            ..#
            .#.
            
            z=-1, w=1
            #..
            ..#
            .#.
            
            z=0, w=1
            #..
            ..#
            .#.
            
            z=1, w=1
            #..
            ..#
            .#.
            """.trimIndent()
        }
        context("apply five more cycle") {
            repeat(5) {
                cube = cube.cycle4()
            }
            test("should have right number of active cubes") {
                cube.size shouldBe 848
            }
        }
    }
})


class Day17_Part2_Exercise: FunSpec({
    val input = readResource("day17Input.txt")!!
    var cube = parseCubeLayer4(input)
    repeat(6) {
        cube = cube.cycle4()
    }
    test("should have right number of active cubes") {
        cube.size shouldBe 2440
    }
})
