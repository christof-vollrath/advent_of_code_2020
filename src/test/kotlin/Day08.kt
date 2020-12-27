import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/*
--- Day 8: Handheld Halting ---

See https://adventofcode.com/2020/day/8


 */

class ConsoleCpu(val program: List<ConsoleCommand>) {
    var accu = 0
    var pc = 0
    fun execute() {
        val executed = mutableSetOf<Int>()
        while(pc < program.size) {
            if (pc in executed) throw java.lang.IllegalArgumentException("Loop detected at $pc")
            executed += pc
            program[pc].execute(this)
        }
    }
}

fun parseProgram(progString: String): List<ConsoleCommand> {
    val programLines = progString.split("\n")
    return programLines.map { line ->
        val regex = """(\w+) ([+-]\d+)""".toRegex()
        val match = regex.find(line) ?: throw IllegalArgumentException("Can not parse input $line")
        if (match.groupValues.size != 3) throw IllegalArgumentException("Not all elements parsed")
        val cmdString = match.groupValues[1]
        val arg = match.groupValues[2].toInt()
        when(cmdString) {
            "nop" -> Nop(arg)
            "acc" -> Acc(arg)
            "jmp" -> Jmp(arg)
            else -> throw java.lang.IllegalArgumentException("Unkown cmd $cmdString")
        }
    }
}

interface ConsoleCommand {
    fun execute(cpu: ConsoleCpu)
}


data class Nop(val arg: Int) : ConsoleCommand {
    override fun execute(cpu: ConsoleCpu) {
        cpu.pc++
    }
}
data class Acc(val arg: Int) : ConsoleCommand {
    override fun execute(cpu: ConsoleCpu) {
        cpu.accu += arg
        cpu.pc++
    }
}
data class Jmp(val arg: Int) : ConsoleCommand {
    override fun execute(cpu: ConsoleCpu) {
        cpu.pc += arg
    }
}

fun List<ConsoleCommand>.correct(): List<ConsoleCommand> {
    for (i in 0 until size) {
        val cmd = get(i)
        if (cmd is Nop || cmd is Jmp) {
            val changedProgram = toMutableList()
            changedProgram[i] =
                when(cmd)  {
                    is Nop -> Jmp(cmd.arg)
                    is Jmp -> Nop(cmd.arg)
                    else -> throw InternalError("Must be nop or jmp")
                }
            try {
                ConsoleCpu(changedProgram).execute()
                return changedProgram
            } catch (e: IllegalArgumentException) { } // Error, try next change
        }
    }
    throw java.lang.IllegalArgumentException("No correction found")
}

class Day08_Part1 : FunSpec({
    val progString = """
        nop +0
        acc +1
        jmp +4
        acc +3
        jmp -3
        acc -99
        acc +1
        jmp -4
        acc +6
    """.trimIndent()
    context("parse program") {
        test("program parsed correctly") {
            val program = parseProgram(progString)
            program[0] shouldBe Nop(0)
            program[1] shouldBe Acc(1)
            program[4] shouldBe Jmp(-3)
        }
    }
    context("execute program") {
        val console = ConsoleCpu(parseProgram(progString))
        try {
            console.execute()
            fail("Loop should be detected")
        } catch(e: java.lang.IllegalArgumentException) {}
        console.accu shouldBe 5
    }
})

class Day08_Part1_Exercise: FunSpec({
    val input = readResource("day08Input.txt")!!
    val console = ConsoleCpu(parseProgram(input))
    try {
        console.execute()
        fail("Loop should be detected")
    } catch(e: java.lang.IllegalArgumentException) {}
    test("solution") {
        console.accu shouldBe 1859
    }
})

class Day08_Part2 : FunSpec({
    val progString = """
        nop +0
        acc +1
        jmp +4
        acc +3
        jmp -3
        acc -99
        acc +1
        jmp -4
        acc +6
    """.trimIndent()
    context("find correction") {
        val program = parseProgram(progString)
        val correctedProgram = program.correct()
        val console = ConsoleCpu(correctedProgram)
        console.execute()
        console.accu shouldBe 8
    }
})

class Day08_Part2_Exercise: FunSpec({
    val input = readResource("day08Input.txt")!!
    val program = parseProgram(input)
    val correctedProgram = program.correct()
    val console = ConsoleCpu(correctedProgram)
    console.execute()
    console.accu shouldBe 1235
})
