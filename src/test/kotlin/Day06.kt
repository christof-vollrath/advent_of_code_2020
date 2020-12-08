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

--- Part Two ---

As you finish the last group's customs declaration, you notice that you misread one word in the instructions:

You don't need to identify the questions to which anyone answered "yes"; 
you need to identify the questions to which everyone answered "yes"!

Using the same example as above:

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

In the first group, everyone (all 1 person) answered "yes" to 3 questions: a, b, and c.
In the second group, there is no question to which everyone answered "yes".
In the third group, everyone answered yes to only 1 question, a. Since some people did not answer "yes" to b or c, they don't count.
In the fourth group, everyone answered yes to only 1 question, a.
In the fifth group, everyone (all 1 person) answered "yes" to 1 question, b.
In this example, the sum of these counts is 3 + 0 + 1 + 1 + 1 = 6.

For each group, count the number of questions to which everyone answered "yes". What is the sum of those counts?

 */


fun List<Set<Char>>.countYes(): Int = sumBy { it.size }

fun List<List<Set<Char>>>.anyPerGroup(): List<Set<Char>> = map { group ->
    group.reduce { result, current ->
        result.union(current)
    }
}

fun List<List<Set<Char>>>.allPerGroup(): List<Set<Char>> = map { group ->
    group.reduce { result, current ->
        result.intersect(current)
    }
}
fun parseAnswers(answers: String): List<List<Set<Char>>> =
    answers.split("""\n\s*\n""".toRegex())
        .map { groupString ->
            groupString.trim().split("\n")
                .map {
                    it.toCharArray().toSet()
                }
                .filter { it.isNotEmpty() }
        }

class Day06_Part1 : FunSpec({
    val answersString = """
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
        val answers = parseAnswers(answersString)
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
            parseAnswers(answersString).anyPerGroup() shouldBe listOf(
                setOf('a', 'b', 'c'),
                setOf('a', 'b', 'c'),
                setOf('a', 'b', 'c'),
                setOf('a'),
                setOf('b'),
            )
        }
        test("sum yes answers") {
            parseAnswers(answersString).anyPerGroup().countYes() shouldBe 11
        }
    }
})

class Day06_Part1_Exercise: FunSpec({
    val input = readResource("day06Input.txt")!!
    val count = parseAnswers(input).anyPerGroup().countYes()
    test("solution") {
        count shouldBe 6542
    }
})

class Day06_Part2 : FunSpec({
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

    context("yes answers of group are a now an intersection of answers of persons") {
        test("intersection of answers per group") {
            parseAnswers(answers).allPerGroup() shouldBe listOf(
                setOf('a', 'b', 'c'),
                setOf(),
                setOf('a'),
                setOf('a'),
                setOf('b'),
            )
        }
        test("sum yes answers") {
            parseAnswers(answers).allPerGroup().countYes() shouldBe 6
        }
    }
})

class Day06_Part2_Exercise: FunSpec({
    val input = readResource("day06Input.txt")!!
    val count = parseAnswers(input).allPerGroup().countYes()
    test("solution") {
        count shouldBe 3299
    }
})