import io.kotest.core.datatest.forAll
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/*
--- Day 2: Password Philosophy ---

See https://adventofcode.com/2020/day/2

 */

fun String.validatePasswordInput(): Boolean {
    val (policy, password) = parsePolicyAndPassword(this)
    val count = password.count { it == policy.c }
    return count in (policy.atLeast .. policy.atMost)
}

fun String.validatePasswordInput2(): Boolean {
    val (policy, password) = parsePolicyAndPassword(this)
    val count = listOf(password[policy.pos1 - 1], password[policy.pos2 - 1]).count { it == policy.c }
    return count == 1
}

fun parsePolicyAndPassword(input: String): Pair<PasswordPolicy, String> {
    val regex = """(\d+)-(\d+) ([a-z]): ([a-z]*)""".toRegex()
    val match = regex.find(input) ?: throw IllegalArgumentException("Can not parse input")
    if (match.groupValues.size != 5) throw IllegalArgumentException("Not all elements parsed")
    val values = match.groupValues
    return Pair(PasswordPolicy(values[1].toInt(), values[2].toInt(), values[3].first()), values[4])

}

data class PasswordPolicy(val atLeast: Int, val atMost: Int, val c: Char)  {
    val pos1: Int // Aliases for part 2
        get() = atLeast
    val pos2: Int
        get() = atMost
}

class Day02_ParseInput : FunSpec({
    val policyAndPassword = parsePolicyAndPassword("1-3 a: abcde")

    test("should be parsed correctly") {
        policyAndPassword.second shouldBe "abcde"
        policyAndPassword.first shouldBe PasswordPolicy(atLeast = 1, atMost = 3, c = 'a')
    }
})

class Day02_CheckInput : FunSpec({
    data class CountTestCase(val s: String, val c: Char, val expected: Int)
    context("count chars") {
        forAll(
            CountTestCase("abcde", 'a', 1),
            CountTestCase("cdefg", 'b', 0),
            CountTestCase("ccccccccc", 'c', 9),
        ) { (s, c, expected) ->
            s.count { it == c } shouldBe expected
        }
    }
    data class ValidateTestCase(val s: String, val expected: Boolean)
    context("validate passwords") {
        forAll(
            ValidateTestCase("1-3 a: abcde", true),
            ValidateTestCase("1-3 b: cdefg", false),
            ValidateTestCase("2-9 c: ccccccccc", true),
        ) { (s, expected) ->
            s.validatePasswordInput() shouldBe expected
        }
    }
})

class Day02_Part1: FunSpec({
    val inputStrings = readResource("day02Input.txt")!!.split("\n")
    val count = inputStrings.count { it.validatePasswordInput() }
    test("solution") {
        count shouldBe 410
    }
})

class Day02_CheckInput2 : FunSpec({
    data class CountTestCase(val s: String, val expected: Int)
    context("count chars") {
        forAll(
            CountTestCase("1-3 a: abcde", 1),
            CountTestCase("1-3 b: cdefg", 0),
            CountTestCase("2-9 c: ccccccccc", 2),
        ) { (s, expected) ->
            val (policy, password) = parsePolicyAndPassword(s)
            listOf(password[policy.pos1 - 1], password[policy.pos2 - 1]).count { it == policy.c } shouldBe expected
        }
    }
    data class ValidateTestCase(val s: String, val expected: Boolean)
    context("validate passwords") {
        forAll(
            ValidateTestCase("1-3 a: abcde", true),
            ValidateTestCase("1-3 b: cdefg", false),
            ValidateTestCase("2-9 c: ccccccccc", false),
        ) { (s, expected) ->
            s.validatePasswordInput2() shouldBe expected
        }
    }
})

class Day02_Part2: FunSpec({
    val inputStrings = readResource("day02Input.txt")!!.split("\n")
    val count = inputStrings.count { it.validatePasswordInput2() }
    test("solution") {
        count shouldBe 694
    }
})
