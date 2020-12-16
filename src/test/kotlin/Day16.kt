import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/*
--- Day 16: Ticket Translation ---

As you're walking to yet another connecting flight, you realize that one of the legs of your re-routed trip coming up
is on a high-speed train. However, the train ticket you were given is in a language you don't understand.
You should probably figure out what it says before you get to the train station after the next flight.

Unfortunately, you can't actually read the words on the ticket.
You can, however, read the numbers, and so you figure out the fields these tickets must have and
the valid ranges for values in those fields.

You collect the rules for ticket fields, the numbers on your ticket,
and the numbers on other nearby tickets for the same train service (via the airport security cameras)
together into a single document you can reference (your puzzle input).

The rules for ticket fields specify a list of fields that exist somewhere on the ticket
and the valid ranges of values for each field.
For example, a rule like class: 1-3 or 5-7 means that one of the fields in every ticket is named class
and can be any value in the ranges 1-3 or 5-7 (inclusive, such that 3 and 5 are both valid in this field, but 4 is not).

Each ticket is represented by a single line of comma-separated values.
The values are the numbers on the ticket in the order they appear; every ticket has the same format.
For example, consider this ticket:

.--------------------------------------------------------.
| ????: 101    ?????: 102   ??????????: 103     ???: 104 |
|                                                        |
| ??: 301  ??: 302             ???????: 303      ??????? |
| ??: 401  ??: 402           ???? ????: 403    ????????? |
'--------------------------------------------------------'

Here, ? represents text in a language you don't understand.
This ticket might be represented as 101,102,103,104,301,302,303,401,402,403;
of course, the actual train tickets you're looking at are much more complicated.
In any case, you've extracted just the numbers in such a way that the first number is always the same specific field,
the second number is always a different specific field, and so on
- you just don't know what each position actually means!

Start by determining which tickets are completely invalid;
these are tickets that contain values which aren't valid for any field. Ignore your ticket for now.

For example, suppose you have the following notes:

class: 1-3 or 5-7
row: 6-11 or 33-44
seat: 13-40 or 45-50

your ticket:
7,1,14

nearby tickets:
7,3,47
40,4,50
55,2,20
38,6,12

It doesn't matter which position corresponds to which field; you can identify invalid nearby tickets
by considering only whether tickets contain values that are not valid for any field.
In this example, the values on the first nearby ticket are all valid for at least one field.
This is not true of the other three nearby tickets: the values 4, 55, and 12 are are not valid for any field.
Adding together all of the invalid values produces your ticket scanning error rate: 4 + 55 + 12 = 71.

Consider the validity of the nearby tickets you scanned. What is your ticket scanning error rate?

--- Part Two ---

Now that you've identified which tickets contain invalid values, discard those tickets entirely.
Use the remaining valid tickets to determine which field is which.

Using the valid ranges for each field, determine what order the fields appear on the tickets.
The order is consistent between all tickets: if seat is the third field, it is the third field on every ticket,
including your ticket.

For example, suppose you have the following notes:

class: 0-1 or 4-19
row: 0-5 or 8-19
seat: 0-13 or 16-19

your ticket:
11,12,13

nearby tickets:
3,9,18
15,1,5
5,14,9

Based on the nearby tickets in the above example, the first position must be row, the second position must be class,
and the third position must be seat; you can conclude that in your ticket, class is 12, row is 11, and seat is 13.

Once you work out which field is which, look for the six fields on your ticket that start with the word departure.
What do you get if you multiply those six values together?

--- Part Two ---

Now that you've identified which tickets contain invalid values, discard those tickets entirely.
Use the remaining valid tickets to determine which field is which.

Using the valid ranges for each field, determine what order the fields appear on the tickets.
The order is consistent between all tickets: if seat is the third field, it is the third field on every ticket,
including your ticket.

For example, suppose you have the following notes:

class: 0-1 or 4-19
row: 0-5 or 8-19
seat: 0-13 or 16-19

your ticket:
11,12,13

nearby tickets:
3,9,18
15,1,5
5,14,9

Based on the nearby tickets in the above example, the first position must be row,
the second position must be class, and the third position must be seat;
you can conclude that in your ticket, class is 12, row is 11, and seat is 13.

Once you work out which field is which, look for the six fields on your ticket that start with the word departure.
What do you get if you multiply those six values together?


 */

fun  List<List<Int>>.filterTicketsSatisfyingAnyRule(rules: List<TicketRule>): List<List<Int>> =
    filter { ticket ->
        ticket.all { value ->
            rules.any { rule ->
                rule.ranges.any { range ->
                    value in range
                }
            }
        }
    }

fun  List<List<Int>>.filterValuesNotSatisfyingAllRules(rules: List<TicketRule>): List<List<Int>> =
    map { ticket ->
        ticket.filterValueInTicketNotSatisfyingAllRules(rules)
    }

fun List<Int>.filterValueInTicketNotSatisfyingAllRules(ticketRules: List<TicketRule>)  =
    filter { value ->
        val ranges = ticketRules.flatMap { it.ranges }
        ranges.all { range ->
            value !in range
        }
    }


fun parseTrainTicketNotes(inputString: String): TrainTicketNotes {
    val lines = inputString.split("\n")
    val ruleStrings = lines.takeWhile { ! it.startsWith("your ticket") }
    val rules = ruleStrings.filter{ it.isNotEmpty() }.map { parseTicketRule(it)}
    val myTicketString = lines.dropWhile { ! it.startsWith("your ticket")}.drop(1).first()
    val myTicket = parseTicket(myTicketString)
    val nearbyTicketsString = lines.dropWhile { ! it.startsWith("nearby ticket")}.drop(1)
    val nearbyTickets = nearbyTicketsString.filter{ it.isNotEmpty() }.map { parseTicket(it) }
    return TrainTicketNotes(rules, myTicket, nearbyTickets)
}

data class TrainTicketNotes(val rules: List<TicketRule>, val yourTicket: List<Int>, val nearbyTickets: List<List<Int>>)

fun parseTicket(ticketString: String): List<Int> = ticketString.split(",").map {
    it.trim().toInt()
}

fun parseTicketRule(ticketRuleString: String): TicketRule {
    val regex = """([a-z0-9 ]+): (\d+)-(\d+) or (\d+)-(\d+)""".toRegex()
    val match = regex.find(ticketRuleString) ?: throw IllegalArgumentException("Can not parse input=$ticketRuleString")
    if (match.groupValues.size != 6) throw IllegalArgumentException("Wrong number of elements parsed")
    val name = match.groupValues[1]
    val rules = sequence {
        for (i in 2..5 step 2) {
            val from = match.groupValues[i].toInt()
            val to = match.groupValues[i+1].toInt()
            yield(from..to)
        }
    }.toList()
    return TicketRule(name, rules)
}

data class TicketRule(val name: String, val ranges: List<IntRange>)

fun findRuleForRows(yourTicket: List<Int>, tickets: List<List<Int>>, rules: List<TicketRule>): Map<String, Int> {
    val ruleForColumnMap = mutableMapOf<TicketRule, Int>()
    while (ruleForColumnMap.size < yourTicket.size) {
        for (columnIndex in yourTicket.indices) {
            val rulesForColumn = findRuleForColumn(tickets, rules, columnIndex)
            val filteredRulesForColumn = rulesForColumn - ruleForColumnMap.keys // Ignore already found rules
            if (filteredRulesForColumn.size == 1) { // Found a unique rule
                val rule = filteredRulesForColumn.first()
                ruleForColumnMap[rule] = columnIndex
            }
        }
    }
    return ruleForColumnMap.entries.map { (key, value) -> key.name to yourTicket[value] }.toMap()
}

fun findRuleForColumn(tickets: List<List<Int>>, rules: List<TicketRule>, columnIndex: Int): List<TicketRule> {
    val column = tickets.map { it[columnIndex] }
    return rules.filter { rule ->
        column.all { value ->
            rule.ranges.any { range -> value in range}
        }
    }
}

class Day16_Part1 : FunSpec({
    val exampleString = """
    class: 1-3 or 5-7
    row: 6-11 or 33-44
    seat: 13-40 or 45-50
    
    your ticket:
    7,1,14
    
    nearby tickets:
    7,3,47
    40,4,50
    55,2,20
    38,6,12        
    """.trimIndent()
    context("parse train tickets") {
        context("parse ticket rule class") {
            val ticketRuleString = "class: 1-3 or 5-7"
            val ticketRule = parseTicketRule(ticketRuleString)
            test("ticket rule is parsed correctly") {
                ticketRule.name shouldBe "class"
                ticketRule.ranges shouldBe listOf(1..3, 5..7)
            }
        }
        context("parse ticket rule departure location") {
            val ticketRuleString = "departure location: 27-374 or 395-974"
            val ticketRule = parseTicketRule(ticketRuleString)
            test("ticket rule is parsed correctly") {
                ticketRule.name shouldBe "departure location"
                ticketRule.ranges shouldBe listOf(27..374, 395..974)
            }
        }
        context("parse ticket") {
            val ticketString = "7,3,47"
            val ticket = parseTicket(ticketString)
            test("ticket is parsed correctly") {
                ticket shouldBe listOf(7, 3, 47)
            }
        }
        context("parse notes") {
            val notes = parseTrainTicketNotes(exampleString)
            test("notes should be parsed correctly") {
                notes.rules.size shouldBe 3
                notes.rules[1] shouldBe TicketRule("row", listOf(6..11, 33..44))
                notes.yourTicket shouldBe listOf(7, 1, 14)
                notes.nearbyTickets.size shouldBe 4
                notes.nearbyTickets[1] shouldBe listOf(40, 4, 50)
            }
        }
    }
    context("filter valid tickets") {
        val notes = parseTrainTicketNotes(exampleString)
        val filtered = notes.nearbyTickets.filterTicketsSatisfyingAnyRule(notes.rules)
        test("should have filtered values") {
            filtered shouldBe listOf(
                listOf(7, 3, 47)
            )
        }
    }
    context("filter invalid tickets") {
        val notes = parseTrainTicketNotes(exampleString)
        val filtered = notes.nearbyTickets.filterValuesNotSatisfyingAllRules(notes.rules)
        test("should have filtered values") {
            filtered.flatten() shouldBe listOf(4, 55, 12)
        }
        test("should have calculated correct sum") {
            filtered.flatten().sum() shouldBe 71
        }
    }
})

class Day16_Part1_Exercise: FunSpec({
    val input = readResource("day16Input.txt")!!
    val notes = parseTrainTicketNotes(input)
    val filtered = notes.nearbyTickets.filterValuesNotSatisfyingAllRules(notes.rules)
    test("should have calculated correct sum") {
        filtered.flatten().sum() shouldBe 26941
    }
})

class Day16_Part2 : FunSpec({
    val exampleString = """
    class: 0-1 or 4-19
    row: 0-5 or 8-19
    seat: 0-13 or 16-19
    
    your ticket:
    11,12,13
    
    nearby tickets:
    3,9,18
    15,1,5
    5,14,9
    """.trimIndent()
    val notes = parseTrainTicketNotes(exampleString)
    context("find rules for row") {
        val rules = findRuleForColumn(notes.nearbyTickets, notes.rules, 0)
        test("should have found rules") {
            rules.first().name shouldBe "row"
        }
    }
    context("find rules for rows") {
        val columnMapping = findRuleForRows(notes.yourTicket, notes.nearbyTickets, notes.rules)
        test("should have found mappings") {
            columnMapping shouldBe mapOf(
                "class" to 12,
                "row" to 11,
                "seat" to 13
            )
        }
    }
})

class Day16_Part2_Exercise: FunSpec({
    val input = readResource("day16Input.txt")!!
    val notes = parseTrainTicketNotes(input)
    val filteredTickets = notes.nearbyTickets.filterTicketsSatisfyingAnyRule(notes.rules)
    val columnMapping = findRuleForRows(notes.yourTicket, filteredTickets, notes.rules)
    val columnMappingDeparture = columnMapping.entries.filter { (key, _) -> key.startsWith("departure") }
    println(columnMappingDeparture)
    val solution = columnMappingDeparture.map { (_, value) -> value }.map { it.toLong() }.reduce { a, b -> a * b}
    test("should have calculated correct solution") {
        solution shouldBe 634796407951L
    }
})
