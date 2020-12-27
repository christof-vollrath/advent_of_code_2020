import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/*
--- Day 6: Custom Customs ---

See https://adventofcode.com/2020/day/6

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