import Coord2.Companion.turnMatrixLeft
import Coord2.Companion.turnMatrixRight
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/*
--- Day 12: Rain Risk ---

See https://adventofcode.com/2020/day/12


 */


data class Ship(var pos: Coord2, var direction: Direction = Direction.EAST) {
    fun move(moveDir: Direction, units: Int) = when(moveDir) {
        Direction.NORTH -> pos += Coord2(0, -units)
        Direction.EAST -> pos += Coord2(units, 0)
        Direction.SOUTH -> pos += Coord2(0, units)
        Direction.WEST -> pos += Coord2(-units, 0)
    }
    fun turnLeft(units: Int) = repeat(units/90) { direction = direction.turnLeft() }
    fun turnRight(units: Int) = repeat(units/90) { direction = direction.turnRight() }
    fun navigate(instructions: List<NavigationInstruction>) {
        instructions.forEach { instruction ->
            val units = instruction.units
            when(instruction) {
                is NavigateNorth -> move(Direction.NORTH, units)
                is NavigateEast -> move(Direction.EAST, units)
                is NavigateSouth -> move(Direction.SOUTH, units)
                is NavigateWest -> move(Direction.WEST, units)
                is NavigateLeft -> turnLeft(units)
                is NavigateRight -> turnRight(units)
                is NavigateForward -> move(direction, units)
            }
        }
    }
}

fun parseNavigationInstructions(navigationString: String): List<NavigationInstruction> =
    navigationString.split("\n")
        .map { line ->
            parseNavigationInstruction(line)
        }

fun parseNavigationInstruction(input: String): NavigationInstruction {
    val regex = """([NESWRLF])(\d+)""".toRegex()
    val match = regex.find(input) ?: throw IllegalArgumentException("Can not parse input=$input")
    if (match.groupValues.size != 3) throw IllegalArgumentException("Wrong number of elements parsed")
    val values = match.groupValues
    val units = values[2].toInt()
    val cmdChar = values[1].first()
    return when(cmdChar) {
        'N' -> NavigateNorth(units)
        'E' -> NavigateEast(units)
        'S' -> NavigateSouth(units)
        'W' -> NavigateWest(units)
        'F' -> NavigateForward(units)
        'R' -> NavigateRight(units)
        'L' -> NavigateLeft(units)
        else -> throw IllegalArgumentException("Unknown cmd=$cmdChar")
    }
}

sealed class NavigationInstruction {
    abstract val units: Int
}
data class NavigateNorth(override val units: Int) : NavigationInstruction()
data class NavigateEast(override val units: Int) : NavigationInstruction()
data class NavigateSouth(override val units: Int) : NavigationInstruction()
data class NavigateWest(override val units: Int) : NavigationInstruction()
data class NavigateForward(override val units: Int) : NavigationInstruction()
data class NavigateRight(override val units: Int) : NavigationInstruction()
data class NavigateLeft(override val units: Int) : NavigationInstruction()

data class ShipWithWaypoint(var pos: Coord2, var waypoint: Coord2) {
    fun moveWaypoint(moveDir: Direction, units: Int) = when(moveDir) {
        Direction.NORTH -> waypoint += Coord2(0, -units)
        Direction.EAST -> waypoint += Coord2(units, 0)
        Direction.SOUTH -> waypoint += Coord2(0, units)
        Direction.WEST -> waypoint += Coord2(-units, 0)
    }
    fun move(units: Int) {
        val diff = waypoint - pos
        val nDiff = diff * units
        pos += nDiff
        waypoint += nDiff // Waypoint moves with ship
    }
    fun turnLeft(units: Int) = repeat(units/90) {
        val diff = waypoint - pos
        val turned = diff * turnMatrixLeft
        waypoint = waypoint - diff + turned
    }
    fun turnRight(units: Int) = repeat(units/90) {
        val diff = waypoint - pos
        val turned = diff * turnMatrixRight
        waypoint = waypoint - diff + turned
    }
    fun navigate(instructions: List<NavigationInstruction>) {
        instructions.forEach { instruction ->
            val units = instruction.units
            when(instruction) {
                is NavigateNorth -> moveWaypoint(Direction.NORTH, units)
                is NavigateEast -> moveWaypoint(Direction.EAST, units)
                is NavigateSouth -> moveWaypoint(Direction.SOUTH, units)
                is NavigateWest -> moveWaypoint(Direction.WEST, units)
                is NavigateLeft -> turnLeft(units)
                is NavigateRight -> turnRight(units)
                is NavigateForward -> move(units)
            }
            println("pos=$pos waypoint=$waypoint instruction=$instruction")
        }
    }
}

enum class Direction {
    NORTH {
        override fun turnLeft() = WEST
        override fun turnRight() = EAST
    },
    EAST {
        override fun turnLeft() = NORTH
        override fun turnRight() = SOUTH
    },
    SOUTH {
        override fun turnLeft() = EAST
        override fun turnRight() = WEST
    },
    WEST {
        override fun turnLeft() = SOUTH
        override fun turnRight() = NORTH
    };

    abstract fun turnLeft(): Direction
    abstract fun turnRight(): Direction
}

class Day12_Part1 : FunSpec({
    val navigationString = """
    F10
    N3
    F7
    R90
    F11
    """.trimIndent()
    context("parse navigation instructions") {
        val instructions = parseNavigationInstructions(navigationString)
        test("should have parsed 5 instructions") {
            instructions.size shouldBe 5
        }
        test("should have parsed the right instructions") {
            instructions shouldBe listOf(
                NavigateForward(10),
                NavigateNorth(3),
                NavigateForward(7),
                NavigateRight(90),
                NavigateForward(11),
            )
        }
        context("move ship") {
            val start = Coord2(0, 0)
            val ship = Ship(start)
            ship.navigate(instructions)
            val manhattanDistance = ship.pos.manhattanDistance(start)
            test("ship should have moved to the right position") {
                manhattanDistance shouldBe 25
            }
        }
    }
})


class Day12_Part1_Exercise: FunSpec({
    val input = readResource("day12Input.txt")!!
    val instructions = parseNavigationInstructions(input)
    val start = Coord2(0, 0)
    val ship = Ship(start)
    ship.navigate(instructions)
    val manhattanDistance = ship.pos.manhattanDistance(start)
    test("ship should have moved to the right position") {
        manhattanDistance shouldBe 938
    }
})

class Day12_Part2 : FunSpec({
    context("turn vector by matrix") {
        context("turn right by 90") {

            val coord = Coord2(1, 0) // ->
            coord * turnMatrixRight shouldBe Coord2(0, 1)
        }
        context("turn left by 90") {
            val coord = Coord2(1, 0) // ->
            coord * turnMatrixLeft shouldBe Coord2(0, -1)
        }
    }
    val instructions = parseNavigationInstructions("""
    F10
    N3
    F7
    R90
    F11
    """.trimIndent())
    context("move ship with waypoint") {
        val start = Coord2(0, 0)
        val waypoint = Coord2(10, -1)
        val ship = ShipWithWaypoint(start, waypoint)
        ship.navigate(instructions)
        val manhattanDistance = ship.pos.manhattanDistance(start)
        test("ship should have moved to the right position") {
            manhattanDistance shouldBe 286
        }
    }
})

class Day12_Part2_Exercise: FunSpec({
    val input = readResource("day12Input.txt")!!
    val instructions = parseNavigationInstructions(input)
    val start = Coord2(0, 0)
    val waypoint = Coord2(10, -1)
    val ship = ShipWithWaypoint(start, waypoint)
    ship.navigate(instructions)
    val manhattanDistance = ship.pos.manhattanDistance(start)
    test("ship should have moved to the right position") {
        manhattanDistance shouldBe 54404
    }
})
