import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/*
--- Day 12: Rain Risk ---

Your ferry made decent progress toward the island, but the storm came in faster than anyone expected.
The ferry needs to take evasive actions!

Unfortunately, the ship's navigation computer seems to be malfunctioning;
rather than giving a route directly to safety, it produced extremely circuitous instructions.
When the captain uses the PA system to ask if anyone can help, you quickly volunteer.

The navigation instructions (your puzzle input) consists of a sequence of single-character actions
paired with integer input values.
After staring at them for a few minutes, you work out what they probably mean:

Action N means to move north by the given value.
Action S means to move south by the given value.
Action E means to move east by the given value.
Action W means to move west by the given value.
Action L means to turn left the given number of degrees.
Action R means to turn right the given number of degrees.
Action F means to move forward by the given value in the direction the ship is currently facing.

The ship starts by facing east. Only the L and R actions change the direction the ship is facing.
(That is, if the ship is facing east and the next instruction is N10,
the ship would move north 10 units, but would still move east if the following action were F.)

For example:

F10
N3
F7
R90
F11

These instructions would be handled as follows:

F10 would move the ship 10 units east (because the ship starts by facing east) to east 10, north 0.
N3 would move the ship 3 units north to east 10, north 3.
F7 would move the ship another 7 units east (because the ship is still facing east) to east 17, north 3.
R90 would cause the ship to turn right by 90 degrees and face south; it remains at east 17, north 3.
F11 would move the ship 11 units south to east 17, south 8.

At the end of these instructions, the ship's Manhattan distance
(sum of the absolute values of its east/west position and its north/south position)
from its starting position is 17 + 8 = 25.

Figure out where the navigation instructions lead.
What is the Manhattan distance between that location and the ship's starting position?

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
            println("pos=$pos direction=$direction instruction=$instruction")
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
        else -> throw IllegalArgumentException("Unknow cmd=$cmdChar")
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
