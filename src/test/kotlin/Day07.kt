import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/*
--- Day 7: Handy Haversacks ---

You land at the regional airport in time for your next flight.
In fact, it looks like you'll even have time to grab some food:
all flights are currently delayed due to issues in luggage processing.

Due to recent aviation regulations, many rules (your puzzle input) are being enforced about bags and their contents;
bags must be color-coded and must contain specific quantities of other color-coded bags. Apparently,
nobody responsible for these regulations considered how long they would take to enforce!

For example, consider the following rules:

light red bags contain 1 bright white bag, 2 muted yellow bags.
dark orange bags contain 3 bright white bags, 4 muted yellow bags.
bright white bags contain 1 shiny gold bag.
muted yellow bags contain 2 shiny gold bags, 9 faded blue bags.
shiny gold bags contain 1 dark olive bag, 2 vibrant plum bags.
dark olive bags contain 3 faded blue bags, 4 dotted black bags.
vibrant plum bags contain 5 faded blue bags, 6 dotted black bags.
faded blue bags contain no other bags.
dotted black bags contain no other bags.

These rules specify the required contents for 9 bag types.
In this example, every faded blue bag is empty, every vibrant plum bag contains 11 bags
(5 faded blue and 6 dotted black), and so on.

You have a shiny gold bag.
If you wanted to carry it in at least one other bag,
how many different bag colors would be valid for the outermost bag?
(In other words: how many colors can, eventually, contain at least one shiny gold bag?)

In the above rules, the following options would be available to you:

A bright white bag, which can hold your shiny gold bag directly.
A muted yellow bag, which can hold your shiny gold bag directly, plus some other bags.
A dark orange bag, which can hold bright white and muted yellow bags, either of which could then hold your shiny gold bag.
A light red bag, which can hold bright white and muted yellow bags, either of which could then hold your shiny gold bag.

So, in this example, the number of bag colors that can eventually contain at least one shiny gold bag is 4.

How many bag colors can eventually contain at least one shiny gold bag?
(The list of rules is quite long; make sure you get all of it.)

--- Part Two ---

It's getting pretty expensive to fly these days - not because of ticket prices,
but because of the ridiculous number of bags you need to buy!

Consider again your shiny gold bag and the rules from the above example:

faded blue bags contain 0 other bags.
dotted black bags contain 0 other bags.
vibrant plum bags contain 11 other bags: 5 faded blue bags and 6 dotted black bags.
dark olive bags contain 7 other bags: 3 faded blue bags and 4 dotted black bags.
So, a single shiny gold bag must contain 1 dark olive bag (and the 7 bags within it)
plus 2 vibrant plum bags (and the 11 bags within each of those): 1 + 1*7 + 2 + 2*11 = 32 bags!

Of course, the actual rules have a small chance of going several levels deeper than this example;
be sure to count all of the bags, even if the nesting becomes topologically impractical!

Here's another example:

shiny gold bags contain 2 dark red bags.
dark red bags contain 2 dark orange bags.
dark orange bags contain 2 dark yellow bags.
dark yellow bags contain 2 dark green bags.
dark green bags contain 2 dark blue bags.
dark blue bags contain 2 dark violet bags.
dark violet bags contain no other bags.

In this example, a single shiny gold bag must contain 126 other bags.

How many individual bags are required inside your single shiny gold bag?

 */

fun Collection<Bag>.findAllBagsContaining(color: String): Set<Bag> {
    val containingMap = this.flatMap {bag ->
        bag.content.map { bagQuantity ->
            bagQuantity.color to bag
        }
    }.groupBy { it.first }
    return sequence {
        var currentColors = listOf(color)
        while(true) {
            val nextColors = currentColors.flatMap { currentColor ->
                val containers = containingMap[currentColor]
                yield(containers?.map { it.second } ?: emptyList<Bag>())
                containers?.map { it.second.color } ?: emptyList()
            }
            if(nextColors.isEmpty()) break
            else currentColors = nextColors
        }
    }.flatten().toSet()
}

fun Collection<BagQuantity>.sumByColor() = groupBy { it.color }
    .entries.map { (color, bagQuantities) ->
        BagQuantity(color, bagQuantities.map { it.quantity }.sum())
    }
fun Collection<Bag>.findAllBagsContainingWithQuantity(initalBagQuantity: BagQuantity): Set<BagQuantity> {
    val containingMap = this.flatMap {bag ->
        bag.content.map { bagQuantity ->
            bag.color to bagQuantity
        }
    }.groupBy { it.first }
    return sequence {
        var currentBagQuantities = listOf(initalBagQuantity)
        while(true) {
            val nextBagQuantities = currentBagQuantities.flatMap { currentBagQuantity ->
                val containers = containingMap[currentBagQuantity.color]
                val interimResult = containers?.map { (_, bagQuantity) -> BagQuantity(bagQuantity.color, currentBagQuantity.quantity * bagQuantity.quantity) } ?: emptyList<BagQuantity>()
                yield(interimResult)
                interimResult
            }
            if(nextBagQuantities.isEmpty()) break
            else currentBagQuantities = nextBagQuantities.sumByColor()
        }
    }.flatten().toList().sumByColor().toSet()
}

fun parseBagSpecifications(bagSpecifications: String): List<Bag> = bagSpecifications.split("\n")
    .map { parseBagSpecification(it) }

fun parseBagSpecification(bagSpecString: String): Bag {
    val regex = """(\w+) (\w+) bags contain (.*)""".toRegex()
    val match = regex.find(bagSpecString) ?: throw IllegalArgumentException("Can not parse input")
    if (match.groupValues.size < 4) throw IllegalArgumentException("Not enough elements parsed")
    println(match.groupValues)
    val color = match.groupValues[1] + " " + match.groupValues[2]
    val content = if (match.groupValues[3] == "no other bags.") emptySet()
    else {
        val pattern = """(\d+) (\w+) (\w+) bags?,?""".toPattern()
        val matcher = pattern.matcher(match.groupValues[3])
        sequence {
            while(matcher.find()) {
                val quantity = matcher.group(1).toInt()
                val containingColor = matcher.group(2) + " " + matcher.group(3)
                yield(BagQuantity(containingColor, quantity))
            }
        }.toSet()
    }
    return Bag(color, content)
}

data class BagQuantity(val color: String, val quantity: Int)
data class Bag(val color: String, val content: Set<BagQuantity>)

class Day07_Part1 : FunSpec({
    context("parse bag specification") {
        test("parse single bag specification with two containing bags") {
            val bagSpecification = "light red bags contain 1 bright white bag, 2 muted yellow bags."
            parseBagSpecification(bagSpecification) shouldBe Bag(color = "light red", content = setOf(
                    BagQuantity(color = "bright white", quantity = 1),
                    BagQuantity(color = "muted yellow", quantity = 2),
                )
            )
        }
        test("parse single bag specification with one containing bag") {
            val bagSpecification = "bright white bags contain 1 shiny gold bag."
            parseBagSpecification(bagSpecification) shouldBe Bag(color = "bright white", content = setOf(
                    BagQuantity(color = "shiny gold", quantity = 1),
                )
            )
        }
        test("parse single bag specification with no containing bag") {
            val bagSpecification = "faded blue bags contain no other bags."
            parseBagSpecification(bagSpecification) shouldBe Bag(color = "faded blue", content = emptySet())
        }
        test("parse single bag specification with four containing bags") {
            val bagSpecification = "light fuchsia bags contain 2 dotted silver bags, 3 dotted lavender bags, 3 shiny gold bags, 5 clear magenta bags."
            parseBagSpecification(bagSpecification) shouldBe Bag(color = "light fuchsia", content = setOf(
                BagQuantity(color = "dotted silver", quantity = 2),
                BagQuantity(color = "dotted lavender", quantity = 3),
                BagQuantity(color = "shiny gold", quantity = 3),
                BagQuantity(color = "clear magenta", quantity = 5),
            )
            )
        }
    }
    val bagSpecificationsString = """
                light red bags contain 1 bright white bag, 2 muted yellow bags.
                dark orange bags contain 3 bright white bags, 4 muted yellow bags.
                bright white bags contain 1 shiny gold bag.
                muted yellow bags contain 2 shiny gold bags, 9 faded blue bags.
                shiny gold bags contain 1 dark olive bag, 2 vibrant plum bags.
                dark olive bags contain 3 faded blue bags, 4 dotted black bags.
                vibrant plum bags contain 5 faded blue bags, 6 dotted black bags.
                faded blue bags contain no other bags.
                dotted black bags contain no other bags.
            """.trimIndent()
    context("parse bags") {
        test("parse bag specifications") {
            parseBagSpecifications(bagSpecificationsString).size shouldBe 9
        }
    }
    context("find all bags containing") {
        val bagSpecifications = parseBagSpecifications(bagSpecificationsString)
        val bagsContainingShinyGold = bagSpecifications.findAllBagsContaining("shiny gold")
        test("should contain the right number of bags") {
            bagsContainingShinyGold.size shouldBe 4
        }
        test("should contain the right bags") {
            bagsContainingShinyGold.map { it.color }.toSet() shouldBe setOf("bright white", "muted yellow", "dark orange", "light red")
        }
    }
})

class Day07_Part1_Exercise: FunSpec({
    val input = readResource("day07Input.txt")!!
    val bagSpecifications = parseBagSpecifications(input)
    test("should have parsed all bag specifications") {
        bagSpecifications.size shouldBe 594
    }
    val count = bagSpecifications.findAllBagsContaining("shiny gold").size
    test("solution") {
        count shouldBe 205
    }
})

class Day07_Part2 : FunSpec({
    context("find all bags containing with quantity example 1") {
        val bagSpecificationsString = """
                light red bags contain 1 bright white bag, 2 muted yellow bags.
                dark orange bags contain 3 bright white bags, 4 muted yellow bags.
                bright white bags contain 1 shiny gold bag.
                muted yellow bags contain 2 shiny gold bags, 9 faded blue bags.
                shiny gold bags contain 1 dark olive bag, 2 vibrant plum bags.
                dark olive bags contain 3 faded blue bags, 4 dotted black bags.
                vibrant plum bags contain 5 faded blue bags, 6 dotted black bags.
                faded blue bags contain no other bags.
                dotted black bags contain no other bags.
            """.trimIndent()
        val bagSpecifications = parseBagSpecifications(bagSpecificationsString)
        val bagsContainingShinyGold = bagSpecifications.findAllBagsContainingWithQuantity(BagQuantity("shiny gold", 1))
        test("should contain the right number of bags") {
            bagsContainingShinyGold.size shouldBe 4
        }
        test("should contain the right quantity") {
            bagsContainingShinyGold.map { it.quantity }.sum() shouldBe 32
        }
    }
    context("find all bags containing with quantity example 2") {
        val bagSpecificationsString = """
            shiny gold bags contain 2 dark red bags.
            dark red bags contain 2 dark orange bags.
            dark orange bags contain 2 dark yellow bags.
            dark yellow bags contain 2 dark green bags.
            dark green bags contain 2 dark blue bags.
            dark blue bags contain 2 dark violet bags.
            dark violet bags contain no other bags.
            """.trimIndent()
        val bagSpecifications = parseBagSpecifications(bagSpecificationsString)
        val bagsContainingShinyGold = bagSpecifications.findAllBagsContainingWithQuantity(BagQuantity("shiny gold", 1))
        println(bagsContainingShinyGold)
        test("should contain the right quantity") {
            bagsContainingShinyGold.map { it.quantity }.sum() shouldBe 126
        }
    }
})


class Day07_Part2_Exercise: FunSpec({
    val input = readResource("day07Input.txt")!!
    val bagSpecifications = parseBagSpecifications(input)
    test("should have parsed all bag specifications") {
        bagSpecifications.size shouldBe 594
    }
    val quantity = bagSpecifications.findAllBagsContainingWithQuantity(BagQuantity("shiny gold", 1)).map { it.quantity }.sum()
    test("solution") {
        quantity shouldBe 80902
    }
})
