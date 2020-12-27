import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.math.sqrt

/*
--- Day 20: Jurassic Jigsaw ---

See https://adventofcode.com/2020/day/20

 */

fun findSolutionTiles(tiles: List<Tile>): List<List<Tile>> {
    val expectedSizeOfSolution = sqrt(tiles.size.toFloat()).toInt()
    val tileConnections = tiles.findConnections()
    val cornerCandidates = tileConnections.filter { it.isTopLeftCorner() } // Corners don't need to be turned because the whole solution would just be turned
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
    // This does not check if the tiles match from east to west which they should according to the description
    // Interestingly the solution is nevertheless found, matching from north to south seems to be unique
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
            println("eastern neighbors for ${currentTile.id}=${nextCandidates.map { it.id }}")
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

fun parseTileArray(tileString: String): TileArray =
    tileString.split("\n").filter { it.isNotBlank() }.map { it.toList() }


fun TileArray.countNonMonsters(): Int = map { row ->
    row.count { it == '#'}
}.sum()

fun TileArray.findMonstersInAnyVariation(monster: TileArray): TileArray = allVariations.map {
    this.variation(it)
}.map { tileArrayVariation ->
    tileArrayVariation.findMonsters(monster)
}.first { tileArrayWithMarkedMonsters ->
    tileArrayWithMarkedMonsters.any { row ->
        row.any { it == 'O'}
    }
}

fun TileArray.findMonsters(monster: TileArray): TileArray {
    fun findMonsterAt(x: Int, y: Int, monster: TileArray): Boolean {
        for (monsterY in monster.indices)
            for (monsterX in monster.first().indices) {
                val monsterC = monster[monsterY][monsterX]
                if (monsterC == '#' && this.getOrNull(y + monsterY)?.getOrNull(x + monsterX) != '#')
                    return false // found mismatch
            }
        return true
    }
    fun markMonsterAt(mutableTile: MutableList<MutableList<Char>>, x: Int, y: Int, monster: TileArray) {
        for (monsterY in monster.indices)
            for (monsterX in monster.first().indices) {
                val monsterC = monster[monsterY][monsterX]
                if (monsterC == '#') {
                    val setX = x + monsterX
                    val setY = y + monsterY
                    if (setY in mutableTile.indices && setX in mutableTile.first().indices)
                        mutableTile[setY][setX] = 'O'
                }
            }
    }
    val mutableTile = this.map { row ->
        row.toMutableList()
    }.toMutableList()
    for (y in mutableTile.indices)
        for (x in mutableTile.first().indices) {
            val monsterFound = findMonsterAt(x, y, monster)
            if (monsterFound) markMonsterAt(mutableTile, x, y, monster)
        }
    return mutableTile
}

fun combineTiles(tiles: List<List<Tile>>): TileArray =
    tiles.flatMap { rowOfTile ->
        val firstTile = rowOfTile.first()
        firstTile.array.indices.map { y ->
            val charRow = rowOfTile.indices.map { tileIndex -> rowOfTile[tileIndex].array[y] }
            val combinedLine = charRow.reduce { acc, tile ->  acc + tile }
            combinedLine
        }
    }

fun List<List<Tile>>.removeGaps() =
    map { rowOfTiles ->
        rowOfTiles.map { tile ->
            val droppedArray = tile.array.drop(1).dropLast(1).map { row ->
                row.drop(1).dropLast(1)
            }
            Tile(tile.id, droppedArray)
        }
    }

val exampleTilesString = """
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
val exampleTiles = parseTiles(exampleTilesString)

class Day20_Part1 : FunSpec({
    context("tile operations") {
        context("flip x") {
            val tile = parseTileArray("""
            .#
            ..
            """.trimIndent())
            tile.flipX() shouldBe  parseTileArray("""
            ..
            .#
            """.trimIndent())
        }
        context("flip y") {
            val tile = parseTileArray("""
            .#
            ..
            """.trimIndent())
            tile.flipY() shouldBe  parseTileArray("""
            #.
            ..
            """.trimIndent())
        }
        context("turn right") {
            val tile = parseTileArray("""
            ##
            ..
            """.trimIndent())
            tile.turnRight() shouldBe  parseTileArray("""
            .#
            .#
            """.trimIndent())
        }
        context("all variations") {
            val tileArray = parseTileArray("""
            .##
            ...
            ...
            """.trimIndent())
            val allVariants = Tile(1, tileArray).inAllVariations()
            allVariants shouldBe setOf(
                TileVariation(Original, TurnRight(0)) to
                parseTileArray("""
                .##
                ...
                ...
                """.trimIndent()),
                TileVariation(Original, TurnRight(1)) to
                parseTileArray("""
                ...
                ..#
                ..#
                """.trimIndent()),
                TileVariation(Original, TurnRight(2)) to
                parseTileArray("""
                ...
                ...
                ##.
                """.trimIndent()),
                TileVariation(Original,  TurnRight(3)) to
                parseTileArray("""
                #..
                #..
                ...
                """.trimIndent()),
                TileVariation(FlipX,  TurnRight(0)) to
                parseTileArray("""
                ...
                ...
                .##
                """.trimIndent()),
                TileVariation(FlipX,  TurnRight(1)) to
                parseTileArray("""
                ...
                #..
                #..
                """.trimIndent()),
                TileVariation(FlipX,  TurnRight(2)) to
                parseTileArray("""
                ##.
                ...
                ...
                """.trimIndent()),
                TileVariation(FlipX,  TurnRight(3)) to
                parseTileArray("""
                ..#
                ..#
                ...
                """.trimIndent()),
                TileVariation(FlipY,  TurnRight(0)) to
                parseTileArray("""
                ##.
                ...
                ...
                """.trimIndent()),
                TileVariation(FlipY,  TurnRight(1)) to
                parseTileArray("""
                ..#
                ..#
                ...
                """.trimIndent()),
                TileVariation(FlipY,  TurnRight(2)) to
                parseTileArray("""
                ...
                ...
                .##
                """.trimIndent()),
                TileVariation(FlipY,  TurnRight(3)) to
                parseTileArray("""
                ...
                #..
                #..
                """.trimIndent()),
             )
        }
    }

    context("parse tiles") {
        test("should have parsed tiles") {
            exampleTiles.size shouldBe 9
            exampleTiles[7].id shouldBe 2729
            exampleTiles[8].array.toPrintableString() shouldBe """
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
        val tile = exampleTiles[8]
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
        val tileConnections = exampleTiles.findConnections()
        val corner = tileConnections.first { it.isTopLeftCorner() }
        test("should have found corner") {
            corner.tile.id shouldBe 2971
        }
        context("eastern neighbors") {
            val easternNeighbor = (exampleTiles - corner.tile).searchEasternNeighbor(corner.tile)
            test("should find eastern neighbor of corner") {
                easternNeighbor?.id shouldBe 1489
            }
            val completedRow = exampleTiles.completeEasternNeighbor(corner.tile)
            test("should find all eastern neighbor of corner") {
                completedRow.map { it.id } shouldBe listOf(2971, 1489, 1171)
            }
        }
        context("southern neighbor") {
            val southernNeighbor = exampleTiles.searchSouthernNeighbor(corner.tile)
            test("should find southern neighbor of corner") {
                southernNeighbor?.id shouldBe 2729
            }
        }
        context("southern neighbors") {
            val southernNeighbors = exampleTiles.searchSouthernNeighbors(exampleTiles.completeEasternNeighbor(corner.tile))
            test("should find southern neighbors of start line") {
                southernNeighbors.map { it.id } shouldBe listOf(2729, 1427, 2473)
            }
        }
        context("complete southern neighbors") {
            val solutionTiles = exampleTiles.completeSouthernNeighbors(exampleTiles.completeEasternNeighbor(corner.tile))
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
            val solutionTiles = findSolutionTiles(exampleTiles)
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

val monster = parseTileArray("""
                                      # 
                    #    ##    ##    ###
                     #  #  #  #  #  #   
                    """.trimIndent())

class Day20_Part2: FunSpec({
    context("search sea monsters") {
        context("remove gaps") {
            val solutionTiles = findSolutionTiles(exampleTiles)
            val tilesWithoutGaps = solutionTiles.removeGaps()
            test("gaps should be removed") {
                tilesWithoutGaps[0][0].array.toPrintableString() shouldBe """
                ...###..
                .#.###..
                #.##..#.
                #####..#
                #..####.
                ..#.#..#
                .####.##
                .#.#.###
                """.trimIndent()
            }
            context("combine tiles") {
                val image = combineTiles(tilesWithoutGaps)
                test("should combined image") { // Flip needed because example has image flipped compared to my solution
                    image.flipX().toPrintableString() shouldBe """
                    .#.#..#.##...#.##..#####
                    ###....#.#....#..#......
                    ##.##.###.#.#..######...
                    ###.#####...#.#####.#..#
                    ##.#....#.##.####...#.##
                    ...########.#....#####.#
                    ....#..#...##..#.#.###..
                    .####...#..#.....#......
                    #..#.##..#..###.#.##....
                    #.####..#.####.#.#.###..
                    ###.#.#...#.######.#..##
                    #.####....##..########.#
                    ##..##.#...#...#.#.#.#..
                    ...#..#..#.#.##..###.###
                    .#.#....#.##.#...###.##.
                    ###.#...#..#.##.######..
                    .#.#.###.##.##.#..#.##..
                    .####.###.#...###.#..#.#
                    ..#.#..#..#.#.#.####.###
                    #..####...#.#.#.###.###.
                    #####..#####...###....##
                    #.##..#..#...#..####...#
                    .#.###..##..##..####.##.
                    ...###...##...#...#..###
                    """.trimIndent()
                }
                context("find monster") {
                    val withMarkedMonsters = image.findMonstersInAnyVariation(monster)
                    test("monsters should be marked") {
                        withMarkedMonsters.toPrintableString() shouldBe """
                        .####...#####..#...###..
                        #####..#..#.#.####..#.#.
                        .#.#...#.###...#.##.O#..
                        #.O.##.OO#.#.OO.##.OOO##
                        ..#O.#O#.O##O..O.#O##.##
                        ...#.#..##.##...#..#..##
                        #.##.#..#.#..#..##.#.#..
                        .###.##.....#...###.#...
                        #.####.#.#....##.#..#.#.
                        ##...#..#....#..#...####
                        ..#.##...###..#.#####..#
                        ....#.##.#.#####....#...
                        ..##.##.###.....#.##..#.
                        #...#...###..####....##.
                        .#.##...#.##.#.#.###...#
                        #.###.#..####...##..#...
                        #.###...#.##...#.##O###.
                        .O##.#OO.###OO##..OOO##.
                        ..O#.O..O..O.#O##O##.###
                        #.#..##.########..#..##.
                        #.#####..#.#...##..#....
                        #....##..#.#########..##
                        #...#.....#..##...###.##
                        #..###....##.#...##.##.#
                        """.trimIndent()
                    }
                    test("should count non monsters") {
                        val solution = withMarkedMonsters.countNonMonsters()
                        solution shouldBe 273
                    }
                }
            }
        }
    }
})

class Day20_Part2_Exercise: FunSpec({
    val input = readResource("day20Input.txt")!!
    val tiles = parseTiles(input)
    val solutionTiles = findSolutionTiles(tiles)
    val tilesWithoutGaps = solutionTiles.removeGaps()
    val image = combineTiles(tilesWithoutGaps)
    println(image.toPrintableString())
    val withMarkedMonsters = image.findMonstersInAnyVariation(monster)
    println(withMarkedMonsters.toPrintableString())
    val solution = withMarkedMonsters.countNonMonsters()
    test("should have found the correct number on non monsters") {
        solution shouldBe 2376
    }
})
