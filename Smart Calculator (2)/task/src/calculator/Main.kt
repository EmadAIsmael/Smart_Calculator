package calculator

import kotlin.system.exitProcess

fun main() {
    while (true) {
        val input = readLine()!!
        if (input.isNotEmpty()) {
            when {
                isCommand(input) -> doCommand(input)
                isValidOneTerm(input) == true -> println(input.toInt())
                isValidArithmetic(input) -> println(evaluate(input))
                else -> println("Invalid expression")
            }
        }
    }
}

fun isValidOneTerm(inputString: String): Boolean {
    val validOneValue = """^[-+]?\d+$""".toRegex()
    val match = validOneValue.find(inputString)
    return match?.value?.isNotEmpty() == true
}

fun hasEndingOperator(inputString: String): Boolean {
    return """[-+]$""".toRegex().find(inputString)?.value?.isNotEmpty() == true
}

fun hasNoOperator(inputString: String): Boolean {
    return """\d+\s+\d+""".toRegex().find(inputString)?.value?.isNotEmpty() == true
}

fun isValidArithmetic(inputString: String): Boolean {
//    val validExp = """(([-+]?\d+)(\s*(\++|-+)\s*([-+]*\d+[^-+])\s*)+)+[^-+]$""".toRegex()

    val operator = """[-+]+"""
    val operand = """\d+"""
    val sign = """[-+]?"""
    val term = """$sign?${operand}"""
    val validExp = """${term}(\s*${operator}\s*${term})*"""

    val match = validExp.toRegex().find(inputString)
    return (!hasEndingOperator(inputString)) &&
            !hasNoOperator(inputString) &&
            (match?.value?.isNotEmpty() == true)
}

fun evaluate(inputString: String): Int {
    val input = inputString.split("\\s+".toRegex()).toMutableList()
    val ops = mapOf("+" to { a: Int, b: Int -> a + b }, "-" to { a: Int, b: Int -> a - b })
    var op = ""
    // step through input values
    // to check operator
    for (i in 0..input.lastIndex - 2 step 2) {
        // if operator is "+"
        if (input[i + 1].all { it == '+' })
            op = "+"
        // if operator is "-", odd number of "-"'s is "-", else "+".
        else if (input[i + 1].all { it == '-' })
            op = if (input[i + 1].length % 2 != 0) "-" else "+"

        val a = input[i].toInt()
        val b = input[i + 2].toInt()
        input[i + 2] = ops[op]?.invoke(a, b).toString()
    }
    return input[input.lastIndex].toInt()
}

fun isCommand(cmd: String): Boolean {
    return cmd[0] == '/'
}

fun doCommand(cmd: String) {

    when (cmd) {
        "/exit" -> {
            println("Bye!")
            exitProcess(0)
        }
        "/help" -> {
            println("The program calculates the sum/difference of numbers")
        }
        else -> {
            println("Unknown command")
        }
    }
}
