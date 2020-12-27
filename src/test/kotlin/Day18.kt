import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import java.io.PushbackReader
import java.io.StringReader
import java.lang.IllegalStateException

/*
--- Day 18: Operation Order ---

See https://adventofcode.com/2020/day/18


*/

fun String.sumExpressions() = split("\n").map { line -> parseExpression(line).evaluate() }.sum()
fun String.sumExpressions2() = split("\n").map { line -> parseExpression(line).evaluate2() }.sum()

fun List<ExpressionToken>.evaluate(): Long {
    val resultStack = ArrayDeque<Long>()
    val operatorStack = ArrayDeque<Operator>()
    fun execute(n: Long): Long =
        when (operatorStack.removeLastOrNull()) {
            null -> n
            Operator.PLUS -> n + resultStack.removeLast()
            Operator.MULTIPLY -> n * resultStack.removeLast()
            Operator.OPENING_PARENTHESES -> n
            Operator.CLOSING_PARENTHESES -> throw IllegalStateException("Closing parentheses should be handled earlier")
        }
    forEach { token ->
        when(token) {
            is NumberToken -> resultStack.add(execute(token.n))
            is OperatorToken -> if (token.operator == Operator.CLOSING_PARENTHESES) resultStack.add(execute(resultStack.removeLast()))
            else operatorStack.add(token.operator)
        }
    }
    if (resultStack.size != 1) throw IllegalStateException("Result stack should contain one value instead $resultStack")
    return resultStack.first()
}

sealed class ExpressionToken
data class NumberToken(val n: Long): ExpressionToken()
data class OperatorToken(val operator: Operator): ExpressionToken()
enum class Operator { PLUS, MULTIPLY, OPENING_PARENTHESES, CLOSING_PARENTHESES }

fun parseExpression(expressionString: String): List<ExpressionToken> {
    val inputStream = PushbackReader(StringReader(expressionString))
    fun readNumber(start: Char): Long =
        sequence {
            yield(start)
            while(true) {
                val r = inputStream.read()
                if (r == -1) break
                val c = r.toChar()
                if (c.isDigit()) yield(c)
                else {
                    inputStream.unread(r)
                    break
                }
            }
        }.joinToString("").toLong()

    return sequence {
        while(true) {
            val r = inputStream.read()
            if (r == -1) break
            val c = r.toChar()
            when {
                c == '+' -> yield(OperatorToken(Operator.PLUS))
                c == '*' -> yield(OperatorToken(Operator.MULTIPLY))
                c == '(' -> yield(OperatorToken(Operator.OPENING_PARENTHESES))
                c == ')' -> yield(OperatorToken(Operator.CLOSING_PARENTHESES))
                c.isDigit() -> yield(NumberToken(readNumber(c)))
                c.isWhitespace() -> {}
            }
        }
    }.toList()
}

fun List<ExpressionToken>.evaluate2(): Long {
    val resultStack = ArrayDeque<Long>()
    val operatorStack = ArrayDeque<Operator>()
    fun execute(n: Long): Long =
        when (operatorStack.removeLastOrNull()) {
            null -> n
            Operator.PLUS -> n + resultStack.removeLast()
            Operator.MULTIPLY -> n * resultStack.removeLast()
            Operator.OPENING_PARENTHESES -> { operatorStack.add(Operator.OPENING_PARENTHESES); n }
            Operator.CLOSING_PARENTHESES -> throw IllegalStateException("Closing parentheses should be handled earlier")
        }
    forEach { token ->
        when(token) {
            is NumberToken -> if (operatorStack.lastOrNull() == Operator.MULTIPLY) resultStack.add(token.n)
            else resultStack.add(execute(token.n))
            is OperatorToken -> when(token.operator) {
                Operator.CLOSING_PARENTHESES -> {
                    while(operatorStack.isNotEmpty() && operatorStack.last() != Operator.OPENING_PARENTHESES)
                        resultStack.add(execute(resultStack.removeLast()))
                    if (operatorStack.isNotEmpty()) operatorStack.removeLast() // opening parentheses
                    if (operatorStack.isNotEmpty() && operatorStack.lastOrNull() != Operator.MULTIPLY) resultStack.add(execute(resultStack.removeLast()))
                }
                else -> operatorStack.add(token.operator)
            }
        }
    }
    while(operatorStack.isNotEmpty()) {
        resultStack.add(execute(resultStack.removeLast()))
    }
    if (resultStack.size != 1) throw IllegalStateException("Result stack should contain one value instead $resultStack")
    return resultStack.first()
}

class Day18_Part1 : FunSpec({
    context("parse expression") {
        context("simple expression") {
            val expressionString = "1 + 2 * 3"
            val expression = parseExpression(expressionString)
            test("should have parsed expression") {
                expression shouldBe listOf(
                    NumberToken(1),
                    OperatorToken(Operator.PLUS),
                    NumberToken(2),
                    OperatorToken(Operator.MULTIPLY),
                    NumberToken(3)
                )
            }
            test("should evaluate expression") {
                val result = expression.evaluate()
                result shouldBe 9
            }
        }
        context("expression with parentheses") {
            val expressionString = "1 + (2 * 3)"
            val expression = parseExpression(expressionString)
            test("should have parsed expression") {
                expression shouldBe listOf(
                    NumberToken(1),
                    OperatorToken(Operator.PLUS),
                    OperatorToken(Operator.OPENING_PARENTHESES),
                    NumberToken(2),
                    OperatorToken(Operator.MULTIPLY),
                    NumberToken(3),
                    OperatorToken(Operator.CLOSING_PARENTHESES),
                )
            }
            test("should evaluate expression") {
                val result = expression.evaluate()
                result shouldBe 7
            }
        }
        context("more examples") {
            table(
                headers("expression", "expected"),
                row("1 + 2 * 3 + 4 * 5 + 6", 71),
                row("1 + (2 * 3) + (4 * (5 + 6))", 51),
                row("2 * 3 + (4 * 5)", 26),
                row("5 + (8 * 3 + 9 + 3 * 4 * 3)", 437),
                row("5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))", 12240),
                row("((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2", 13632),
            ).forAll { expressionString, expected ->
                val result = parseExpression(expressionString).evaluate()
                result shouldBe expected
            }
        }
        context("sum examples") {
            val expressionsString = """
            2 * 3 + (4 * 5)
            5 + (8 * 3 + 9 + 3 * 4 * 3)
            5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))
            ((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2
            """.trimIndent()
            val result = expressionsString.sumExpressions()
            test("should calculate correct sum of expression lines") {
                result shouldBe 26335
            }
        }
    }
})

class Day18_Part1_Exercise: FunSpec({
    val input = readResource("day18Input.txt")!!
    val solution = input.sumExpressions()
    test("should have calculated solution") {
        solution shouldBe 31142189909908
    }
})

class Day18_Part2 : FunSpec({
    context("evaluate expression 2") {
        context("simple expression") {
            val expressionString = "2 * 3 + 4"
            val expression = parseExpression(expressionString)
            test("should evaluate expression") {
                val result = expression.evaluate2()
                result shouldBe 14
            }
        }
        context("expression with parentheses") {
            val expressionString = "(2 * 3) + 4"
            val expression = parseExpression(expressionString)
            test("should evaluate expression") {
                val result = expression.evaluate2()
                result shouldBe 10
            }
        }
        context("more examples") {
            table(
                headers("expression", "expected"),
                row("1 + 2 * 3 + 4 * 5 + 6", 231),
                row("1 + (2 * 3) + (4 * (5 + 6))", 51),
                row("2 * 3 + (4 * 5)", 46),
                row("5 + (8 * 3 + 9 + 3 * 4 * 3)", 1445),
                row("5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))", 669060),
                row("((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2", 23340),
                row("1+(2*4+5)*2", 38),
            ).forAll { expressionString, expected ->
                val result = parseExpression(expressionString).evaluate2()
                result shouldBe expected
            }
        }
    }
})

class Day18_Part2_Exercise: FunSpec({
    val input = readResource("day18Input.txt")!!
    val solution = input.sumExpressions2()
    test("should have calculated solution") {
        solution shouldBeLessThan 384615893831695
        solution shouldBe 323912478287549L
    }
})
