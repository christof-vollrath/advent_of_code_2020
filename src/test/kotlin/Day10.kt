import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.lang.IllegalStateException

/*
--- Day 10: Adapter Array ---

See https://adventofcode.com/2020/day/10

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
