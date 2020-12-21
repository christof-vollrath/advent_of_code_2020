import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.math.sqrt

/*
--- Day 20: Jurassic Jigsaw ---

The high-speed train leaves the forest and quickly carries you south.
You can even see a desert in the distance!
Since you have some spare time, you might as well see
if there was anything interesting in the image the Mythical Information Bureau satellite captured.

After decoding the satellite messages, you discover that the data actually contains many small images
created by the satellite's camera array.
The camera array consists of many cameras; rather than produce a single square image,
they produce many smaller square image tiles that need to be reassembled back into a single image.

Each camera in the camera array returns a single monochrome image tile with a random unique ID number.
The tiles (your puzzle input) arrived in a random order.

Worse yet, the camera array appears to be malfunctioning:
each image tile has been rotated and flipped to a random orientation.
Your first task is to reassemble the original image by orienting the tiles so they fit together.

To show how the tiles should be reassembled,
each tile's image data includes a border that should line up exactly with its adjacent tiles.
All tiles have this border, and the border lines up exactly when the tiles are both oriented correctly.
Tiles at the edge of the image also have this border, but the outermost edges won't line up with any other tiles.

For example, suppose you have the following nine tiles:

Tile 2311:
..##.#..#.
##..#.....
#...##..#.
####.#...#
##.##.###.
##...#.###
.#.#.#..##
..#....#..
###...#.#.
..###..###

Tile 1951:
#.##...##.
#.####...#
.....#..##
#...######
.##.#....#
.###.#####
###.##.##.
.###....#.
..#.#..#.#
#...##.#..

Tile 1171:
####...##.
#..##.#..#
##.#..#.#.
.###.####.
..###.####
.##....##.
.#...####.
#.##.####.
####..#...
.....##...

Tile 1427:
###.##.#..
.#..#.##..
.#.##.#..#
#.#.#.##.#
....#...##
...##..##.
...#.#####
.#.####.#.
..#..###.#
..##.#..#.

Tile 1489:
##.#.#....
..##...#..
.##..##...
..#...#...
#####...#.
#..#.#.#.#
...#.#.#..
##.#...##.
..##.##.##
###.##.#..

Tile 2473:
#....####.
#..#.##...
#.##..#...
######.#.#
.#...#.#.#
.#########
.###.#..#.
########.#
##...##.#.
..###.#.#.

Tile 2971:
..#.#....#
#...###...
#.#.###...
##.##..#..
.#####..##
.#..####.#
#..#.#..#.
..####.###
..#.#.###.
...#.#.#.#

Tile 2729:
...#.#.#.#
####.#....
..#.#.....
....#..#.#
.##..##.#.
.#.####...
####.#.#..
##.####...
##..#.##..
#.##...##.

Tile 3079:
#.#.#####.
.#..######
..#.......
######....
####.#..#.
.#...#.##.
#.#####.##
..#.###...
..#.......
..#.###...

By rotating, flipping, and rearranging them,
you can find a square arrangement that causes all adjacent borders to line up:

#...##.#.. ..###..### #.#.#####.
..#.#..#.# ###...#.#. .#..######
.###....#. ..#....#.. ..#.......
###.##.##. .#.#.#..## ######....
.###.##### ##...#.### ####.#..#.
.##.#....# ##.##.###. .#...#.##.
#...###### ####.#...# #.#####.##
.....#..## #...##..#. ..#.###...
#.####...# ##..#..... ..#.......
#.##...##. ..##.#..#. ..#.###...

#.##...##. ..##.#..#. ..#.###...
##..#.##.. ..#..###.# ##.##....#
##.####... .#.####.#. ..#.###..#
####.#.#.. ...#.##### ###.#..###
.#.####... ...##..##. .######.##
.##..##.#. ....#...## #.#.#.#...
....#..#.# #.#.#.##.# #.###.###.
..#.#..... .#.##.#..# #.###.##..
####.#.... .#..#.##.. .######...
...#.#.#.# ###.##.#.. .##...####

...#.#.#.# ###.##.#.. .##...####
..#.#.###. ..##.##.## #..#.##..#
..####.### ##.#...##. .#.#..#.##
#..#.#..#. ...#.#.#.. .####.###.
.#..####.# #..#.#.#.# ####.###..
.#####..## #####...#. .##....##.
##.##..#.. ..#...#... .####...#.
#.#.###... .##..##... .####.##.#
#...###... ..##...#.. ...#..####
..#.#....# ##.#.#.... ...##.....

For reference, the IDs of the above tiles are:

1951    2311    3079
2729    1427    2473
2971    1489    1171

To check that you've assembled the image correctly, multiply the IDs of the four corner tiles togethr.
If you do this with the assembled tiles from the example above, you get 1951 * 3079 * 2971 * 1171 = 20899048083289.

Assemble the tiles into an image. What do you get if you multiply together the IDs of the four corner tiles?

 */

fun findSolutionTiles(tiles: List<Tile>): List<List<Tile>> {
    val expectedSizeOfSolution = sqrt(tiles.size.toFloat()).toInt()
    val tileConnections = tiles.findConnections()
    val cornerCandidates = tileConnections.filter { it.isTopLeftCorner() } // Corners dont need to be turned because the whole solution would just be turned
    println("cornerCandidates.size=${cornerCandidates.size}")
    for (cornerCandidate in cornerCandidates) {
        println("cornerCandidate=$cornerCandidate")
        val topRow = tiles.completeEasternNeighbor(cornerCandidate.tile)
        println("topRow=${topRow.map { it.id }}")
        if (topRow.size != expectedSizeOfSolution) continue
        println("topRow=$topRow")
        val solution = tiles.completeSouthernNeighbors(topRow)
        if (solution.size == expectedSizeOfSolution && solution.first().size == expectedSizeOfSolution) return solution
    }
    throw java.lang.IllegalArgumentException("No solution found")
}

fun List<List<Tile>>.toIds() = this.map { row ->
    row.map { it.id }
}
fun List<List<Tile>>.calculateCornerProduct(): Long {
    val idArray = this.toIds()
    val topLeft = idArray.first().first()
    val topRight = idArray.first().last()
    val bottomLeft = idArray.last().first()
    val bottomRight = idArray.last().last()
    return topLeft.toLong() * topRight.toLong() * bottomLeft.toLong() * bottomRight.toLong()
}


fun List<Tile>.completeSouthernNeighbors(startRow: List<Tile>): List<List<Tile>> {
    var remainingTiles = this
    return sequence {
        yield(startRow)
        remainingTiles = remainingTiles.removeByIds(startRow.map { it.id })
        var currentTileRow = startRow
        while(remainingTiles.isNotEmpty()) {
            val next = remainingTiles.searchSouthernNeighbors(currentTileRow)
            if (next.isEmpty()) break
            yield(next)
            currentTileRow = next
            remainingTiles = remainingTiles.removeByIds(next.map { it.id })
        }
    }.toList()
}

fun List<Tile>.searchSouthernNeighbor(tile: Tile): Tile? {
    val connectedToSouth = searchConnected(tile, this) { tileArray: TileArray, other: TileArray ->
        tileArray.southBorder() == other.northBorder() }.firstOrNull()
    connectedToSouth ?: return null
    val connectedTile =  this.find { it.id == connectedToSouth.second }!!
    return Tile(connectedTile.id, connectedTile.array.variation(connectedToSouth.first))
}

fun List<Tile>.searchSouthernNeighbors(startRow: List<Tile>): List<Tile> {
    var result = emptyList<Tile>()
    for(tile in startRow) {
        val next = this.removeByIds((startRow + result).map { it.id }).searchSouthernNeighbor(tile)
        if (next != null) result = result + next
    }
    return result
}

fun List<Tile>.completeEasternNeighbor(start: Tile): List<Tile> {
    var remainingTiles = this
    return sequence {
        yield(start)
        remainingTiles = remainingTiles.removeById(start.id)
        var currentTile = start
        while(remainingTiles.isNotEmpty()) {
            val nextCandidates = remainingTiles.searchEasternNeighborCandidates(currentTile)
            println("eastern neigbors for ${currentTile.id}=${nextCandidates.map { it.id }}")
            val next = nextCandidates.firstOrNull() ?: break
            yield(next)
            currentTile = next
            remainingTiles = remainingTiles.removeById(next.id)
        }
    }.toList()
}

fun List<Tile>.searchEasternNeighbor(tile: Tile): Tile? {
    val connectedToEast = searchConnected(tile, this) { tileArray: TileArray, other: TileArray ->
        tileArray.eastBorder() == other.westBorder() }.firstOrNull()
    connectedToEast ?: return null
    val connectedTile =  this.find { it.id == connectedToEast.second }!!
    return Tile(connectedTile.id, connectedTile.array.variation(connectedToEast.first))
}

fun List<Tile>.searchEasternNeighborCandidates(tile: Tile): List<Tile> {
    val connectedToEast = searchConnected(tile, this) { tileArray: TileArray, other: TileArray ->
        tileArray.eastBorder() == other.westBorder()
    }
    return connectedToEast.map { tileVariation ->
        val connectedTile = this.find { it.id == tileVariation.second }!!
        Tile(connectedTile.id, connectedTile.array.variation(tileVariation.first))
    }
}

fun searchConnected(tile: Tile, inTiles: List<Tile>, check: (TileArray, TileArray) -> Boolean) = inTiles.flatMap { otherTile ->
    if (otherTile.id == tile.id) emptyList()
    else {
        val variations = otherTile.inAllVariations()
        variations.mapNotNull { variation ->
            if (check(tile.array, variation.second))
                variation.first to otherTile.id
            else null
        }
    }
}

fun List<Tile>.findConnections(): List<TileConnection> {
    return map { tile ->
        val north = searchConnected(tile, this) { it: TileArray, other: TileArray ->
            it.northBorder() == other.southBorder() }
        val east = searchConnected(tile, this) { tileArray: TileArray, other: TileArray ->
            tileArray.eastBorder() == other.westBorder() }
        val south = searchConnected(tile, this) { tileArray: TileArray, other: TileArray ->
            tileArray.southBorder() == other.northBorder() }
        val west = searchConnected(tile, this) { tileArray: TileArray, other: TileArray ->
            tileArray.westBorder() == other.eastBorder() }
        TileConnection(tile, north, east, south, west)
    }
}

fun List<Tile>.removeById(id: Int) = filter { it.id != id }
fun List<Tile>.removeByIds(ids: List<Int>) = filter { it.id !in ids }

data class TileConnection(val tile: Tile,
                          val north: List<Pair<TileVariation, Int>>,
                          val east: List<Pair<TileVariation, Int>>,
                          val south: List<Pair<TileVariation, Int>>,
                          val west: List<Pair<TileVariation, Int>>,
) {
    fun isTopLeftCorner() = north.isEmpty() && east.size == 1 && south.size == 1 && west.isEmpty()
}

fun parseTiles(tilesString: String): List<Tile> =
    tilesString.split("\n\b*\n".toRegex())
        .filter { it.isNotBlank() }
        .map { tileString ->
            val tileStrings = tileString.split("\n")
            val headline = tileStrings.first()
            val id = parseTileHeadline(headline)
            val tileArray = tileStrings.drop(1).filter { it.isNotBlank() }.map { it.trim().toList() }
            Tile(id, tileArray)
        }

fun parseTileHeadline(headline: String): Int {
    val regex = """\w+ (\d+):""".toRegex()
    val match = regex.find(headline) ?: throw IllegalArgumentException("Can not parse input=$headline")
    if (match.groupValues.size != 2) throw IllegalArgumentException("Wrong number of elements parsed")
    return match.groupValues[1].toInt()
}

data class Tile(val id: Int, val array: TileArray)

typealias TileArray = List<List<Char>>
fun TileArray.northBorder(): List<Char> = first()
fun TileArray.eastBorder(): List<Char> = map { it.last()}
fun TileArray.southBorder(): List<Char> = last()
fun TileArray.westBorder(): List<Char> = map { it.first()}

fun Tile.inAllVariations() = allVariations.map {
    it to this.array.variation(it)
}

fun TileArray.variation(variation: TileVariation) = when(variation.flip) {
    is Original -> this
    is FlipX -> flipX()
    is FlipY -> flipY()
}.run {
    var h = this
    repeat(variation.turnRight.n) {
        h = h.turnRight()
    }
    h
}
val allVariations = listOf(Original, FlipX, FlipY).flatMap { flip ->
    (0..3).map { turnN ->
        TileVariation(flip, TurnRight(turnN))
    }
}


sealed class TileFlip
object Original : TileFlip()
object FlipX : TileFlip()
object FlipY : TileFlip()
data class TurnRight(val n: Int)

data class TileVariation(val flip: TileFlip, val turnRight: TurnRight)
fun TileArray.flipX() = reversed()
fun TileArray.flipY() = map { it.reversed() }

fun parseTile(tileString: String): List<List<Char>> =
    tileString.split("\n").filter { it.isNotBlank() }.map { it.trim().toList() }


class Day20_Part1 : FunSpec({
    context("tile operations") {
        context("flip x") {
            val tile = parseTile("""
            .#
            ..
            """.trimIndent())
            tile.flipX() shouldBe  parseTile("""
            ..
            .#
            """.trimIndent())
        }
        context("flip y") {
            val tile = parseTile("""
            .#
            ..
            """.trimIndent())
            tile.flipY() shouldBe  parseTile("""
            #.
            ..
            """.trimIndent())
        }
        context("turn right") {
            val tile = parseTile("""
            ##
            ..
            """.trimIndent())
            tile.turnRight() shouldBe  parseTile("""
            .#
            .#
            """.trimIndent())
        }
        context("all variations") {
            val tileArray = parseTile("""
            .##
            ...
            ...
            """.trimIndent())
            val allVariants = Tile(1, tileArray).inAllVariations()
            allVariants shouldBe setOf(
                TileVariation(Original, TurnRight(0)) to
                parseTile("""
                .##
                ...
                ...
                """.trimIndent()),
                TileVariation(Original, TurnRight(1)) to
                parseTile("""
                ...
                ..#
                ..#
                """.trimIndent()),
                TileVariation(Original, TurnRight(2)) to
                parseTile("""
                ...
                ...
                ##.
                """.trimIndent()),
                TileVariation(Original,  TurnRight(3)) to
                parseTile("""
                #..
                #..
                ...
                """.trimIndent()),
                TileVariation(FlipX,  TurnRight(0)) to
                parseTile("""
                ...
                ...
                .##
                """.trimIndent()),
                TileVariation(FlipX,  TurnRight(1)) to
                parseTile("""
                ...
                #..
                #..
                """.trimIndent()),
                TileVariation(FlipX,  TurnRight(2)) to
                parseTile("""
                ##.
                ...
                ...
                """.trimIndent()),
                TileVariation(FlipX,  TurnRight(3)) to
                parseTile("""
                ..#
                ..#
                ...
                """.trimIndent()),
                TileVariation(FlipY,  TurnRight(0)) to
                parseTile("""
                ##.
                ...
                ...
                """.trimIndent()),
                TileVariation(FlipY,  TurnRight(1)) to
                parseTile("""
                ..#
                ..#
                ...
                """.trimIndent()),
                TileVariation(FlipY,  TurnRight(2)) to
                parseTile("""
                ...
                ...
                .##
                """.trimIndent()),
                TileVariation(FlipY,  TurnRight(3)) to
                parseTile("""
                ...
                #..
                #..
                """.trimIndent()),
             )
        }
    }
    val tilesString = """
        Tile 2311:
        ..##.#..#.
        ##..#.....
        #...##..#.
        ####.#...#
        ##.##.###.
        ##...#.###
        .#.#.#..##
        ..#....#..
        ###...#.#.
        ..###..###

        Tile 1951:
        #.##...##.
        #.####...#
        .....#..##
        #...######
        .##.#....#
        .###.#####
        ###.##.##.
        .###....#.
        ..#.#..#.#
        #...##.#..

        Tile 1171:
        ####...##.
        #..##.#..#
        ##.#..#.#.
        .###.####.
        ..###.####
        .##....##.
        .#...####.
        #.##.####.
        ####..#...
        .....##...

        Tile 1427:
        ###.##.#..
        .#..#.##..
        .#.##.#..#
        #.#.#.##.#
        ....#...##
        ...##..##.
        ...#.#####
        .#.####.#.
        ..#..###.#
        ..##.#..#.

        Tile 1489:
        ##.#.#....
        ..##...#..
        .##..##...
        ..#...#...
        #####...#.
        #..#.#.#.#
        ...#.#.#..
        ##.#...##.
        ..##.##.##
        ###.##.#..

        Tile 2473:
        #....####.
        #..#.##...
        #.##..#...
        ######.#.#
        .#...#.#.#
        .#########
        .###.#..#.
        ########.#
        ##...##.#.
        ..###.#.#.

        Tile 2971:
        ..#.#....#
        #...###...
        #.#.###...
        ##.##..#..
        .#####..##
        .#..####.#
        #..#.#..#.
        ..####.###
        ..#.#.###.
        ...#.#.#.#

        Tile 2729:
        ...#.#.#.#
        ####.#....
        ..#.#.....
        ....#..#.#
        .##..##.#.
        .#.####...
        ####.#.#..
        ##.####...
        ##..#.##..
        #.##...##.

        Tile 3079:
        #.#.#####.
        .#..######
        ..#.......
        ######....
        ####.#..#.
        .#...#.##.
        #.#####.##
        ..#.###...
        ..#.......
        ..#.###...
        """.trimIndent()
    val tiles = parseTiles(tilesString)

    context("parse tiles") {
        test("should have parsed tiles") {
            tiles.size shouldBe 9
            tiles[7].id shouldBe 2729
            tiles[8].array.toPrintableString() shouldBe """
            #.#.#####.
            .#..######
            ..#.......
            ######....
            ####.#..#.
            .#...#.##.
            #.#####.##
            ..#.###...
            ..#.......
            ..#.###...
            """.trimIndent()
        }
    }
    context("find borders") {
        val tile = tiles[8]
        val northBorder = tile.array.northBorder()
        test("north border") {
            northBorder.joinToString("") shouldBe "#.#.#####."
        }
        val southBorder = tile.array.southBorder()
        test("south border") {
            southBorder.joinToString("") shouldBe "..#.###..."
        }
        val eastBorder = tile.array.eastBorder()
        test("east border") {
            eastBorder.joinToString("") shouldBe ".#....#..."
        }
        val westBorder = tile.array.westBorder()
        test("west border") {
            westBorder.joinToString("") shouldBe "#..##.#..."
        }
    }
    context("connect tiles") {
        val tileConnections = tiles.findConnections()
        val corner = tileConnections.first { it.isTopLeftCorner() }
        test("should have found corner") {
            corner.tile.id shouldBe 2971
        }
        context("eastern neighbors") {
            val easternNeighbor = (tiles - corner.tile).searchEasternNeighbor(corner.tile)
            test("should find eastern neighbor of corner") {
                easternNeighbor?.id shouldBe 1489
            }
            val completedRow = tiles.completeEasternNeighbor(corner.tile)
            test("should find all eastern neighbor of corner") {
                completedRow.map { it.id } shouldBe listOf(2971, 1489, 1171)
            }
        }
        context("southern neighbor") {
            val southernNeighbor = tiles.searchSouthernNeighbor(corner.tile)
            test("should find southern neighbor of corner") {
                southernNeighbor?.id shouldBe 2729
            }
        }
        context("southern neighbors") {
            val southernNeighbors = tiles.searchSouthernNeighbors(tiles.completeEasternNeighbor(corner.tile))
            test("should find southern neighbors of start line") {
                southernNeighbors.map { it.id } shouldBe listOf(2729, 1427, 2473)
            }
        }
        context("complete southern neighbors") {
            val solutionTiles = tiles.completeSouthernNeighbors(tiles.completeEasternNeighbor(corner.tile))
            test("should find all southern neighbors of start line") {
                solutionTiles.map { row ->
                    row.map { it.id }
                } shouldBe listOf(
                    listOf(2971, 1489, 1171),
                    listOf(2729, 1427, 2473),
                    listOf(1951, 2311, 3079),
                )
            }
        }
        context("all steps to find the solution") {
            val solutionTiles = findSolutionTiles(tiles)
            test("should find all southern neighbors of start line") {
                solutionTiles.map { row ->
                    row.map { it.id }
                } shouldBe listOf(
                    listOf(2971, 1489, 1171),
                    listOf(2729, 1427, 2473),
                    listOf(1951, 2311, 3079),
                )
            }
            val solution = solutionTiles.calculateCornerProduct()
            test("should calculate solution") {
                solution shouldBe 20899048083289L
            }
        }
    }
})

class Day20_Part1_Exercise: FunSpec({
    val input = readResource("day20Input.txt")!!
    val tiles = parseTiles(input)
    test("should have read 144 tiles") {
        tiles.size shouldBe 144
    }
    val solutionTiles = findSolutionTiles(tiles)
    println(solutionTiles.toIds())
    val solution = solutionTiles.calculateCornerProduct()
    test("should have found solution") {
        solution shouldBe 23386616781851L
    }
})
