import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

/*
--- Day 24: Lobby Layout ---

See https://adventofcode.com/2020/day/24


 */

fun executeFlips(flipsString: String): TiledFloor {
    val flips = flipsString.split("\n").map { parseHexagonalMoves(it.trim()) }
    val tiledFloor = TiledFloor()
    flips.forEach { moves ->
        tiledFloor.flipTiles(moves)
    }
    return tiledFloor
}

fun TiledFloor.flipTiles(moves: List<HexagonalMove>) {
    var currCoord = Coord2(0, 0)
    moves.forEach { move ->
        currCoord = currCoord.moveHexagonal(move)
    }
    if (contains(currCoord)) remove(currCoord)
    else add(currCoord)

}

typealias TiledFloor = HashSet<Coord2>

fun parseHexagonalMoves(hexagonalMovesString: String): List<HexagonalMove> {
    var recentChar: Char? = null
    return hexagonalMovesString.mapNotNull { c ->
        when(c) {
            'e' -> {
                val result = when(recentChar) {
                    'n' -> HexagonalMove.NORTHEAST
                    's' -> HexagonalMove.SOUTHEAST
                    else -> HexagonalMove.EAST
                }
                recentChar = null
                result
            }
            'w' -> {
                val result = when(recentChar) {
                    'n' -> HexagonalMove.NORTHWEST
                    's' -> HexagonalMove.SOUTHWEST
                    else -> HexagonalMove.WEST
                }
                recentChar = null
                result
            }
            else -> {
                recentChar = c
                null
            }
        }
    }
}

enum class HexagonalMove { EAST, SOUTHEAST, SOUTHWEST, WEST, NORTHWEST, NORTHEAST }

/* Mapping of hexagonals to coords
    |00|10|
    /\ /\/
   |01|11|
    \/ \/\
    |02|12|
 */

fun Coord2.moveHexagonal(hexagonalMove: HexagonalMove): Coord2 =
    when(hexagonalMove) {
        HexagonalMove.EAST -> Coord2(x + 1, y)
        HexagonalMove.SOUTHEAST -> Coord2(if (y % 2 == 0) x + 1 else x, y + 1)
        HexagonalMove.SOUTHWEST -> Coord2(if (y % 2 == 0) x else x - 1, y + 1)
        HexagonalMove.WEST -> Coord2(x - 1, y)
        HexagonalMove.NORTHEAST -> Coord2(if (y % 2 == 0) x + 1 else x, y - 1)
        HexagonalMove.NORTHWEST -> Coord2(if (y % 2 == 0) x else x - 1, y - 1)
    }

fun TiledFloor.applyDailyRules(): TiledFloor {
    val result = TiledFloor()
    forEach { blackTileCoord2 ->
        val neighbors = blackTileCoord2.hexagonalNeighbors()
        val neighborSize = neighbors.filter { it in this }.size
        if (neighborSize in 1..2 ) result.add(blackTileCoord2)
    }
    val whiteTiles = flatMap { blackTileCoord2 ->
        blackTileCoord2.hexagonalNeighbors().filter { hexagonalNeighbor -> hexagonalNeighbor !in this}
    }
    whiteTiles.forEach { whitTileCoord2 ->
        val neighbors = whitTileCoord2.hexagonalNeighbors()
        val neighborSize = neighbors.filter { it in this }.size
        if (neighborSize == 2 ) result.add(whitTileCoord2)
    }
    return result
}

fun Coord2.hexagonalNeighbors(): List<Coord2> =
    HexagonalMove.values().map { this.moveHexagonal(it) }

val exampleflipsString = """
        sesenwnenenewseeswwswswwnenewsewsw
        neeenesenwnwwswnenewnwwsewnenwseswesw
        seswneswswsenwwnwse
        nwnwneseeswswnenewneswwnewseswneseene
        swweswneswnenwsewnwneneseenw
        eesenwseswswnenwswnwnwsewwnwsene
        sewnenenenesenwsewnenwwwse
        wenwwweseeeweswwwnwwe
        wsweesenenewnwwnwsenewsenwwsesesenwne
        neeswseenwwswnwswswnw
        nenwswwsewswnenenewsenwsenwnesesenew
        enewnwewneswsewnwswenweswnenwsenwsw
        sweneswneswneneenwnewenewwneswswnese
        swwesenesewenwneswnwwneseswwne
        enesenwswwswneneswsenwnewswseenwsese
        wnwnesenesenenwwnenwsewesewsesesew
        nenewswnwewswnenesenwnesewesw
        eneswnwswnwsenenwnwnwwseeswneewsenese
        neswnwewnwnwseenwseesewsenwsweewe
        wseweeenwnesenwwwswnew            
        """.trimIndent()

class Day24_Part1 : FunSpec({

    context("parse hexagonal moves") {
        val hexagonalMovesString = "nwswewesene"
        test("should parse correctly to list of moves") {
            val hexagonalMoves = parseHexagonalMoves(hexagonalMovesString)
            hexagonalMoves shouldBe listOf(HexagonalMove.NORTHWEST, HexagonalMove.SOUTHWEST, HexagonalMove.EAST,
                HexagonalMove.WEST, HexagonalMove.EAST, HexagonalMove.SOUTHEAST, HexagonalMove.NORTHEAST)
        }
    }
    context("hexagonal move") {
        var currCoord = Coord2(0, 0)
        currCoord = currCoord.moveHexagonal(HexagonalMove.NORTHWEST)
        currCoord shouldBe Coord2(0, -1)
        currCoord = currCoord.moveHexagonal(HexagonalMove.WEST)
        currCoord shouldBe Coord2(-1, -1)
        currCoord = currCoord.moveHexagonal(HexagonalMove.SOUTHWEST)
        currCoord shouldBe Coord2(-2, 0)
        currCoord = currCoord.moveHexagonal(HexagonalMove.EAST)
        currCoord shouldBe Coord2(-1, 0)
        currCoord = currCoord.moveHexagonal(HexagonalMove.EAST)
        currCoord shouldBe Coord2(0, 0)
    }
    context("some flips") {
        test("should flip adjacent tile") {
            val tiledFloor = TiledFloor()
            tiledFloor.flipTiles(parseHexagonalMoves("esew"))
            tiledFloor shouldContain Coord2(1, 1)
        }
        test("should all flip start tile") {
            table(
                headers("moves"),
                row("nwwswee"),
                row("swwnwee"),
                row("neeseww"),
                row("seeneww"),
                row("nnwwswees"),
                row("nswwnwees"),
                row("nneesewws"),
                row("nseenewws"),
                row("snwwsween"),
                row("sswwnween"),
                row("sneesewwn"),
                row("sseenewwn"),
            ).forAll { moves ->
                val tiledFloor = TiledFloor()
                tiledFloor.flipTiles(parseHexagonalMoves(moves))
                tiledFloor shouldContain Coord2(0, 0)
            }
        }
    }
    context("do several flips") {
        test("two flips should make two tiles black") {
            val flipsString = """
            esew
            sseenewwn
            nwwswee
            """.trimIndent()
            val tiledFloor = executeFlips(flipsString)
            tiledFloor.size shouldBe 1
        }
        test("three flips where the third undoes the second") {
            val flipsString = """
            esew
            sseenewwn
            """.trimIndent()
            val tiledFloor = executeFlips(flipsString)
            tiledFloor.size shouldBe 2
        }
    }
    context("example") {
        val tiledFloor = executeFlips(exampleflipsString)
        tiledFloor.size shouldBe 10
    }
})

class Day24_Part1_Exercise: FunSpec({
    val input = readResource("day24Input.txt")!!
    val tiledFloor = executeFlips(input)
    val solution = tiledFloor.size
    test("should have found the right number of black tiles") {
        solution shouldBe 351
    }
})

class Day24_Part2 : FunSpec({

    context("apply hexagonal rules") {
        var tiledFloor = executeFlips(exampleflipsString)

        test("day 1") {
            tiledFloor = tiledFloor.applyDailyRules()
            tiledFloor.size shouldBe 15
        }
        test("day 2") {
            tiledFloor = tiledFloor.applyDailyRules()
            tiledFloor.size shouldBe 12
        }
        test("day 3") {
            tiledFloor = tiledFloor.applyDailyRules()
            tiledFloor.size shouldBe 25
        }
        test("day 100") {
            repeat(97) { tiledFloor = tiledFloor.applyDailyRules() }
            tiledFloor.size shouldBe 2208
        }
    }
})

class Day24_Part2_Exercise: FunSpec({
    val input = readResource("day24Input.txt")!!
    var tiledFloor = executeFlips(input)
    repeat(100) { tiledFloor = tiledFloor.applyDailyRules() }
    val solution = tiledFloor.size
    test("should have flipped the right number of black tiles") {
        solution shouldBe 3869
    }

})
