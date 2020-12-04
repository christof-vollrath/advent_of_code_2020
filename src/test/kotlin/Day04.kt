import io.kotest.core.datatest.forAll
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/*
--- Day 4: Passport Processing ---

You arrive at the airport only to realize that you grabbed your North Pole Credentials instead of your passport.
While these documents are extremely similar, North Pole Credentials aren't issued by a country
and therefore aren't actually valid documentation for travel in most of the world.

It seems like you're not the only one having problems, though;
a very long line has formed for the automatic passport scanners, and the delay could upset your travel itinerary.

Due to some questionable network security,
you realize you might be able to solve both of these problems at the same time.

The automatic passport scanners are slow because they're having trouble detecting
which passports have all required fields.

The expected fields are as follows:

byr (Birth Year)
iyr (Issue Year)
eyr (Expiration Year)
hgt (Height)
hcl (Hair Color)
ecl (Eye Color)
pid (Passport ID)
cid (Country ID)

Passport data is validated in batch files (your puzzle input).
Each passport is represented as a sequence of key:value pairs separated by spaces or newlines. Passports are separated by blank lines.

Here is an example batch file containing four passports:

ecl:gry pid:860033327 eyr:2020 hcl:#fffffd
byr:1937 iyr:2017 cid:147 hgt:183cm

iyr:2013 ecl:amb cid:350 eyr:2023 pid:028048884
hcl:#cfa07d byr:1929

hcl:#ae17e1 iyr:2013
eyr:2024
ecl:brn pid:760753108 byr:1931
hgt:179cm

hcl:#cfa07d eyr:2025 pid:166559648
iyr:2011 ecl:brn hgt:59in

The first passport is valid - all eight fields are present.
The second passport is invalid - it is missing hgt (the Height field).

The third passport is interesting; the only missing field is cid,
so it looks like data from North Pole Credentials, not a passport at all!
Surely, nobody would mind if you made the system temporarily ignore missing cid fields.
Treat this "passport" as valid.

The fourth passport is missing two fields, cid and byr.
Missing cid is fine, but missing any other field is not, so this passport is invalid.

According to the above rules, your improved system would report 2 valid passports.

Count the number of valid passports - those that have all required fields.
Treat cid as optional. In your batch file, how many passports are valid?

 */

fun parsePassportStrings(passportsString: String): List<String> =
    passportsString.split("""\n\s*\n""".toRegex())

fun countValidPassports(passportStrings: List<String>) = passportStrings.map {
    parsePassport(it)
}
    .filter { it.check() }
    .count()
fun Map<String, String>.check() =
    keys.toSet().intersect(setOf(
        "byr",
        "iyr",
        "eyr",
        "hgt",
        "hcl",
        "ecl",
        "pid"
    )).size == 7

fun parsePassport(input: String): Map<String, String> {
    val pattern = """\s*([a-z]+):\s*(\S+)""".toPattern()
    val matcher = pattern.matcher(input)
    return sequence {
        while(matcher.find()) {
            yield(matcher.group(1) to matcher.group(2))
        }
    }.toMap()
}

class Day04_Part1 : FunSpec({
    context("parse passport") {
        val passportString = """
            ecl:gry pid:860033327 eyr:2020 hcl:#fffffd
            byr:1937 iyr:2017 cid:147 hgt:183cm
            """.trimIndent()
        test("should be parsed correctly") {
            val passport = parsePassport(passportString)
            passport["ecl"] shouldBe "gry"
            passport["hcl"] shouldBe "#fffffd"
        }
    }
    context("check passport") {
        data class CheckPassportTestCase(val passportString: String, val expected: Boolean)
        forAll(
            CheckPassportTestCase("""
                ecl:gry pid:860033327 eyr:2020 hcl:#fffffd
                byr:1937 iyr:2017 cid:147 hgt:183cm                
            """.trimIndent(), true),
            CheckPassportTestCase("""
                iyr:2013 ecl:amb cid:350 eyr:2023 pid:028048884
                hcl:#cfa07d byr:1929
            """.trimIndent(), false),
            CheckPassportTestCase("""
                hcl:#ae17e1 iyr:2013
                eyr:2024
                ecl:brn pid:760753108 byr:1931
                hgt:179cm
            """.trimIndent(), true),
            CheckPassportTestCase("""
                hcl:#cfa07d eyr:2025 pid:166559648
                iyr:2011 ecl:brn hgt:59in
            """.trimIndent(), false),
        ) { (passportString, expected) ->
            val result = parsePassport(passportString).check()
            result shouldBe expected
        }

    }
    context("count valid passports") {
        val passportsString = """
            ecl:gry pid:860033327 eyr:2020 hcl:#fffffd
            byr:1937 iyr:2017 cid:147 hgt:183cm
            
            iyr:2013 ecl:amb cid:350 eyr:2023 pid:028048884
            hcl:#cfa07d byr:1929
            
            hcl:#ae17e1 iyr:2013
            eyr:2024
            ecl:brn pid:760753108 byr:1931
            hgt:179cm
            
            hcl:#cfa07d eyr:2025 pid:166559648
            iyr:2011 ecl:brn hgt:59in
        """.trimIndent()
        context("parse passports") {
            val passportStrings = parsePassportStrings(passportsString)
            test("should have found four passport strings") {
                passportStrings.size shouldBe 4
            }
        }
        context("count valid passports") {
            val count = countValidPassports(parsePassportStrings(passportsString))
            count shouldBe 2
        }
    }
})

class Day04_Part1_Excercise: FunSpec({
    val input = readResource("day04Input.txt")!!
    val count = countValidPassports(parsePassportStrings(input))
    test("solution") {
        count shouldBe 230
    }
})
