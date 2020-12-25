import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.lang.IllegalArgumentException

/*

--- Day 25: Combo Breaker ---

You finally reach the check-in desk.
Unfortunately, their registration systems are currently offline, and they cannot check you in.
Noticing the look on your face, they quickly add that tech support is already on the way!
They even created all the room keys this morning;
you can take yours now and give them your room deposit once the registration system comes back online.

The room key is a small RFID card.
Your room is on the 25th floor and the elevators are also temporarily out of service,
so it takes what little energy you have left to even climb the stairs and navigate the halls.
You finally reach the door to your room, swipe your card, and - beep - the light turns red.

Examining the card more closely, you discover a phone number for tech support.

"Hello! How can we help you today?" You explain the situation.

"Well, it sounds like the card isn't sending the right command to unlock the door.
If you go back to the check-in desk, surely someone there can reset it for you."
Still catching your breath, you describe the status of the elevator and the exact number of stairs you just had to climb.

"I see! Well, your only other option would be to reverse-engineer the cryptographic handshake
the card does with the door and then inject your own commands into the data stream,
but that's definitely impossible."
You thank them for their time.

Unfortunately for the door, you know a thing or two about cryptographic handshakes.

The handshake used by the card and the door involves an operation that transforms a subject number.
To transform a subject number, start with the value 1.
Then, a number of times called the loop size, perform the following steps:

Set the value to itself multiplied by the subject number.
Set the value to the remainder after dividing the value by 20201227.
The card always uses a specific, secret loop size when it transforms a subject number.
The door always uses a different, secret loop size.

The cryptographic handshake works like this:

The card transforms the subject number of 7 according to the card's secret loop size.
The result is called the card's public key.
The door transforms the subject number of 7 according to the door's secret loop size.
The result is called the door's public key.
The card and door use the wireless RFID signal to transmit the two public keys (your puzzle input)
to the other device.
Now, the card has the door's public key, and the door has the card's public key.
Because you can eavesdrop on the signal, you have both public keys, but neither device's loop size.

The card transforms the subject number of the door's public key according to the card's loop size.
The result is the encryption key.
The door transforms the subject number of the card's public key according to the door's loop size.
The result is the same encryption key as the card calculated.
If you can use the two public keys to determine each device's loop size,
you will have enough information to calculate the secret encryption key
that the card and door use to communicate;
this would let you send the unlock command directly to the door!

For example, suppose you know that the card's public key is 5764801.
With a little trial and error, you can work out that the card's loop size must be 8,
because transforming the initial subject number of 7 with a loop size of 8 produces 5764801.

Then, suppose you know that the door's public key is 17807724.
By the same process, you can determine that the door's loop size is 11,
because transforming the initial subject number of 7 with a loop size of 11 produces 17807724.

At this point, you can use either device's loop size with the other device's public key to calculate the encryption key.
Transforming the subject number of 17807724 (the door's public key) with a loop size of 8 (the card's loop size)
produces the encryption key, 14897079.
(Transforming the subject number of 5764801 (the card's public key) with a loop size of 11 (the door's loop size)
produces the same encryption key: 14897079.)

What encryption key is the handshake trying to establish?

To begin, get your puzzle input.
11239946
10464955

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
