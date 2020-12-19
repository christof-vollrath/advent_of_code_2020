import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.lang.IllegalArgumentException

/*
--- Day 19: Monster Messages ---

You land in an airport surrounded by dense forest.
As you walk to your high-speed train, the Elves at the Mythical Information Bureau contact you again.
They think their satellite has collected an image of a sea monster! Unfortunately,
the connection to the satellite is having problems,
and many of the messages sent back from the satellite have been corrupted.

They sent you a list of the rules valid messages should obey
and a list of received messages they've collected so far (your puzzle input).

The rules for valid messages (the top part of your puzzle input) are numbered and build upon each other.
For example:

0: 1 2
1: "a"
2: 1 3 | 3 1
3: "b"

Some rules, like 3: "b", simply match a single character (in this case, b).

The remaining rules list the sub-rules that must be followed;
for example, the rule 0: 1 2 means that to match rule 0, the text being checked must match rule 1,
and the text after the part that matched rule 1 must then match rule 2.

Some of the rules have multiple lists of sub-rules separated by a pipe (|).
This means that at least one list of sub-rules must match.
(The ones that match might be different each time the rule is encountered.)
For example, the rule 2: 1 3 | 3 1 means that to match rule 2,
the text being checked must match rule 1 followed by rule 3 or it must match rule 3 followed by rule 1.

Fortunately, there are no loops in the rules, so the list of possible matches will be finite.
Since rule 1 matches a and rule 3 matches b, rule 2 matches either ab or ba.
Therefore, rule 0 matches aab or aba.

Here's a more interesting example:

0: 4 1 5
1: 2 3 | 3 2
2: 4 4 | 5 5
3: 4 5 | 5 4
4: "a"
5: "b"

Here, because rule 4 matches a and rule 5 matches b, rule 2 matches two letters that are the same (aa or bb),
and rule 3 matches two letters that are different (ab or ba).

Since rule 1 matches rules 2 and 3 once each in either order, it must match two pairs of letters,
one pair with matching letters and one pair with different letters.
This leaves eight possibilities: aaab, aaba, bbab, bbba, abaa, abbb, baaa, or babb.

Rule 0, therefore, matches a (rule 4), then any of the eight options from rule 1, then b (rule 5):
aaaabb, aaabab, abbabb, abbbab, aabaab, aabbbb, abaaab, or ababbb.

The received messages (the bottom part of your puzzle input) need to be checked against the rules
so you can determine which are valid and which are corrupted.
Including the rules and the messages together, this might look like:

0: 4 1 5
1: 2 3 | 3 2
2: 4 4 | 5 5
3: 4 5 | 5 4
4: "a"
5: "b"

ababbb
bababa
abbbab
aaabbb
aaaabbb

Your goal is to determine the number of messages that completely match rule 0.
In the above example, ababbb and abbbab match, but bababa, aaabbb, and aaaabbb do not, producing the answer 2.
The whole message must match all of rule 0; there can't be extra unmatched characters in the message.
(For example, aaaabbb might appear to match rule 0 above, but it has an extra unmatched b on the end.)

How many messages completely match rule 0?

 */


fun List<String>.filterMatchingRule(rules: Map<Int, MessageRule>): List<String> {
    val allValidMessages = generateValidMessages(0, rules)
    return filter { it in allValidMessages }
}

fun parseRulesAndMessages(input: String): Pair<Map<Int, MessageRule>, List<String>> {
    val parts = input.split("\n\n")
    if (parts.size != 2) throw IllegalArgumentException("Input should have parts rules and messages")
    val rules = parseMessageRules(parts[0])
    val messages = parts[1].split("\n").map { it.trim() }
    return rules to messages

}

fun generateValidMessages(ruleId: Int, rules: Map<Int, MessageRule>): Set<String> {
    return when (val rule = rules[ruleId]) {
        is MessageCharRule -> setOf(rule.c.toString())
        is MessageAlternativesRule -> {
            val alternatives = rule.alternatives
            alternatives.flatMap { parts ->
                val partAlternatives = parts.map { alternativeRuleId ->
                    generateValidMessages(alternativeRuleId, rules)
                }
                combine(partAlternatives).map { it.joinToString("") }
            }.toSet()
        }
        else -> throw IllegalArgumentException("Unexpected rule $rule")
    }
}

fun parseMessageRules(rulesString: String): Map<Int, MessageRule> =
    rulesString.split("\n")
        .map {
            val rule = parseMessageRule(it)
            rule.id to rule
        }.toMap()

fun parseMessageRule(ruleString: String): MessageRule {
    val idAndRule = ruleString.split(":").map{ it.trim() }
    val id = idAndRule[0].toInt()
    return if (idAndRule[1].startsWith('"'))
        MessageCharRule(id, idAndRule[1][1])
    else {
        val ruleParts = idAndRule[1].split("|").map{ it.trim() }
        val alternatives = ruleParts.map { rulePart ->
            rulePart.split("""\s+""".toRegex()).map { it.toInt() }
        }
        MessageAlternativesRule(id, alternatives)
    }
}

sealed class MessageRule {
    abstract val id: Int
}
data class MessageAlternativesRule(override val id: Int, val alternatives: List<List<Int>>) : MessageRule()
data class MessageCharRule(override val id: Int, val c: Char) : MessageRule()

class Day19_Part1 : FunSpec({
    context("parse message rules") {
        context("simple rule") {
            val ruleString = "10: 4 1 5"
            val rule = parseMessageRule(ruleString)
            test("should have parsed rule") {
                rule shouldBe MessageAlternativesRule(10, listOf(listOf(4, 1, 5)))
            }
        }
        context("rule with alternatves") {
            val ruleString = "1: 2 3 | 3 2"
            val rule = parseMessageRule(ruleString)
            test("should have parsed rule") {
                rule shouldBe MessageAlternativesRule(1, listOf(listOf(2, 3), listOf(3, 2)))
            }
        }
        context("rule with single char") {
            val ruleString = """4: "a""""
            val rule = parseMessageRule(ruleString)
            test("should have parsed rule") {
                rule shouldBe MessageCharRule(4, 'a')
            }
        }
        context("rules") {
            val rulesString = """
                0: 4 1 5
                1: 2 3 | 3 2
                2: 4 4 | 5 5
                3: 4 5 | 5 4
                4: "a"
                5: "b"
            """.trimIndent()
            val rules = parseMessageRules(rulesString)
            test("should have parsed all rules") {
                rules.size shouldBe 6
                rules[1] shouldBe MessageAlternativesRule(1, listOf(listOf(2, 3), listOf(3, 2)))
                rules[5] shouldBe MessageCharRule(5, 'b')
            }
        }
    }
    context("combine lists") {
        combine(listOf(listOf(1, 2), listOf(3, 4))) shouldBe listOf(listOf(1, 3), listOf(1, 4), listOf(2, 3),listOf(2, 4))
    }
    context("generate all valid messages") {
        val rules = parseMessageRules("""
        0: 4 1 5
        1: 2 3 | 3 2
        2: 4 4 | 5 5
        3: 4 5 | 5 4
        4: "a"
        5: "b"    
        """.trimIndent())
        val validMessages = generateValidMessages(0, rules)
        test("all valid messages should be generated") {
            validMessages shouldBe setOf("aaaabb", "aaabab", "abbabb", "abbbab", "aabaab", "aabbbb", "abaaab", "ababbb")
        }
    }
    context("parse rules and messages") {
        val input = """
        0: 4 1 5
        1: 2 3 | 3 2
        2: 4 4 | 5 5
        3: 4 5 | 5 4
        4: "a"
        5: "b"

        ababbb
        bababa
        abbbab
        aaabbb
        aaaabbb
        """.trimIndent()
        val (rules, messages) = parseRulesAndMessages(input)
        test("should have parsed rules and messages") {
            rules.size shouldBe 6
            rules[4] shouldBe MessageCharRule(4, 'a')
            messages.size shouldBe 5
            messages[3] shouldBe "aaabbb"
        }
        context("filter messages") {
            val matchingMessages = messages.filterMatchingRule(rules)
            test("only the two messages ababbb and abbbab should match") {
                matchingMessages.toSet() shouldBe setOf("ababbb", "abbbab")
            }
        }
    }
})

class Day19_Part1_Exercise: FunSpec({
    val input = readResource("day19Input.txt")!!
    val (rules, messages) = parseRulesAndMessages(input)
    val matchingMessages = messages.filterMatchingRule(rules)
    val solution = matchingMessages.size
    test("should have found solution") {
       solution shouldBe 233
    }
})
