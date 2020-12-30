package calculator

import kotlin.math.pow
import kotlin.system.exitProcess

val vars = mutableMapOf<String, Int?>()
//val cmd = "(/help)|(/exit)".toRegex()

fun main() {

    while (true) {
        val input = readLine()!!
        if (input.isNotEmpty()) {
            when {
                isCommand(input) -> {
                    try {
                        doCommand(input)
                    } catch (e: UnknownCommandException) {
                        println(e.message)
                    }
                }
                isAssignment(input) -> {
                    if (hasMultipleAssignments(input)) println("Invalid assignment")
                    if (hasInvalidLvalue(input)) println("Invalid identifier")
                    if (hasInvalidRvalue(input)) {
                        if (isValidIdentifier(getRValue(input)) == true &&
                            !isValidVar(getRValue(input))
                        ) {
                            println("Unknown variable")
                        } else
                            println("Invalid assignment")
                    }
                    if (isValidAssignment(input)) doAssignment(input)
                }
                isExpression(input) -> {
                    val output = evaluate(input)
                    if (output != Unit)
                        println(output)
                }
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

//fun hasEndingOperator(inputString: String): Boolean {
//    return """[-+]$""".toRegex().find(inputString)?.value?.isNotEmpty() == true
//}

//fun hasNoOperator(inputString: String): Boolean {
//    return """\d+\s+\d+""".toRegex().find(inputString)?.value?.isNotEmpty() == true
//}

//fun isValidArithmetic(inputString: String): Boolean {
//
//    val operator = """[-+]+"""
//    val operand = """\d+"""
//    val sign = """[-+]?"""
//    val term = """$sign?${operand}"""
//    val validExp = """${term}(\s*${operator}\s*${term})*"""
//
//    val match = validExp.toRegex().find(inputString)
//    return (!hasEndingOperator(inputString)) &&
//            !hasNoOperator(inputString) &&
//            (match?.value?.isNotEmpty() == true)
//}

//fun evaluate(inputString: String): Any {
//    val input = inputString.split("\\s+".toRegex()).toMutableList()
//    val ops = mapOf(
//        "+" to { a: Int, b: Int -> a + b },
//        "-" to { a: Int, b: Int -> a - b },
//        "*" to { a: Int, b: Int -> a * b },
//        "/" to { a: Int, b: Int -> a / b },
//        "^" to { a: Int, b: Int -> Math.pow(a.toDouble(), b.toDouble()).toInt() }
//    )
//    var op = ""
//    // step through input values
//    // to check operator
//    for (i in 0..input.lastIndex - 2 step 2) {
//        // if operator is "+"
//        if (input[i + 1].all { it == '+' })
//            op = "+"
//        // if operator is "-", odd number of "-"'s is "-", else "+".
//        else if (input[i + 1].all { it == '-' })
//            op = if (input[i + 1].length % 2 != 0) "-" else "+"
//
//        val a = tokenToVal(input[i])
//        val b = tokenToVal(input[i + 2])
//        input[i + 2] = ops[op]?.invoke(a, b).toString()
//    }
//    return try {
//        val output = input[input.lastIndex].toInt()
//        output
//    } catch (e: NumberFormatException) {
//        val v = input[input.lastIndex]
//        if (isValidVar(v)) tokenToVal(v)
//        else println("Unknown variable")
//    }
//}

fun evaluate(inputString: String): Any {
    val input = inputString.split("\\s+".toRegex()).toMutableList()
    val ops = mapOf(
        "+" to { a: Int, b: Int -> a + b },
        "-" to { a: Int, b: Int -> a - b },
        "*" to { a: Int, b: Int -> a * b },
        "/" to { a: Int, b: Int -> a / b },
        "^" to { a: Int, b: Int -> a.toDouble().pow(b.toDouble()).toInt() }
    )
    val postfix = toPostfix(inputString)

//    var op = ""
//    // step through input values
//    // to check operator
//    for (i in 0..input.lastIndex - 2 step 2) {
//        // if operator is "+"
//        if (input[i + 1].all { it == '+' })
//            op = "+"
//        // if operator is "-", odd number of "-"'s is "-", else "+".
//        else if (input[i + 1].all { it == '-' })
//            op = if (input[i + 1].length % 2 != 0) "-" else "+"
//
//        val a = tokenToVal(input[i])
//        val b = tokenToVal(input[i + 2])
//        input[i + 2] = ops[op]?.invoke(a, b).toString()
//    }
//    return try {
//        val output = input[input.lastIndex].toInt()
//        output
//    } catch (e: NumberFormatException) {
//        val v = input[input.lastIndex]
//        if (isValidVar(v)) tokenToVal(v)
//        else println("Unknown variable")
//    }

    return 0
}

fun toPostfix(input: String) {
    val opStack = Stack()
    var result = ""

    // handle multiple +'s and / or -'s
    val oPlus = "\\+{2,}".toRegex()
    val oMinus = "\\-{2,}".toRegex()
    var tokensString = oPlus.replace(input, "+")
    tokensString = oMinus.replace(input) { m ->
        if (m.value.length % 2 == 0) "+" else "-"
    }
    tokensString.replace(" ", "")

    // delimit tokens with spaces
    val opRegex = """[-+*/^()]""".toRegex()
    tokensString = opRegex.replace(input) { m -> "${m.value} " }

    // tokenize
    val tokens = tokensString.split("\\s".toRegex()).toMutableList()
        .filter { it.isNotEmpty() }
    println(tokens)
    for (token in tokens) {
        if (isValidOneTerm(token) || isValidVar(token))
            result += tokenToVal(token).toString()
        else if (opStack.isEmpty() || opStack.peek() == "(")
            opStack.push(token)
        else if (Operator.findBySymbol(token).precedence >
            Operator.findBySymbol(opStack.peek().toString()).precedence
        )
            opStack.push(token)
        else if (Operator.findBySymbol(token).precedence <=
            Operator.findBySymbol(opStack.peek().toString()).precedence
        ) {
            while ((Operator.findBySymbol(token).precedence <=
                        Operator.findBySymbol(opStack.peek().toString()).precedence) ||
                opStack.peek() != "("
            ) {
                result += opStack.pop()
            }
            if (opStack.peek() == "(") opStack.pop()
        } else if (token == "(") opStack.push(token)
        else if (token == ")") {
            while (opStack.peek() != "(")
                result += opStack.pop()
            opStack.pop()               // discard "("
        }
    }
    while (!opStack.isEmpty())
        result += opStack.pop()

    println(result)
}

fun tokenToVal(token: String): Int {
    return if (isValidVar(token)) vars[token]!! else token.toInt()
}

fun isAssignment(input: String): Boolean {
    return input.split("=").size > 1
}

fun isExpression(input: String): Boolean {
    val op = "[-+/*^]+"
    val expr = "^(\\w+)(\\s*$op\\s*\\w+)*$"
    val withoutParenthesis = input.replace("[()]".toRegex(), "")
    return withoutParenthesis.matches(expr.toRegex())
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
        "/listvars" -> {
            for ((k, v) in vars) {
                println("$k = $v")
            }
        }
        else -> throw UnknownCommandException()
    }
}

fun isValidIdentifier(varName: String?): Boolean? {
    return varName?.matches("""[a-zA-Z]+""".toRegex())
}

fun isValidVar(identifier: String): Boolean {
    return vars.containsKey(identifier)
}

fun isValidAssignment(assignment: String): Boolean {
    return if (assignment.split("=").size == 2) {
        val (lValue, rValue) = assignment.split("=").map { it.trim() }
        isValidIdentifier(lValue) == true &&
                (isValidVar(rValue) || isValidOneTerm(rValue))
    } else
        false
}

fun doAssignment(input: String) {
    val (identifier, mapping) = input.split("=").map { it.trim() }

    if (isValidIdentifier(mapping) == true && isValidVar(mapping))
        vars[identifier] = vars[mapping]
    else
        vars[identifier] = mapping.toInt()
}

fun hasMultipleAssignments(input: String): Boolean {
    return """=""".toRegex().findAll(input).count() > 1
}

fun hasInvalidLvalue(input: String): Boolean {
    val possibleAssignment = """=""".toRegex().findAll(input).count() == 1
    val lValue = input.split("=")[0].trim()
    return possibleAssignment && !isValidIdentifier(lValue)!!
}

fun hasInvalidRvalue(input: String): Boolean {
    val possibleAssignment = """=""".toRegex().findAll(input).count() == 1
    return if (possibleAssignment) {

        val rValue = getRValue(input)
        possibleAssignment &&
                !(isValidVar(rValue) ||
                        isValidOneTerm(rValue))
    } else
        false
}

fun getRValue(input: String): String {
    return input.split("=")[1].trim()
}

class Stack() {
    private val store = mutableListOf<String>()

    fun push(item: String) {
        store.add(item)
    }

    fun pop(): String {
        val i = store.last()
        return i
    }

    fun peek(): String? {
        return if (store.isNotEmpty()) store.last() else null
    }

    fun isEmpty(): Boolean {
        return store.size == 0
    }
}

enum class Operator(val symbol: String, val precedence: Int) {
    PLUS("+", 1),
    MINUS("-", 1),
    MULTIPLY("*", 2),
    DIVIDE("/", 2),
    POWER("^", 3),
    NULL("", 0);

    companion object {
        fun findBySymbol(symbol: String): Operator {
            for (enum in Operator.values()) {
                if (symbol == enum.symbol)
                    return enum
            }
            return NULL
        }
    }
}

class UnknownCommandException(message: String = "Unknown command") : Exception(message)
