import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import java.lang.IllegalStateException

/*

--- Day 14: Docking Data ---

See https://adventofcode.com/2020/day/14


 */

fun List<String>.executeMaskMem(): Map<Int, Long> {
    val result = mutableMapOf<Int, Long>()
    var currentMask: Pair<Long, Long>? = null
    map { line ->
        when {
            line.startsWith("mask") -> currentMask = parseMaskLine(line)
            line.startsWith("mem") -> {
                val (index, value) = parseMemLine(line)
                result[index] = value.applyMaskValue(currentMask ?: throw IllegalStateException("mask not yet set in line=$line"))
            }
        }
    }
    return result
}

fun parseMaskLine(line: String) = parseMaskValue(line.split(" = ")[1])

fun parseMemLine(input: String): Pair<Int, Long> {
    val regex = """mem\[(\d+)] = (\d+)""".toRegex()
    val match = regex.find(input) ?: throw IllegalArgumentException("Can not parse input=$input")
    if (match.groupValues.size != 3) throw IllegalArgumentException("Wrong number of elements parsed")
    val index = match.groupValues[1].toInt()
    val value = match.groupValues[2].toLong()
    return index to value
}

fun Long.applyMaskValue(maskValue: Pair<Long, Long>): Long =
    (this and maskValue.first) or maskValue.second

fun parseMaskValue(maskValueString: String): Pair<Long, Long> {
    val maskString = maskValueString.map {
        when(it) {
            'X' -> '1'
            else -> '0'
        }
    }.joinToString("")
    val mask = maskString.toLong(2)
    val valueString = maskValueString.map {
        when(it) {
            '1' -> '1'
            else -> '0'
        }
    }.joinToString("")
    val value = valueString.toLong(2)
    return (mask to value)
}

fun String.floatBits(): List<String> {
    if (isEmpty()) return listOf("")
    val startChar = first()
    val tail = drop(1)
    return if (startChar == 'X') {
        tail.floatBits().map { "0$it" } +
                tail.floatBits().map { "1$it" }
    } else {
        tail.floatBits().map { "$startChar$it" }
    }
}

fun String.applyMask(mask: String) = zip(mask).map { (char, maskChar) ->
    when (maskChar) {
        'X' -> 'X'
        '1' -> '1'
        else -> char
    }
}.joinToString("")

fun List<String>.executeMaskMem2(): Map<Long, Long> {
    val result = mutableMapOf<Long, Long>()
    var currentMask: String? = null
    map { line ->
        when {
            line.startsWith("mask") -> currentMask = parseMaskLine2(line)
            line.startsWith("mem") -> {
                val (index, value) = parseMemLine(line)
                val indexes = index.toAddressString().applyMask(currentMask ?: throw IllegalStateException("mask not yet set in line=$line"))
                    .floatBits().map { it.toLong(2) }
                for(i in indexes) {
                    result[i] = value
                }
            }
        }
    }
    return result
}

fun parseMaskLine2(line: String) = line.split(" = ")[1]

fun Int.toAddressString(): String = toString(2).padStart(36, '0')

class Day14_Part1 : FunSpec({
    context("parse mask value") {
        val maskValueString = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXX1XXXX0X"
        val (bitMask, bitValue) = parseMaskValue(maskValueString)
        test("should have parsed value") {
            bitValue shouldBe 64L
        }
        test("should have parsed mask") {
            bitMask shouldBeGreaterThan 0
            (bitMask and bitValue) shouldBe 0
        }
    }
    context("parse mask line") {
        val maskLine = "mask = 0X01"
        val (bitMask, bitValue) = parseMaskLine(maskLine)
        test("should have parsed mask line") {
            bitMask shouldBe 4L
            bitValue shouldBe 1L
        }
    }
    context("parse mem line") {
        val memLine = "mem[80] = 11"
        val (index, value) = parseMemLine(memLine)
        test("should have parsed mask line") {
            index shouldBe 80
            value shouldBe 11
        }
    }
    context("example calculations") {
        val maskValue = parseMaskValue("XXXXXXXXXXXXXXXXXXXXXXXXXXXXX1XXXX0X")
        context("example 1") {
            val value = 11L
            value.applyMaskValue(maskValue) shouldBe 73L
        }
        context("example 2") {
            val value = 101L
            value.applyMaskValue(maskValue) shouldBe 101L
        }
        context("example 3") {
            val value = 0L
            value.applyMaskValue(maskValue) shouldBe 64L
        }
    }
    context("example") {
        val input = """
        mask = XXXXXXXXXXXXXXXXXXXXXXXXXXXXX1XXXX0X
        mem[8] = 11
        mem[7] = 101
        mem[8] = 0
        """.trimIndent()
        val inputLines = input.split("\n")
        val resultMap = inputLines.executeMaskMem()
        val result = resultMap.values.sum()
        test("result of example") {
            result shouldBe 165
        }
    }
})

class Day14_Part1_Exercise: FunSpec({
    val input = readResource("day14Input.txt")!!
    val inputLines = input.split("\n")
    val resultMap = inputLines.executeMaskMem()
    val solution = resultMap.values.sum()
    test("should have found solution") {
        solution shouldBe 13556564111697L
    }
})

class Day14_Part2 : FunSpec({
    context("address to string") {
        42.toAddressString() shouldBe "000000000000000000000000000000101010"
    }
    context("apply mask") {
        42.toAddressString().applyMask("000000000000000000000000000000X1001X") shouldBe
                "000000000000000000000000000000X1101X"
    }
    context("float bits") {
        "00000000000000000000000000000001X0XX".floatBits() shouldBe listOf(
                "000000000000000000000000000000010000",
                "000000000000000000000000000000010001",
                "000000000000000000000000000000010010",
                "000000000000000000000000000000010011",
                "000000000000000000000000000000011000",
                "000000000000000000000000000000011001",
                "000000000000000000000000000000011010",
                "000000000000000000000000000000011011"
        )
    }
    context("example") {
        val input = """
        mask = 000000000000000000000000000000X1001X
        mem[42] = 100
        mask = 00000000000000000000000000000000X0XX
        mem[26] = 1
        """.trimIndent()
        val inputLines = input.split("\n")
        val resultMap = inputLines.executeMaskMem2()
        val result = resultMap.values.sum()
        test("result of example") {
            result shouldBe 208L
        }
    }
})

class Day14_Part2_Exercise: FunSpec({
    val input = readResource("day14Input.txt")!!
    val inputLines = input.split("\n")
    val resultMap = inputLines.executeMaskMem2()
    val solution = resultMap.values.sum()
    test("should have found solution") {
        solution shouldBe 4173715962894L
    }
})
