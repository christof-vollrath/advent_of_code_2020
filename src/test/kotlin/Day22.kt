import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe


/*
--- Day 22: Crab Combat ---

It only takes a few hours of sailing the ocean on a raft for boredom to sink in.
Fortunately, you brought a small deck of space cards!
You'd like to play a game of Combat, and there's even an opponent available:
a small crab that climbed aboard your raft before you left.

Fortunately, it doesn't take long to teach the crab the rules.

Before the game starts, split the cards so each player has their own deck (your puzzle input).
Then, the game consists of a series of rounds: both players draw their top card,
and the player with the higher-valued card wins the round.
The winner keeps both cards, placing them on the bottom of their own deck
so that the winner's card is above the other card.
If this causes a player to have all of the cards, they win, and the game ends.

For example, consider the following starting decks:

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

This arrangement means that player 1's deck contains 5 cards, with 9 on top and 1 on the bottom;
player 2's deck also contains 5 cards, with 5 on top and 10 on the bottom.

The first round begins with both players drawing the top card of their decks: 9
 and 5. Player 1 has the higher card, so both cards move to the bottom of player 1's deck such that 9 is above 5.
 In total, it takes 29 rounds before a player has all of the cards:

-- Round 1 --
Player 1's deck: 9, 2, 6, 3, 1
Player 2's deck: 5, 8, 4, 7, 10
Player 1 plays: 9
Player 2 plays: 5
Player 1 wins the round!

-- Round 2 --
Player 1's deck: 2, 6, 3, 1, 9, 5
Player 2's deck: 8, 4, 7, 10
Player 1 plays: 2
Player 2 plays: 8
Player 2 wins the round!

-- Round 3 --
Player 1's deck: 6, 3, 1, 9, 5
Player 2's deck: 4, 7, 10, 8, 2
Player 1 plays: 6
Player 2 plays: 4
Player 1 wins the round!

-- Round 4 --
Player 1's deck: 3, 1, 9, 5, 6, 4
Player 2's deck: 7, 10, 8, 2
Player 1 plays: 3
Player 2 plays: 7
Player 2 wins the round!

-- Round 5 --
Player 1's deck: 1, 9, 5, 6, 4
Player 2's deck: 10, 8, 2, 7, 3
Player 1 plays: 1
Player 2 plays: 10
Player 2 wins the round!

...several more rounds pass...

-- Round 27 --
Player 1's deck: 5, 4, 1
Player 2's deck: 8, 9, 7, 3, 2, 10, 6
Player 1 plays: 5
Player 2 plays: 8
Player 2 wins the round!

-- Round 28 --
Player 1's deck: 4, 1
Player 2's deck: 9, 7, 3, 2, 10, 6, 8, 5
Player 1 plays: 4
Player 2 plays: 9
Player 2 wins the round!

-- Round 29 --
Player 1's deck: 1
Player 2's deck: 7, 3, 2, 10, 6, 8, 5, 9, 4
Player 1 plays: 1
Player 2 plays: 7
Player 2 wins the round!


== Post-game results ==

Player 1's deck:
Player 2's deck: 3, 2, 10, 6, 8, 5, 9, 4, 7, 1

Once the game ends, you can calculate the winning player's score.
The bottom card in their deck is worth the value of the card multiplied by 1,
the second-from-the-bottom card is worth the value of the card multiplied by 2, and so on.
With 10 cards, the top card is worth the value on the card multiplied by 10. In this example,
the winning player's score is:

   3 * 10
+  2 *  9
+ 10 *  8
+  6 *  7
+  8 *  6
+  5 *  5
+  9 *  4
+  4 *  3
+  7 *  2
+  1 *  1
= 306

So, once the game ends, the winning player's score is 306.

Play the small crab in a game of Combat using the two decks you just dealt.
What is the winning player's score?

 */

data class CrabCombatGame(var decks: CrabCombatDecks) {
    val score: Int
        get() = calculateScore(decks.player1) + calculateScore(decks.player2)

    private fun calculateScore(player2: ArrayDeque<Int>): Int =
        player2.reversed().mapIndexed { index, cardValue ->  cardValue * (index + 1) }.sum()

    var round = 0
        private set
    var stopped = false
        private set
    fun playRound() {
        with(decks) {
            if (player1.isEmpty() || player2.isEmpty()) {
                stopped = true
                return
            }
            val card1 = player1.removeFirstOrNull()
            val card2 = player2.removeFirstOrNull()
            if (card1 == null || card2 == null) {
                stopped = true
                return
            }
            if (card1 > card2) {
                player1.addLast(card1)
                player1.addLast(card2)
            } else {
                player2.addLast(card2)
                player2.addLast(card1)
            }
            println("player1=$player1")
            println("player2=$player2")
        }
        round++
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

class Day22_Part1 : FunSpec({
    val decksString = """
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
    val decks = parseCrabCombatDecks(decksString)
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
            while (! game.stopped) game.playRound()
            test("should have played 29 round") {
                game.round shouldBe 29
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
    while (! game.stopped) game.playRound()
    val solution = game.score
    test("should have the expected score") {
        solution shouldBe 34566
    }
})
