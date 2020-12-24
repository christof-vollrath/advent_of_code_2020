import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

/*
--- Day 24: Lobby Layout ---

Your raft makes it to the tropical island;
it turns out that the small crab was an excellent navigator.
You make your way to the resort.

As you enter the lobby, you discover a small problem: the floor is being renovated.
You can't even reach the check-in desk until they've finished installing the new tile floor.

The tiles are all hexagonal; they need to be arranged in a hex grid with a very specific color pattern.
Not in the mood to wait, you offer to help figure out the pattern.

The tiles are all white on one side and black on the other. They start with the white side facing up.
The lobby is large enough to fit whatever pattern might need to appear there.

A member of the renovation crew gives you a list of the tiles that need to be flipped over (your puzzle input).
Each line in the list identifies a single tile that needs to be flipped by giving a series of steps
starting from a reference tile in the very center of the room.
(Every line starts from the same reference tile.)

Because the tiles are hexagonal, every tile has six neighbors:
east, southeast, southwest, west, northwest, and northeast.
These directions are given in your list, respectively, as e, se, sw, w, nw, and ne. A tile is identified by a series of these directions with no delimiters; for example, esenee identifies the tile you land on if you start at the reference tile and then move one tile east, one tile southeast, one tile northeast, and one tile east.

Each time a tile is identified, it flips from white to black or from black to white.
Tiles might be flipped more than once.
For example, a line like esew flips a tile immediately adjacent to the reference tile,
and a line like nwwswee flips the reference tile itself.

Here is a larger example:

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

In the above example, 10 tiles are flipped once (to black), and 5 more are flipped twice (to black, then back to white).
After all of these instructions have been followed, a total of 10 tiles are black.

Go through the renovation crew's list and determine which tiles they need to flip.
After all of the instructions have been followed, how many tiles are left with the black side up?

--- Part Two ---

The tile floor in the lobby is meant to be a living art exhibit.
Every day, the tiles are all flipped according to the following rules:

Any black tile with zero or more than 2 black tiles immediately adjacent to it is flipped to white.
Any white tile with exactly 2 black tiles immediately adjacent to it is flipped to black.
Here, tiles immediately adjacent means the six tiles directly touching the tile in question.

The rules are applied simultaneously to every tile; put another way,
it is first determined which tiles need to be flipped, then they are all flipped at the same time.

In the above example, the number of black tiles that are facing up
after the given number of days has passed is as follows:

Day 1: 15
Day 2: 12
Day 3: 25
Day 4: 14
Day 5: 23
Day 6: 28
Day 7: 41
Day 8: 37
Day 9: 49
Day 10: 37

Day 20: 132
Day 30: 259
Day 40: 406
Day 50: 566
Day 60: 788
Day 70: 1106
Day 80: 1373
Day 90: 1844
Day 100: 2208

After executing this process a total of 100 times, there would be 2208 black tiles facing up.

How many tiles will be black after 100 days?

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
        val flipsString = """
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
        val tiledFloor = executeFlips(flipsString)
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
