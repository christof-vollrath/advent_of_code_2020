import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.lang.IllegalStateException

/*
--- Day 10: Adapter Array ---

Patched into the aircraft's data port, you discover weather forecasts of a massive tropical storm.
Before you can figure out whether it will impact your vacation plans, however, your device suddenly turns off!

Its battery is dead.

You'll need to plug it in. There's only one problem:
the charging outlet near your seat produces the wrong number of jolts.
Always prepared, you make a list of all of the joltage adapters in your bag.

Each of your joltage adapters is rated for a specific output joltage (your puzzle input).
Any given adapter can take an input 1, 2, or 3 jolts lower than its rating and still produce its rated output joltage.

In addition, your device has a built-in joltage adapter rated for 3 jolts higher
than the highest-rated adapter in your bag.
(If your adapter list were 3, 9, and 6, your device's built-in adapter would be rated for 12 jolts.)

Treat the charging outlet near your seat as having an effective joltage rating of 0.

Since you have some time to kill, you might as well test all of your adapters.
Wouldn't want to get to your resort and realize you can't even charge your device!

If you use every adapter in your bag at once,
what is the distribution of joltage differences between the charging outlet, the adapters, and your device?

For example, suppose that in your bag, you have adapters with the following joltage ratings:

16
10
15
5
1
11
7
19
6
12
4

With these adapters, your device's built-in joltage adapter would be rated for 19 + 3 = 22 jolts,
3 higher than the highest-rated adapter.

Because adapters can only connect to a source 1-3 jolts lower than its rating, in order to use every adapter,
you'd need to choose them like this:

The charging outlet has an effective rating of 0 jolts, so the only adapters
that could connect to it directly would need to have a joltage rating of 1, 2, or 3 jolts.
Of these, only one you have is an adapter rated 1 jolt (difference of 1).
From your 1-jolt rated adapter, the only choice is your 4-jolt rated adapter (difference of 3).
From the 4-jolt rated adapter, the adapters rated 5, 6, or 7 are valid choices.
However, in order to not skip any adapters, you have to pick the adapter rated 5 jolts (difference of 1).
Similarly, the next choices would need to be the adapter rated 6 and then the adapter rated 7
(with difference of 1 and 1).
The only adapter that works with the 7-jolt rated adapter is the one rated 10 jolts (difference of 3).
From 10, the choices are 11 or 12; choose 11 (difference of 1) and then 12 (difference of 1).
After 12, only valid adapter has a rating of 15 (difference of 3), then 16 (difference of 1), then 19 (difference of 3).
Finally, your device's built-in adapter is always 3 higher than the highest adapter, so its rating is 22 jolts
(always a difference of 3).
In this example, when using every adapter, there are 7 differences of 1 jolt and 5 differences of 3 jolts.

Here is a larger example:

28
33
18
42
31
14
46
20
48
47
24
23
49
45
19
38
39
11
1
32
25
35
8
17
7
9
4
2
34
10
3

In this larger example, in a chain that uses all of the adapters,
there are 22 differences of 1 jolt and 10 differences of 3 jolts.

Find a chain that uses all of your adapters to connect the charging outlet to your device's built-in adapter
and count the joltage differences between the charging outlet, the adapters, and your device.
What is the number of 1-jolt differences multiplied by the number of 3-jolt differences?

--- Part Two ---

To completely determine whether you have enough adapters,
you'll need to figure out how many different ways they can be arranged.
Every arrangement needs to connect the charging outlet to your device.
The previous rules about when adapters can successfully connect still apply.

The first example above (the one that starts with 16, 10, 15) supports the following arrangements:

(0), 1, 4, 5, 6, 7, 10, 11, 12, 15, 16, 19, (22)
(0), 1, 4, 5, 6, 7, 10, 12, 15, 16, 19, (22)
(0), 1, 4, 5, 7, 10, 11, 12, 15, 16, 19, (22)
(0), 1, 4, 5, 7, 10, 12, 15, 16, 19, (22)
(0), 1, 4, 6, 7, 10, 11, 12, 15, 16, 19, (22)
(0), 1, 4, 6, 7, 10, 12, 15, 16, 19, (22)
(0), 1, 4, 7, 10, 11, 12, 15, 16, 19, (22)
(0), 1, 4, 7, 10, 12, 15, 16, 19, (22)

(The charging outlet and your device's built-in adapter are shown in parentheses.)
Given the adapters from the first example,
the total number of arrangements that connect the charging outlet to your device is 8.

The second example above (the one that starts with 28, 33, 18) has many arrangements.
Here are a few:

(0), 1, 2, 3, 4, 7, 8, 9, 10, 11, 14, 17, 18, 19, 20, 23, 24, 25, 28, 31,
32, 33, 34, 35, 38, 39, 42, 45, 46, 47, 48, 49, (52)

(0), 1, 2, 3, 4, 7, 8, 9, 10, 11, 14, 17, 18, 19, 20, 23, 24, 25, 28, 31,
32, 33, 34, 35, 38, 39, 42, 45, 46, 47, 49, (52)

(0), 1, 2, 3, 4, 7, 8, 9, 10, 11, 14, 17, 18, 19, 20, 23, 24, 25, 28, 31,
32, 33, 34, 35, 38, 39, 42, 45, 46, 48, 49, (52)

(0), 1, 2, 3, 4, 7, 8, 9, 10, 11, 14, 17, 18, 19, 20, 23, 24, 25, 28, 31,
32, 33, 34, 35, 38, 39, 42, 45, 46, 49, (52)

(0), 1, 2, 3, 4, 7, 8, 9, 10, 11, 14, 17, 18, 19, 20, 23, 24, 25, 28, 31,
32, 33, 34, 35, 38, 39, 42, 45, 47, 48, 49, (52)

(0), 3, 4, 7, 10, 11, 14, 17, 20, 23, 25, 28, 31, 34, 35, 38, 39, 42, 45,
46, 48, 49, (52)

(0), 3, 4, 7, 10, 11, 14, 17, 20, 23, 25, 28, 31, 34, 35, 38, 39, 42, 45,
46, 49, (52)

(0), 3, 4, 7, 10, 11, 14, 17, 20, 23, 25, 28, 31, 34, 35, 38, 39, 42, 45,
47, 48, 49, (52)

(0), 3, 4, 7, 10, 11, 14, 17, 20, 23, 25, 28, 31, 34, 35, 38, 39, 42, 45,
47, 49, (52)

(0), 3, 4, 7, 10, 11, 14, 17, 20, 23, 25, 28, 31, 34, 35, 38, 39, 42, 45,
48, 49, (52)

In total, this set of adapters can connect the charging outlet to your device in 19208 distinct arrangements.

You glance back down at your bag and try to remember why you brought so many adapters;
there must be more than a trillion valid ways to arrange them!
Surely, there must be an efficient way to count the arrangements.

What is the total number of distinct ways you can arrange the adapters to connect the charging outlet to your device?

 */


fun Set<Int>.findAdapterChain(start: Int): List<Pair<Int, Int>> {
    val currentSet = this.toMutableSet()
    var currentJolts = start
    return sequence {
        while(currentSet.isNotEmpty()) {
            val nextJolts = findJoltageCandidates(currentJolts).intersect(currentSet).minOrNull() ?: throw IllegalStateException("No adapter for $currentJolts")
            yield(nextJolts - currentJolts to nextJolts)
            currentSet -= nextJolts
            currentJolts = nextJolts
        }
        yield(3 to currentJolts + 3)
    }.toList()
}

fun parseAdapters(numbersString: String) =
    numbersString.split("\n").map{ it.toInt() }

fun findJoltageCandidates(jolts: Int): Set<Int> = (1..3).map { jolts + it }.toSet()

fun Set<Int>.findAlternatives(): List<Pair<Int, Set<Int>>> {
    val adapters = this + setOf(0)
    val sortedAdapters = adapters.sorted()
    return sequence {
        for(currentAdapter in sortedAdapters) {
            val nextAdapters = findJoltageCandidates(currentAdapter).intersect(this@findAlternatives)
            yield(currentAdapter to nextAdapters)
        }
    }.toList()
}

fun findJoltageCandidatesBackwards(jolts: Int): Set<Int> = (1..3).map { jolts - it }.toSet()
fun Set<Int>.findAlternativesBackwards(): Map<Int, Set<Int>> {
    val adapters = this + setOf(0)
    val sortedAdapters = this.sorted()
    return sequence {
        for(currentAdapter in sortedAdapters) {
            val nextAdapters = findJoltageCandidatesBackwards(currentAdapter).intersect(adapters)
            yield(currentAdapter to nextAdapters)
        }
    }.toMap()
}

fun Set<Int>.findCombinations(): List<Pair<Long, Int>> {
    val reachableFrom = findAlternativesBackwards().toMap()
    val numberOfCombinationsPerAdapter = linkedMapOf<Int, Long>(0 to 1)
    for(adapter in this.sorted()) {
        if (adapter == 0) continue
        val adapterReachableFrom = reachableFrom[adapter] ?: throw IllegalStateException("Adapter $adapter can't be reached")
        val numberOfCombinations = adapterReachableFrom.map { from ->
            numberOfCombinationsPerAdapter[from] ?: throw IllegalStateException("Unknown combinations for $from")
        }.sum()
        numberOfCombinationsPerAdapter[adapter] = numberOfCombinations
    }
    return numberOfCombinationsPerAdapter.entries.map { it.value to it.key }
}

fun Set<Int>.findCombinationsDeprecated(): List<Pair<Long, Int>> {
    val alternatives = findAlternatives()
    val reachableFrom = alternatives.invertConnections()
    val numberOfCombinationsPerAdapter = linkedMapOf(0 to 1L)
    for(adapter in this.sorted()) {
        if (adapter == 0) continue
        val adapterReachableFrom = reachableFrom[adapter] ?: throw IllegalStateException("Adapter $adapter can't be reached")
        val numberOfCombinations = adapterReachableFrom.map { from ->
            numberOfCombinationsPerAdapter[from] ?: throw IllegalStateException("Unknown combinations for $from")
        }.sum()
        numberOfCombinationsPerAdapter[adapter] = numberOfCombinations
    }
    return numberOfCombinationsPerAdapter.entries.map { it.value to it.key }
}

fun List<Pair<Int, Set<Int>>>.invertConnections() =
    flatMap { (adapter, connectedTos) ->
        connectedTos.map { connectedTo ->
            connectedTo to adapter
        }.toSet()
    }.groupBy { it.first }
        .map { (adapter, connections) ->
            adapter to connections.map { it.second }.toSet()
        }.toMap()

val adaptersString = """
        16
        10
        15
        5
        1
        11
        7
        19
        6
        12
        4
    """.trimIndent()

val largerExample = """
        28
        33
        18
        42
        31
        14
        46
        20
        48
        47
        24
        23
        49
        45
        19
        38
        39
        11
        1
        32
        25
        35
        8
        17
        7
        9
        4
        2
        34
        10
        3
    """.trimIndent()

class Day10_Part1 : FunSpec({
    val numbers = parseAdapters(adaptersString)
    context("find next joltage candidates for 0") {
        val candidates = findJoltageCandidates(0)
        candidates shouldBe setOf(1, 2, 3)
    }
    context("find next joltage candidates for 2") {
        val candidates = findJoltageCandidates(2)
        candidates shouldBe setOf(3, 4, 5)
    }
    context("find adapter chain") {
        val chain = numbers.toSet().findAdapterChain(0)
        chain shouldBe listOf(
            1 to 1,
            3 to 4,
            1 to 5,
            1 to 6,
            1 to 7,
            3 to 10,
            1 to 11,
            1 to 12,
            3 to 15,
            1 to 16,
            3 to 19,
            3 to 22
        )
        chain.filter { it.first == 1 }.count() shouldBe 7
        chain.filter { it.first == 3 }.count() shouldBe 5
    }
    context("find adapter chain for larger example") {
        val largerExampleNumbers = parseAdapters(largerExample)
        val chain = largerExampleNumbers.toSet().findAdapterChain(0)
        chain.filter { it.first == 1 }.count() shouldBe 22
        chain.filter { it.first == 3 }.count() shouldBe 10
    }
})

class Day10_Part1_Exercise: FunSpec({
    val input = readResource("day10Input.txt")!!
    val numbers = parseAdapters(input)
    val chain = numbers.toSet().findAdapterChain(0)
    val count1 = chain.filter { it.first == 1 }.count()
    val count3 = chain.filter { it.first == 3 }.count()
    val result = count1 * count3
    test("solution") {
        result shouldBe 1984
    }

})

class Day10_Part2 : FunSpec({
    val numbers = parseAdapters(adaptersString)
    context("find alternatives for next adapter") {
        val alternatives = numbers.toSet().findAlternatives()
        alternatives shouldBe listOf(
            0 to setOf(1),
            1 to setOf(4), 
            4 to setOf(5, 6, 7),
            5 to setOf(6, 7),
            6 to setOf(7),
            7 to setOf(10),
            10 to setOf(11, 12),
            11 to setOf(12),
            12 to setOf(15),
            15 to setOf(16),
            16 to setOf(19),
            19 to setOf()
        )
        test("connections backwards") {
            val alternativesBackwards = numbers.toSet().findAlternativesBackwards()
            alternativesBackwards shouldBe mapOf(
                1 to setOf(0),
                4 to setOf(1),
                5 to setOf(4),
                6 to setOf(4, 5),
                7 to setOf(4, 5, 6),
                10 to setOf(7),
                11 to setOf(10),
                12 to setOf(10, 11),
                15 to setOf(12),
                16 to setOf(15),
                19 to setOf(16)
            )
        }
        context("invert connections") {
            val inverted = alternatives.invertConnections()
            test("inverted connections") {
                inverted shouldBe mapOf(
                    1 to setOf(0),
                    4 to setOf(1),
                    5 to setOf(4),
                    6 to setOf(4, 5),
                    7 to setOf(4, 5, 6),
                    10 to setOf(7),
                    11 to setOf(10),
                    12 to setOf(10, 11),
                    15 to setOf(12),
                    16 to setOf(15),
                    19 to setOf(16)
                )
            }
        }
        context("invert connections and connections should be equal") {
            val largerExampleNumbers = parseAdapters(largerExample)
            val alternativesBackwards = largerExampleNumbers.toSet().findAlternativesBackwards()
            val inverted = largerExampleNumbers.toSet().findAlternatives().invertConnections()
            alternativesBackwards shouldBe inverted
        }
    }
    context("find combinations for adapters") {
        val combinations = numbers.toSet().findCombinationsDeprecated()
        combinations shouldBe listOf(
            1L to 0,
            1L to 1,
            1L to 4,
            1L to 5,
            2L to 6,
            4L to 7,
            4L to 10,
            4L to 11,
            8L to 12,
            8L to 15,
            8L to 16,
            8L to 19,
        )
        numbers.toSet().findCombinations() shouldBe combinations
    }
    context("find combinations for adapters in larger example") {
        val largerExampleNumbers = parseAdapters(largerExample)
        val combinations = largerExampleNumbers.toSet().findCombinations()
        combinations.last().first shouldBe 19208L
    }
    context("find combinations for adapters in larger example (deprecated)") {
        val largerExampleNumbers = parseAdapters(largerExample)
        val combinations = largerExampleNumbers.toSet().findCombinationsDeprecated()
        combinations.last().first shouldBe 19208L
    }
})

class Day10_Part2_Exercise: FunSpec({
    val input = readResource("day10Input.txt")!!
    val numbers = parseAdapters(input)
    val combinations = numbers.toSet().findCombinations()
    val result = combinations.last().first
    test("solution") {
        result shouldBe 3543369523456L
    }
})
