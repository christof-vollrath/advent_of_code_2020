import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.lang.IllegalArgumentException

/*

--- Day 25: Combo Breaker ---

See https://adventofcode.com/2020/day/25

 */

fun handshakeOperation(loopSize: Long, subjectNumber: Long): Long {
    var value = 1L
    for(i in 0 until loopSize) {
        value = value * subjectNumber % 20201227L
    }
    return value
}

fun bruteForceEncryptionKey(cardsPublicKey: Long, doorsPublicKey: Long): Long {
    val cardsLoopSize = bruteForceLoopSize(cardsPublicKey)
    println("cardsLoopSize=$cardsLoopSize")
    val doorsLoopSize = bruteForceLoopSize(doorsPublicKey)
    println("doorsLoopSize=$doorsLoopSize")
    val encryptionKey1 = handshakeOperation(loopSize = cardsLoopSize, subjectNumber = doorsPublicKey)
    val encryptionKey2 = handshakeOperation(loopSize = doorsLoopSize, subjectNumber = cardsPublicKey)
    if (encryptionKey1 != encryptionKey2) throw IllegalStateException("Public key not unique $encryptionKey1 $encryptionKey2")
    return encryptionKey1
}

fun bruteForceLoopSize(publicKey: Long): Long {
    val subjectNumber = 7L
    var value = 1L
    for(i in  0L until 100_000_000L) {
        value = value * subjectNumber % 20201227L
        if (value == publicKey) return i+1
    }
    throw IllegalArgumentException("Could not find a loop size for publicKey=$publicKey")
}

class Day25_Part1 : FunSpec({

    context("handshake operation") {
        test("handshake operation with loop size 8 and subject number 7 produces 5764801") {
            handshakeOperation(loopSize = 8L, subjectNumber = 7L) shouldBe 5764801L
        }
        test("handshake operation with loop size 11 and subject number 7 produces 17807724") {
            handshakeOperation(loopSize = 11L, subjectNumber = 7L) shouldBe 17807724L
        }
        test("calculate encryption key by door's public key and cards loop size") {
            handshakeOperation(loopSize = 8L, subjectNumber = 17807724L) shouldBe 14897079L
        }
        test("calculate encryption key cards loop size by and door's public key") {
            handshakeOperation(loopSize = 11L, subjectNumber = 5764801L) shouldBe 14897079L
        }
    }
    context("brute force search for encryption key") {
        test("brute force loop size for card") {
            bruteForceLoopSize(5764801L) shouldBe 8
        }
        test("brute force loop size for door") {
            bruteForceLoopSize(17807724L) shouldBe 11
        }
        test("brute force public key") {
            bruteForceEncryptionKey(cardsPublicKey = 5764801L, doorsPublicKey = 17807724L) shouldBe 14897079L
        }
    }
})

class Day25_Part1_Exercise: FunSpec({
    val solution = bruteForceEncryptionKey(cardsPublicKey = 11239946L, doorsPublicKey = 10464955L)
    test("should have found the encryption key") {
        solution shouldBe 711945L
    }
})
