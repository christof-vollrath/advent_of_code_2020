import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/*
--- Day 21: Allergen Assessment ---

You reach the train's last stop and the closest you can get to your vacation island without getting wet.
There aren't even any boats here, but nothing can stop you now: you build a raft.
You just need a few days' worth of food for your journey.

You don't speak the local language, so you can't read any ingredients lists.
However, sometimes, allergens are listed in a language you do understand.
You should be able to use this information to determine which ingredient contains which allergen
and work out which foods are safe to take with you on your trip.

You start by compiling a list of foods (your puzzle input), one food per line.
Each line includes that food's ingredients list followed by some or all of the allergens the food contains.

Each allergen is found in exactly one ingredient. Each ingredient contains zero or one allergen.
Allergens aren't always marked; when they're listed (as in (contains nuts, shellfish) after an ingredients list),
the ingredient that contains each listed allergen will be somewhere in the corresponding ingredients list.
However, even if an allergen isn't listed, the ingredient that contains that allergen could still be present:
maybe they forgot to label it, or maybe it was labeled in a language you don't know.

For example, consider the following list of foods:

mxmxvkd kfcds sqjhc nhms (contains dairy, fish)
trh fvjkl sbzzf mxmxvkd (contains dairy)
sqjhc fvjkl (contains soy)
sqjhc mxmxvkd sbzzf (contains fish)

The first food in the list has four ingredients (written in a language you don't understand):
mxmxvkd, kfcds, sqjhc, and nhms.
While the food might contain other allergens, a few allergens the food definitely contains are listed afterward:
dairy and fish.

The first step is to determine which ingredients can't possibly contain any of the allergens in any food in your list.
In the above example, none of the ingredients kfcds, nhms, sbzzf, or trh can contain an allergen.
Counting the number of times any of these ingredients appear in any ingredients list produces 5:
they all appear once each except sbzzf, which appears twice.

Determine which ingredients cannot possibly contain any of the allergens in your list.
How many times do any of those ingredients appear?

--- Part Two ---

Now that you've isolated the inert ingredients,
you should have enough information to figure out which ingredient contains which allergen.

In the above example:

mxmxvkd contains dairy.
sqjhc contains fish.
fvjkl contains soy.

Arrange the ingredients alphabetically by their allergen
and separate them by commas to produce your canonical dangerous ingredient list.
(There should not be any spaces in your canonical dangerous ingredient list.)
In the above example, this would be mxmxvkd,sqjhc,fvjkl.

Time to stock your raft with supplies. What is your canonical dangerous ingredient list?

 */


fun List<FoodInformation>.findIngredientsWithoutAllergens(): List<String> {
    val allergensUniqueInIngredients = findAllergensUniqueInIngredients()
    val ingredientsWithAllergens = allergensUniqueInIngredients.values.toSet()
    return flatMap { foodInformation ->
        foodInformation.ingredients.filter {
            it !in ingredientsWithAllergens
        }
    }
}

fun List<FoodInformation>.findAllergensUniqueInIngredients(): Map<String, String> {
    val allAllergens = getAllAllergens()
    val allergensInIngredients = findAllergensInIngredients(allAllergens)
    val result = mutableMapOf<String, String>()
    val alreadyFoundIngredient = mutableSetOf<String>()
    val allergensAlreadyChecked = mutableSetOf<String>()
    while((allAllergens - allergensAlreadyChecked).isNotEmpty()) {
        var somethingFound = false
        allAllergens.map { allergen ->
            if (allergen !in allergensAlreadyChecked) {
                val ingredients = allergensInIngredients[allergen]!!
                val remainingIngredients = ingredients - alreadyFoundIngredient
                if (remainingIngredients.isEmpty()) {
                    println("No ingredients for allergen=$allergen")
                } else if (remainingIngredients.size == 1) {
                    val ingredient = remainingIngredients.first()
                    result[allergen] = ingredient
                    alreadyFoundIngredient += ingredient
                    allergensAlreadyChecked += allergen
                    somethingFound = true
                }
            }
        }
        if (! somethingFound) {
            println("Nothing more found, remaining allergens=${allAllergens - allergensAlreadyChecked}")
            break
        }
    }
    return result
}

fun List<FoodInformation>.findAllergensInIngredients(allAllergens: Set<String>): Map<String, Set<String>> =
    allAllergens.map { allergen ->
        val ingredients = filter { foodInformation ->
            foodInformation.allergens.contains(allergen)
        }
            .map { it.ingredients}
            .reduce { acc, ingredients ->  acc.intersect(ingredients)}
        allergen to ingredients
    }.toMap()

fun List<FoodInformation>.getAllAllergens(): Set<String> = flatMap { foodInformation ->
    foodInformation.allergens
}.toSet()

fun parseFoodLines(foodLinesString: String): List<FoodInformation> =
    foodLinesString.split("\n").map { parseIngredients(it) }

fun parseIngredients(ingredientsString: String): FoodInformation {
    val parts = ingredientsString.split("(contains")
    val ingredients = parts[0].split(" ")
        .filter { it.isNotBlank() }.map { it.trim() }.toSet()
    val allergens = parts[1].dropLast(1).split(", ")
        .filter { it.isNotBlank() }.map { it.trim() }.toSet()
    return FoodInformation(ingredients, allergens)
}

data class FoodInformation(val ingredients: Set<String>, val allergens: Set<String>)

fun List<FoodInformation>.ingredientsSortedByAllergens(): List<String> {
    val allergensUniqueInIngredients = findAllergensUniqueInIngredients()
    return allergensUniqueInIngredients.entries.sortedBy { it.key }.map { it.value }
}

class Day21_Part1 : FunSpec({
    context("parse ingredients") {
        val foodInformationString = "mxmxvkd kfcds sqjhc nhms (contains dairy, fish)"
        val foodInformation = parseIngredients(foodInformationString)
        test("should have parsed incredients and allergenes for one food line") {
            foodInformation.ingredients shouldBe setOf("mxmxvkd", "kfcds", "sqjhc", "nhms")
            foodInformation.allergens shouldBe setOf("dairy", "fish")
        }
    }
    val foodLinesString = """
        mxmxvkd kfcds sqjhc nhms (contains dairy, fish)
        trh fvjkl sbzzf mxmxvkd (contains dairy)
        sqjhc fvjkl (contains soy)
        sqjhc mxmxvkd sbzzf (contains fish)
        """.trimIndent()
    val foodLines = parseFoodLines(foodLinesString)
    context("parse food lines") {
        test("should have parsed all food lines") {
            foodLines.size shouldBe 4
            foodLines[3] shouldBe FoodInformation(setOf("sqjhc", "mxmxvkd", "sbzzf"), setOf("fish"))
        }
    }
    context("get all allergens") {
        val allAllergens = foodLines.getAllAllergens()
        allAllergens shouldBe setOf("dairy", "fish", "soy")
        context("find ingredients with allergens") {
            val allergensInIngredients = foodLines.findAllergensInIngredients(allAllergens)
            allergensInIngredients shouldBe mapOf(
                "dairy" to setOf("mxmxvkd"),
                "fish" to setOf("mxmxvkd", "sqjhc"),
                "soy" to setOf("sqjhc", "fvjkl"),
            )
        }
    }
    context("ingredient with allergen") {
        val allergensUniqueInIngredients = foodLines.findAllergensUniqueInIngredients()
        allergensUniqueInIngredients shouldBe mapOf(
            "dairy" to "mxmxvkd",
            "fish" to "sqjhc",
            "soy" to "fvjkl",
        )
    }
    context("ingredients without allergen") {
        val withoutAllergen = foodLines.findIngredientsWithoutAllergens()
        withoutAllergen shouldBe listOf("kfcds", "nhms", "trh", "sbzzf", "sbzzf")
        withoutAllergen.size shouldBe 5
    }
})

class Day21_Part1_Exercise: FunSpec({
    val input = readResource("day21Input.txt")!!
    val foodLines = parseFoodLines(input)
    val allergensUniqueInIngredients = foodLines.findAllergensUniqueInIngredients()
    test("should have found ingredients for all allergens") {
        allergensUniqueInIngredients.keys.toSet() shouldBe foodLines.getAllAllergens()
    }
    val withoutAllergen = foodLines.findIngredientsWithoutAllergens()
    val solution = withoutAllergen.size
    test("should have found the right number of ingredients without allergens") {
        solution shouldBe 1679
    }
})

class Day21_Part2: FunSpec({
    val foodLinesString = """
        mxmxvkd kfcds sqjhc nhms (contains dairy, fish)
        trh fvjkl sbzzf mxmxvkd (contains dairy)
        sqjhc fvjkl (contains soy)
        sqjhc mxmxvkd sbzzf (contains fish)
        """.trimIndent()
    val foodLines = parseFoodLines(foodLinesString)
    val ingredientsWithAllergens = foodLines.ingredientsSortedByAllergens()
    val ingredientsWithAllergensString = ingredientsWithAllergens.joinToString(",")
    test("should have created the correct ingredients string") {
        ingredientsWithAllergensString shouldBe "mxmxvkd,sqjhc,fvjkl"
    }
})

class Day21_Part2_Exercise: FunSpec({
    val input = readResource("day21Input.txt")!!
    val foodLines = parseFoodLines(input)
    val ingredientsWithAllergens = foodLines.ingredientsSortedByAllergens()
    val ingredientsWithAllergensString = ingredientsWithAllergens.joinToString(",")
    test("should have created the correct ingredients string") {
        ingredientsWithAllergensString shouldBe "lmxt,rggkbpj,mxf,gpxmf,nmtzlj,dlkxsxg,fvqg,dxzq"
    }
})

