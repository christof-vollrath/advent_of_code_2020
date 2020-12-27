import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.lang.IllegalStateException


/*
--- Day 22: Crab Combat ---

See https://adventofcode.com/2020/day/22


 */

open class CrabCombatGame(var decks: CrabCombatDecks) {
    val score: Int
        get() = when(winner) {
            1 -> calculateScore(decks.player1)
            2 -> calculateScore(decks.player2)
            else ->  throw IllegalStateException("No unique winner")
        }

    open val winner: Int
        get() {
            if (!stopped) throw IllegalStateException("Game still running")
            return when {
                decks.player1.isEmpty() -> 2
                decks.player2.isEmpty() -> 1
                else ->  throw IllegalStateException("No winner found")
            }
        }

    private fun calculateScore(player2: ArrayDeque<Int>): Int =
        player2.reversed().mapIndexed { index, cardValue ->  cardValue * (index + 1) }.sum()

    var round = 0
        protected set
    var stopped = false
        protected set

    open fun playRound() {
        if (decks.checkEndOfGame()) return
        with(decks) {
            val card1 = player1.removeFirst()
            val card2 = player2.removeFirst()
            if (card1 > card2) {
                player1.addLast(card1)
                player1.addLast(card2)
            } else {
                player2.addLast(card2)
                player2.addLast(card1)
            }
            round++
            //println("round=$round")
            //println("player1=$player1")
            //println("player2=$player2")
        }
    }

    fun playGame() {
        while(! stopped) {
            playRound()
        }
    }

    open fun CrabCombatDecks.checkEndOfGame(): Boolean {
        if (player1.isEmpty() || player2.isEmpty()) {
            stopped = true
            return true
        }
        return false
    }
}

fun parseCrabCombatDecks(decksString: String): CrabCombatDecks {
    val players = decksString.split("\n\n")
    val player1deck = players[0].split("\n").drop(1).map { it.toInt() }
    val player2deck = players[1].split("\n").drop(1).map { it.toInt() }
    return CrabCombatDecks(player1deck, player2deck)
}

data class CrabCombatDecks(val player1: ArrayDeque<Int>, val player2: ArrayDeque<Int>) {
    constructor(cards1: List<Int>, cards2: List<Int>): this(ArrayDeque<Int>(), ArrayDeque<Int>()) {
        player1.addAll(cards1)
        player2.addAll(cards2)
    }
}

class CrabCombatGame2(decks: CrabCombatDecks) : CrabCombatGame(decks) {
    val historyPlayer1 = mutableSetOf<List<Int>>() // Only history for one player 1 is needed because other player has the other cards
    var aborted = false

    override val winner
        get() = if (aborted) 1 else super.winner

    override fun playRound() {
        historyPlayer1 += decks.player1.toList()
        playRound2()
        if (!stopped && historyPlayer1.contains(decks.player1.toList())) { // Loop, always player 2 wins
            stopped = true
            aborted = true
        }
    }

    fun playRound2() {
        if (decks.checkEndOfGame()) return
        with(decks) {
            val card1 = player1.first()
            val card2 = player2.first()
            if (card1 < player1.size && card2 < player2.size) { // start recursive game
                player1.removeFirst()
                player2.removeFirst()
                val recursiveGame = startRecursiveGame(card1, card2)
                if (recursiveGame.winner == 1) {
                    player1.addLast(card1)
                    player1.addLast(card2)
                } else {
                    player2.addLast(card2)
                    player2.addLast(card1)
                }
                round++
                //println("round=$round - after recursion")
                //println("player1=$player1")
                //println("player2=$player2")
            } else super.playRound()
        }
    }

    fun startRecursiveGame(nrCards1: Int, nrCards2: Int): CrabCombatGame2 {
        val recursiveDecks = CrabCombatDecks(
            decks.player1.toList().take(nrCards1),
            decks.player2.toList().take(nrCards2)
        ) // Make sure that deck is copied for recursion
        //println("start recursion")
        //println("player1=${recursiveDecks.player1}")
        //println("player2=${recursiveDecks.player2}")
        val recursiveGame = CrabCombatGame2(recursiveDecks)
        recursiveGame.playGame()
        return recursiveGame
    }
}

val exampleDecksString = """
            Player 1:
            9
            2
            6
            3
            1
            
            Player 2:
            5
            8
            4
            7
            10
        """.trimIndent()

class Day22_Part1 : FunSpec({
    val decks = parseCrabCombatDecks(exampleDecksString)
    context("parse decks") {
         test("should have parsed to two decks") {
            decks.player1 shouldBe listOf(9, 2, 6, 3, 1)
            decks.player2 shouldBe listOf(5, 8, 4, 7, 10)
        }
    }
    context("play round") {
        val game = CrabCombatGame(decks)
        game.playRound()
        test("should have played one round") {
            game.round shouldBe 1
        }
        test("game should continue") {
            game.stopped shouldBe false
        }
        test("after playing the round, the decks should have changed") {
            with(game.decks) {
                player1 shouldBe listOf(2, 6, 3, 1, 9, 5)
                player2 shouldBe listOf(8, 4, 7, 10)
            }
        }
        context("continue playing") {
            game.playGame()
            test("should have played 29 round") {
                game.round shouldBe 29
            }
            test("winner should be 2") {
                game.winner shouldBe 2
            }
            test("after playing all rounds, player 2 should have won") {
                with(game.decks) {
                    player1 shouldBe listOf()
                    player2 shouldBe listOf(3, 2, 10, 6, 8, 5, 9, 4, 7, 1)
                }
            }
            test("should have the expected score") {
                game.score shouldBe 306
            }

        }
    }
})

class Day22_Part1_Exercise: FunSpec({
    val input = readResource("day22Input.txt")!!
    val decks = parseCrabCombatDecks(input)
    val game = CrabCombatGame(decks)
    game.playGame()
    val solution = game.score
    test("should have the expected score") {
        solution shouldBe 34566
    }
})

class Day22_Part2 : FunSpec({
    context("avoid loop") {
        val loopsDecksString = """
            Player 1:
            43
            19
            
            Player 2:
            2
            29
            14
            """.trimIndent()
        val loopsDecks = parseCrabCombatDecks(loopsDecksString)
        val gameWithLoop = CrabCombatGame2(loopsDecks)
        while (! gameWithLoop.stopped) gameWithLoop.playRound()
        test("should terminate") {
            gameWithLoop.stopped shouldBe true
        }
        test("player 1 should win when game aborts because of a loop") {
            gameWithLoop.winner shouldBe 1
        }
    }
    context("play 8 rounds") {
        val decks = parseCrabCombatDecks(exampleDecksString)
        val game = CrabCombatGame2(decks)
        repeat(8) { game.playRound() }
        test("should have played 8 rounds") {
            game.round shouldBe 8
        }
        test("game should continue") {
            game.stopped shouldBe false
        }
        test("after playing the round, the decks should have changed") {
            with(game.decks) {
                player1 shouldBe listOf(4, 9, 8, 5, 2)
                player2 shouldBe listOf(3, 10, 1, 7, 6)
            }
        }
        context("after another round, game should go into a recursive game and return the result") {
            game.playRound()
            test("after playing the round, the decks should have changed depending on the result of the recursive game") {
                with(game.decks) {
                    player1 shouldBe listOf(9, 8, 5, 2)
                    player2 shouldBe listOf(10, 1, 7, 6, 3, 4)
                }
            }
        }
        context("continue the game") {
            game.playGame()
            test("should have played 17 rounds") {
                game.round shouldBe 17
            }
            test("player 2 should have won") {
                game.winner shouldBe 2
            }
            test("should have the expected score") {
                game.score shouldBe 291
            }
        }
    }
})


class Day22_Part2_Exercise: FunSpec({
    context("solve exercise") {
        val input = readResource("day22Input.txt")!!
        val decks = parseCrabCombatDecks(input)
        val game = CrabCombatGame2(decks)
        while (! game.stopped) game.playRound()
        val solution = game.score
        test("should have the expected score") {
            solution shouldBe 31854
        }
    }
})


