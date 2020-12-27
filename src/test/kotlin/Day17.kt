import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.collections.shouldNotBeIn
import io.kotest.matchers.shouldBe

/*
--- Day 17: Conway Cubes ---

See https://adventofcode.com/2020/day/17

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
