import io.kotest.core.datatest.forAll
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/*
--- Day 2: Password Philosophy ---

Your flight departs in a few days from the coastal airport; the easiest way down to the coast from here is via toboggan.

The shopkeeper at the North Pole Toboggan Rental Shop is having a bad day.
"Something's wrong with our computers; we can't log in!" You ask if you can take a look.

Their password database seems to be a little corrupted:
some of the passwords wouldn't have been allowed by the Official Toboggan Corporate Policy
that was in effect when they were chosen.

To try to debug the problem, they have created a list (your puzzle input) of passwords
(according to the corrupted database) and the corporate policy when that password was set.

For example, suppose you have the following list:

1-3 a: abcde
1-3 b: cdefg
2-9 c: ccccccccc

Each line gives the password policy and then the password.
The password policy indicates the lowest and highest number of times a given letter must appear
for the password to be valid.
For example, 1-3 a means that the password must contain a at least 1 time and at most 3 times.

In the above example, 2 passwords are valid.
The middle password, cdefg, is not; it contains no instances of b, but needs at least 1.
The first and third passwords are valid: they contain one a or nine c,
both within the limits of their respective policies.

How many passwords are valid according to their policies?

 */

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
    val policyAndPassword = parsePolicyAndPassword("1-3 a: abcde")

    test("should be parsed correctly") {
        policyAndPassword.second shouldBe "abcde"
        policyAndPassword.first shouldBe PasswordPolicy(atLeast = 1, atMost = 3, c = 'a')
    }
})

class Day02_Part1: FunSpec({
    val inputStrings = readResource("day02Input.txt")!!.split("\n")
    val count = inputStrings.count { it.validatePasswordInput() }
    test("solution") {
        count shouldBe 410
    }
})

private fun String.validatePasswordInput(): Boolean {
    val (policy, password) = parsePolicyAndPassword(this)
    val count = password.count { it == policy.c }
    return count in (policy.atLeast .. policy.atMost)

}

fun parsePolicyAndPassword(input: String): Pair<PasswordPolicy, String> {
    val  regex = """(\d+)-(\d+) ([a-z]): ([a-z]*)""".toRegex()
    val match = regex.find(input) ?: throw IllegalArgumentException("Can not parse input")
    if (match.groupValues.size != 5) throw IllegalArgumentException("Not all elements parsed")
    val values = match.groupValues
    return Pair(PasswordPolicy(values[1].toInt(), values[2].toInt(), values[3].first()), values[4])

}

data class PasswordPolicy(val atLeast: Int, val atMost: Int, val c: Char)
