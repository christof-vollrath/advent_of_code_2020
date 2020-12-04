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

--- Part Two ---

The line is moving more quickly now, but you overhear airport security talking
about how passports with invalid data are getting through. Better add some data validation, quick!

You can continue to ignore the cid field,
but each other field has strict rules about what values are valid for automatic validation:

byr (Birth Year) - four digits; at least 1920 and at most 2002.
iyr (Issue Year) - four digits; at least 2010 and at most 2020.
eyr (Expiration Year) - four digits; at least 2020 and at most 2030.
hgt (Height) - a number followed by either cm or in:
If cm, the number must be at least 150 and at most 193.
If in, the number must be at least 59 and at most 76.
hcl (Hair Color) - a # followed by exactly six characters 0-9 or a-f.
ecl (Eye Color) - exactly one of: amb blu brn gry grn hzl oth.
pid (Passport ID) - a nine-digit number, including leading zeroes.
cid (Country ID) - ignored, missing or not.

Your job is to count the passports where all required fields are both present
and valid according to the above rules.
Here are some example values:

byr valid:   2002
byr invalid: 2003

hgt valid:   60in
hgt valid:   190cm
hgt invalid: 190in
hgt invalid: 190

hcl valid:   #123abc
hcl invalid: #123abz
hcl invalid: 123abc

ecl valid:   brn
ecl invalid: wat

pid valid:   000000001
pid invalid: 0123456789

Here are some invalid passports:

eyr:1972 cid:100
hcl:#18171d ecl:amb hgt:170 pid:186cm iyr:2018 byr:1926

iyr:2019
hcl:#602927 eyr:1967 hgt:170cm
ecl:grn pid:012533040 byr:1946

hcl:dab227 iyr:2012
ecl:brn hgt:182cm pid:021572410 eyr:2020 byr:1992 cid:277

hgt:59cm ecl:zzz
eyr:2038 hcl:74454a iyr:2023
pid:3556412378 byr:2007

Here are some valid passports:

pid:087499704 hgt:74in ecl:grn iyr:2012 eyr:2030 byr:1980
hcl:#623a2f

eyr:2029 ecl:blu cid:129 byr:1989
iyr:2014 pid:896056539 hcl:#a97842 hgt:165cm

hcl:#888785
hgt:164cm byr:2001 iyr:2015 cid:88
pid:545766238 ecl:hzl
eyr:2022

iyr:2010 hgt:158cm hcl:#b6652a ecl:blu byr:1944 eyr:2021 pid:093154719

Count the number of valid passports - those that have all required fields and valid values.
Continue to treat cid as optional.
In your batch file, how many passports are valid?


 */

fun parsePassportStrings(passportsString: String): List<String> =
    passportsString.split("""\n\s*\n""".toRegex())

fun countCheckedPassports(passportStrings: List<String>) = passportStrings.map {
        parsePassport(it)
    }
    .filter { it.check() }
    .count()

fun countValidPassports(passportStrings: List<String>) = passportStrings.map {
        parsePassport(it)
    }
    .filter { it.check() && it.validate() }
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

fun Map<String, String>.validate() = keys.subtract(setOf("cid")).all { key ->
    val validator = validators[key] ?: { _: String -> false }
    val value = this[key]
    if (value == null) {
        println("Error: no validator for $key")
        return false
    }
    val result = validator(value)
    if (!result) println("Error: validator $key could not validate $value")
    result
}

fun parsePassport(input: String): Map<String, String> {
    val pattern = """\s*([a-z]+):\s*(\S+)""".toPattern()
    val matcher = pattern.matcher(input)
    return sequence {
        while(matcher.find()) {
            yield(matcher.group(1) to matcher.group(2))
        }
    }.toMap()
}

val validators: Map<String, (String)->Boolean> = mapOf(
    "byr" to { input ->
        val n = input.toIntOrNull()
        n != null && 1920 <= n && n <= 2002
    },
    "iyr" to { input ->
        val n = input.toIntOrNull()
        n != null && 2010 <= n && n <= 2020
    },
    "eyr" to { input ->
        val n = input.toIntOrNull()
        n != null && 2020 <= n && n <= 2030
    },
    "hgt" to { input ->
        when {
            input.endsWith("cm") -> {
                val n = input.removeSuffix("cm").toIntOrNull()
                n != null && 150 <= n && n <= 193
            }
            input.endsWith("in") -> {
                val n = input.removeSuffix("in").toIntOrNull()
                n != null && 59 <= n && n <= 76
            }
            else -> false
        }
    },
    "hcl" to { input ->
        val regex = """^#[0-9a-f]{6}$""".toRegex()
        regex.find(input) != null
    },
    "ecl" to { input ->
        input in setOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth")
    },
    "pid" to { input ->
        val regex = """^[0-9]{9}$""".toRegex()
        regex.matches(input)
    },
)

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
            val count = countCheckedPassports(parsePassportStrings(passportsString))
            count shouldBe 2
        }
    }
})

class Day04_Part1_Excercise: FunSpec({
    val input = readResource("day04Input.txt")!!
    val count = countCheckedPassports(parsePassportStrings(input))
    test("solution") {
        count shouldBe 230
    }
})

class Day04_Part2 : FunSpec({
    context("validators") {
        validators["byr"]!!("1920") shouldBe true
        validators["byr"]!!("1900") shouldBe false

        validators["iyr"]!!("2010") shouldBe true
        validators["iyr"]!!("2009") shouldBe false

        validators["eyr"]!!("2030") shouldBe true
        validators["eyr"]!!("2031") shouldBe false

        validators["hgt"]!!("150cm") shouldBe true
        validators["hgt"]!!("149cm") shouldBe false
        validators["hgt"]!!("76in") shouldBe true
        validators["hgt"]!!("77in") shouldBe false

        validators["hcl"]!!("#0123ef") shouldBe true
        validators["hcl"]!!("#0123ex") shouldBe false
        validators["hcl"]!!("#0123efX") shouldBe false

        validators["ecl"]!!("amb") shouldBe true
        validators["ecl"]!!("xyz") shouldBe false

        validators["pid"]!!("000000001") shouldBe true
        validators["pid"]!!("0123456789") shouldBe false
    }

    context("validate passports") {
        val invalidPassportsString = """
            eyr:1972 cid:100
            hcl:#18171d ecl:amb hgt:170 pid:186cm iyr:2018 byr:1926
            
            iyr:2019
            hcl:#602927 eyr:1967 hgt:170cm
            ecl:grn pid:012533040 byr:1946
            
            hcl:dab227 iyr:2012
            ecl:brn hgt:182cm pid:021572410 eyr:2020 byr:1992 cid:277
            
            hgt:59cm ecl:zzz
            eyr:2038 hcl:74454a iyr:2023
            pid:3556412378 byr:2007            
        """.trimIndent()
        test("all passports should be invalid") {
            val invalidPassportStrings = parsePassportStrings(invalidPassportsString)
            invalidPassportStrings.size shouldBe 4
            countValidPassports(invalidPassportStrings) shouldBe 0
        }
        val validPassportsString = """
            pid:087499704 hgt:74in ecl:grn iyr:2012 eyr:2030 byr:1980
            hcl:#623a2f

            eyr:2029 ecl:blu cid:129 byr:1989
            iyr:2014 pid:896056539 hcl:#a97842 hgt:165cm

            hcl:#888785
            hgt:164cm byr:2001 iyr:2015 cid:88
            pid:545766238 ecl:hzl
            eyr:2022

            iyr:2010 hgt:158cm hcl:#b6652a ecl:blu byr:1944 eyr:2021 pid:093154719
        """.trimIndent()
        test("all passports should be valid") {
            val validPassportStrings = parsePassportStrings(validPassportsString)
            validPassportStrings.size shouldBe 4
            countValidPassports(validPassportStrings) shouldBe 4
        }
    }
})

class Day04_Part2_Excercise: FunSpec({
    val input = readResource("day04Input.txt")!!
    val count = countValidPassports(parsePassportStrings(input))
    test("solution") {
        count shouldBe 156
    }
})
