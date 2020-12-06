import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/*
--- Day 6: Custom Customs ---

As your flight approaches the regional airport where you'll switch to a much larger plane,
customs declaration forms are distributed to the passengers.

The form asks a series of 26 yes-or-no questions marked a through z.
All you need to do is identify the questions for which anyone in your group answers "yes".
Since your group is just you, this doesn't take very long.

However, the person sitting next to you seems to be experiencing a language barrier and asks if you can help.
For each of the people in their group, you write down the questions for which they answer "yes", one per line.

For example:

abcx
abcy
abcz

In this group, there are 6 questions to which anyone answered "yes": a, b, c, x, y, and z.
(Duplicate answers to the same question don't count extra; each question counts at most once.)

Another group asks for your help, then another, and eventually you've collected answers from every group on the plane
(your puzzle input).
Each group's answers are separated by a blank line, and within each group, each person's answers are on a single line.

For example:

abc

a
b
c

ab
ac

a
a
a
a

b

This list represents answers from five groups:

The first group contains one person who answered "yes" to 3 questions: a, b, and c.
The second group contains three people; combined, they answered "yes" to 3 questions: a, b, and c.
The third group contains two people; combined, they answered "yes" to 3 questions: a, b, and c.
The fourth group contains four people; combined, they answered "yes" to only 1 question, a.
The last group contains one person who answered "yes" to only 1 question, b.
In this example, the sum of these counts is 3 + 3 + 3 + 1 + 1 = 11.

For each group, count the number of questions to which anyone answered "yes".
What is the sum of those counts?

 */


fun List<Set<Char>>.countYes(): Int = sumBy { it.size }

fun List<List<Set<Char>>>.perGroup(): List<Set<Char>> = map { group ->
    group.fold(emptySet()) { result, current ->
        result.union(current)
    }
}

fun parseAnswers(answers: String): List<List<Set<Char>>> =
    answers.split("""\n\s*\n""".toRegex())
        .map { groupString ->
            groupString.trim().split("\n")
                .map {
                    val line = it.trim()
                    it.toCharArray().toSet()
                }
                .filter { it.isNotEmpty() }
        }

class Day06_Part1 : FunSpec({
    val answers = """
        abc
        
        a
        b
        c
        
        ab
        ac
        
        a
        a
        a
        a
        
        b       
    """.trimIndent()

    context("parse answers") {
        val answers = parseAnswers(answers)
        test("numer of groups") {
            answers.size shouldBe 5
        }
        test("number of persons in group") {
            answers.map { it.size } shouldBe listOf(1, 3, 2, 4, 1)
        }
        test("yes answers per person with random samples") {
            answers[0][0] shouldBe setOf('a', 'b', 'c')
            answers[2][1] shouldBe setOf('a', 'c')
            answers[4] shouldBe listOf(setOf('b'))
        }
    }
    context("yes answers of group are a union of answers of persons") {
        test("union of answers per group") {
            parseAnswers(answers).perGroup() shouldBe listOf(
                setOf('a', 'b', 'c'),
                setOf('a', 'b', 'c'),
                setOf('a', 'b', 'c'),
                setOf('a'),
                setOf('b'),
            )
        }
        test("sum yes answers") {
            parseAnswers(answers).perGroup().countYes() shouldBe 11
        }
    }
})

class Day06_Part1_Excercise: FunSpec({
    val input = readResource("day06Input.txt")!!
    val count = parseAnswers(input).perGroup().countYes()
    test("solution") {
        count shouldBe 6542
    }
})
