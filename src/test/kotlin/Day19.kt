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

--- Part Two ---

As you look over the list of messages, you realize your matching rules aren't quite right.
To fix them, completely replace rules 8: 42 and 11: 42 31 with the following:

8: 42 | 42 8
11: 42 31 | 42 11 31

This small change has a big impact: now, the rules do contain loops,
and the list of messages they could hypothetically match is infinite.
You'll need to determine how these changes affect which messages are valid.

Fortunately, many of the rules are unaffected by this change;
it might help to start by looking at which rules always match the same set of values and how those rules
(especially rules 42 and 31) are used by the new versions of rules 8 and 11.

(Remember, you only need to handle the rules you have; building a solution that could handle
any hypothetical combination of rules would be significantly more difficult.)

For example:

42: 9 14 | 10 1
9: 14 27 | 1 26
10: 23 14 | 28 1
1: "a"
11: 42 31
5: 1 14 | 15 1
19: 14 1 | 14 14
12: 24 14 | 19 1
16: 15 1 | 14 14
31: 14 17 | 1 13
6: 14 14 | 1 14
2: 1 24 | 14 4
0: 8 11
13: 14 3 | 1 12
15: 1 | 14
17: 14 2 | 1 7
23: 25 1 | 22 14
28: 16 1
4: 1 1
20: 14 14 | 1 15
3: 5 14 | 16 1
27: 1 6 | 14 18
14: "b"
21: 14 1 | 1 14
25: 1 1 | 1 14
22: 14 14
8: 42
26: 14 22 | 1 20
18: 15 15
7: 14 5 | 1 21
24: 14 1

abbbbbabbbaaaababbaabbbbabababbbabbbbbbabaaaa
bbabbbbaabaabba
babbbbaabbbbbabbbbbbaabaaabaaa
aaabbbbbbaaaabaababaabababbabaaabbababababaaa
bbbbbbbaaaabbbbaaabbabaaa
bbbababbbbaaaaaaaabbababaaababaabab
ababaaaaaabaaab
ababaaaaabbbaba
baabbaaaabbaaaababbaababb
abbbbabbbbaaaababbbbbbaaaababb
aaaaabbaabaaaaababaa
aaaabbaaaabbaaa
aaaabbaabbaaaaaaabbbabbbaaabbaabaaa
babaaabbbaaabaababbaabababaaab
aabbbbbaabbbaaaaaabbbbbababaaaaabbaaabba

Without updating rules 8 and 11, these rules only match three messages: bbabbbbaabaabba, ababaaaaaabaaab,
and ababaaaaabbbaba.

However, after updating rules 8 and 11, a total of 12 messages match:

bbabbbbaabaabba
babbbbaabbbbbabbbbbbaabaaabaaa
aaabbbbbbaaaabaababaabababbabaaabbababababaaa
bbbbbbbaaaabbbbaaabbabaaa
bbbababbbbaaaaaaaabbababaaababaabab
ababaaaaaabaaab
ababaaaaabbbaba
baabbaaaabbaaaababbaababb
abbbbabbbbaaaababbbbbbaaaababb
aaaaabbaabaaaaababaa
aaaabbaabbaaaaaaabbbabbbaaabbaabaaa
aabbbbbaabbbaaaaaabbbbbababaaaaabbaaabba

After updating rules 8 and 11, how many messages completely match rule 0?

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

fun List<String>.filterStringsCustomRules(rules: Map<Int, MessageRule>): List<String> {
    val generated42 = generateValidMessages(42, rules)
    val generated31 = generateValidMessages(31, rules)
    val generatedLength = generated42.first().length
    generated42.forEach {
        if (it.length != generatedLength) throw IllegalArgumentException("Assuming that all string generated by rule 42 have the same size")
    }
    generated31.forEach {
        if (it.length != generatedLength) throw IllegalArgumentException("Assuming that all string generated by rule 31 have the same size as rule 42")
    }
    return filter { str ->
        checkCustomRule(str, generated42, generated31)
    }
}

fun checkCustomRule(str: String, generated42: Set<String>, generated31: Set<String>): Boolean {
    val generatedLength = generated42.first().length
    val chunkedString = str.chunked(generatedLength) // Using the fact that all elements in generated42, generated31 have the same size
    // remove matching rule 31 from back
    val without31fromLast = chunkedString.dropLastWhile { it in generated31 }
    val matching31Count = chunkedString.size - without31fromLast.size
    if (matching31Count == 0) return false // because of rule 31 at least one chunk must match 31
    if (! without31fromLast.all { it in generated42}) return false // because of modified rules 8, 11 the start must contain string fulfilling rule 42
    if (without31fromLast.size <= matching31Count) return false // because of modified rule 8 there must be more rule-42-strings than rule 31-strings in the back
    return true
}

class Day19_Part1 : FunSpec({
    context("parse message rules") {
        context("simple rule") {
            val ruleString = "10: 4 1 5"
            val rule = parseMessageRule(ruleString)
            test("should have parsed rule") {
                rule shouldBe MessageAlternativesRule(10, listOf(listOf(4, 1, 5)))
            }
        }
        context("rule with alternatives") {
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

class Day19_Part2 : FunSpec({
    val input = """
        42: 9 14 | 10 1
        9: 14 27 | 1 26
        10: 23 14 | 28 1
        1: "a"
        11: 42 31
        5: 1 14 | 15 1
        19: 14 1 | 14 14
        12: 24 14 | 19 1
        16: 15 1 | 14 14
        31: 14 17 | 1 13
        6: 14 14 | 1 14
        2: 1 24 | 14 4
        0: 8 11
        13: 14 3 | 1 12
        15: 1 | 14
        17: 14 2 | 1 7
        23: 25 1 | 22 14
        28: 16 1
        4: 1 1
        20: 14 14 | 1 15
        3: 5 14 | 16 1
        27: 1 6 | 14 18
        14: "b"
        21: 14 1 | 1 14
        25: 1 1 | 1 14
        22: 14 14
        8: 42
        26: 14 22 | 1 20
        18: 15 15
        7: 14 5 | 1 21
        24: 14 1
        
        abbbbbabbbaaaababbaabbbbabababbbabbbbbbabaaaa
        bbabbbbaabaabba
        babbbbaabbbbbabbbbbbaabaaabaaa
        aaabbbbbbaaaabaababaabababbabaaabbababababaaa
        bbbbbbbaaaabbbbaaabbabaaa
        bbbababbbbaaaaaaaabbababaaababaabab
        ababaaaaaabaaab
        ababaaaaabbbaba
        baabbaaaabbaaaababbaababb
        abbbbabbbbaaaababbbbbbaaaababb
        aaaaabbaabaaaaababaa
        aaaabbaaaabbaaa
        aaaabbaabbaaaaaaabbbabbbaaabbaabaaa
        babaaabbbaaabaababbaabababaaab
        aabbbbbaabbbaaaaaabbbbbababaaaaabbaaabba
        """.trimIndent()
    val (rules, messages) = parseRulesAndMessages(input)
    context("make sure that unmodified rules act like described") {
        val matchingMessages = messages.filterMatchingRule(rules)
        test("only the three messages should match") {
            matchingMessages.toSet() shouldBe setOf("bbabbbbaabaabba", "ababaaaaaabaaab", "ababaaaaabbbaba")
        }
    }
    context("what is rule 42 producing") {
        val generated42 = generateValidMessages(42, rules)
        println(generated42.sorted())
        generated42.size shouldBe 16
     }
    context("what is rule 31 producing") {
        val generated31 = generateValidMessages(31, rules)
        println(generated31.sorted())
        generated31.size shouldBe 16
    }
    context("split string in chunks") {
        "bbabbbbaabaabb".chunked(2) shouldBe listOf(
            "bb", "ab", "bb", "ba", "ab", "aa", "bb"
        )
    }
    context("a valid message should pass the modified rules") {
        val validMessages = listOf("aaabbbbbbaaaabaababaabababbabaaabbababababaaa")
        val matchingMessages = validMessages.filterStringsCustomRules(rules)
        test("this message should match") {
            matchingMessages.toSet() shouldBe setOf(
                "aaabbbbbbaaaabaababaabababbabaaabbababababaaa",
            )
        }
    }
    context("use a custom filter for part 2") {
        val matchingMessages = messages.filterStringsCustomRules(rules)
        test("only some messages should match") {
            matchingMessages.toSet() shouldBe setOf(
                "bbabbbbaabaabba",
                "babbbbaabbbbbabbbbbbaabaaabaaa",
                "aaabbbbbbaaaabaababaabababbabaaabbababababaaa",
                "bbbbbbbaaaabbbbaaabbabaaa",
                "bbbababbbbaaaaaaaabbababaaababaabab",
                "ababaaaaaabaaab",
                "ababaaaaabbbaba",
                "baabbaaaabbaaaababbaababb",
                "abbbbabbbbaaaababbbbbbaaaababb",
                "aaaaabbaabaaaaababaa",
                "aaaabbaabbaaaaaaabbbabbbaaabbaabaaa",
                "aabbbbbaabbbaaaaaabbbbbababaaaaabbaaabba",
            )
        }
    }
})

class Day19_Part2_Exercise: FunSpec({
    val input = readResource("day19Input.txt")!!
    val (rules, messages) = parseRulesAndMessages(input)
    val matchingMessages = messages.filterStringsCustomRules(rules)
    val solution = matchingMessages.size
    test("should have found solution") {
        solution shouldBe 396
    }
})
