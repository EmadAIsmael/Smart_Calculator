package calculator

import kotlin.math.pow
import kotlin.system.exitProcess

val vars = mutableMapOf<String, Int?>()

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
                    try {
                        doAssignment(input)
                    } catch (e: InvalidAssignmentException) {
                        println(e.message)
                    } catch (e: InvalidIdentifierException) {
                        println(e.message)
                    } catch (e: UnknownVariableException) {
                        println(e.message)
                    } catch (e: NumberFormatException) {
                        println(e.message)
                    }
                }
                isValidOneTerm(input) -> {
                    try {
                        println(tokenToVal(input))
                    } catch (e: UnknownVariableException) {
                        println(e.message)
                    }

                }
                isExpression(input) -> {
                    try {
                        val output = evaluate(input)
                        println(output)
                    } catch (e: InvalidExpressionException) {
                        println(e.message)
                    }
                }
                else -> println("Invalid expression")
            }
        }
    }
}

fun isValidOneTerm(inputString: String): Boolean {
    val validOneValue = """^[-+]?\s*\d+|[a-zA-Z]+$""".toRegex()
    return inputString.matches(validOneValue)
}

fun evaluate(inputString: String): Int? {
    // handle multiple +'s and / or -'s
    val oPlus = "\\+{2,}".toRegex()
    val oMinus = "-{2,}".toRegex()
    var tokensString = oPlus.replace(inputString, "+")
    tokensString = oMinus.replace(tokensString) { m ->
        if (m.value.length % 2 == 0) "+" else "-"
    }

    // handle multiple *'s, ^'s and / or /'s
    if ("""[/^*]{2,}""".toRegex().containsMatchIn(tokensString))
        throw InvalidExpressionException()

    val postfix = toPostfix(tokensString)       // param was inputString

    if (postfix.contains("("))
        throw InvalidExpressionException()

    return evaluatePostfix(postfix)
}

fun toPostfix(input: String): String {
    val opStack = Stack()
    var result = ""

    var tokensString = input
    tokensString.replace(" ", "")

    // delimit tokens with spaces
    val opRegex = """[-+*/^()]""".toRegex()
    tokensString = opRegex.replace(tokensString) { m -> " ${m.value} " }

    // tokenize
    val tokens = tokensString
        .split("\\s".toRegex()).toMutableList()
        .filter { it.isNotEmpty() }

    // process expression
    for (token in tokens) {
        // number or variable, add to result
        if (isValidOneTerm(token) || isValidVar(token))
            try {
                result += tokenToVal(token).toString() + " "
            } catch (e: UnknownVariableException) {
                println(e.message)
            }

        // stack is empty or stack top == "(", push operator to stack
        else if (isOperator(token)) {
            if (opStack.isEmpty() || opStack.peek() == "(")
                opStack.push(token)
            // operator.precedence > stack.top.precedence
            else if (Operator.findBySymbol(token).precedence >
                Operator.findBySymbol(opStack.peek().toString()).precedence
            )
                opStack.push(token)
            // incoming operator has lower or equal precedence compared to the top of the stack
            else if (Operator.findBySymbol(token).precedence <=
                Operator.findBySymbol(opStack.peek().toString()).precedence
            ) {
                while (opStack.isNotEmpty() && (
                            (Operator.findBySymbol(token).precedence <=
                                    Operator.findBySymbol(opStack.peek().toString()).precedence) &&
                                    opStack.peek() != "(")
                ) {
                    result += opStack.pop() + " "
                }
                // if (opStack.peek() == "(") opStack.pop()
                opStack.push(token)
            }
        }
        // incoming element is a left parenthesis, push it on the stack
        else if (token == "(") opStack.push(token)
        // incoming element is a right parenthesis,
        // pop the stack and add operators to the result until you see
        // a left parenthesis. Discard the pair of parentheses
        else if (token == ")") {
            while (opStack.isNotEmpty() && opStack.peek() != "(")
                result += opStack.pop() + " "
            if (opStack.peek() == "(") opStack.pop()               // discard "("
            else
                throw InvalidExpressionException()
        }
    }
    // expression is empty, pop all operator and add to result
    while (opStack.isNotEmpty())
        result += opStack.pop() + " "

    return result
}

fun evaluatePostfix(postfix: String): Int? {
    val calcStack = Stack()
    val pfTokens = postfix.split(" ").filter { it.isNotEmpty() }

    for (token in pfTokens) {
        if (isValidOneTerm(token) || isValidVar(token))
            try {
                calcStack.push(tokenToVal(token).toString())
            } catch (e: UnknownVariableException) {
                println(e.message)
            }

        else if (isOperator(token)) {
            val opObj = Operator.findBySymbol(token)
            val a = calcStack.pop()?.toInt()
            val b = calcStack.pop()?.toInt()

            var result = 0
            if (a != null && b != null)
                result = opObj.method.invoke(b, a)
            calcStack.push(result.toString())
        }
    }
    return calcStack.pop()?.toInt()
}

fun isOperator(op: String): Boolean = "+-/*^".contains(op)

fun tokenToVal(token: String): Int {
//    var newToken = ""
    if (token.startsWith("-")) {
        val newToken = token.substring(token.indexOf('-') + 1)
        return if (isValidVar(newToken)) -1 * vars[newToken]!! else -1 * newToken.toInt()
    }
//    return if (isValidVar(token)) vars[token] else token.toInt()
//    return if (isValidVar(token))
//        vars[token]!!
//    else if (isValidIdentifier(token))
//        throw UnknownVariableException()
//    else
//        token.toInt()
    return when {
        isValidVar(token) -> vars[token]!!
        isValidIdentifier(token) -> throw UnknownVariableException()
        else -> token.toInt()
    }
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

fun isValidIdentifier(varName: String): Boolean {
    return varName.matches("""[a-zA-Z]+""".toRegex())
}

fun isValidVar(identifier: String): Boolean {
    return vars.containsKey(identifier)
}

fun doAssignment(input: String) {
    val (identifier, rValue) = input.split("=").map { it.trim() }

    if (hasMultipleAssignments(input))
        throw InvalidAssignmentException()

    if (hasInvalidLvalue(input))
        throw InvalidIdentifierException()

    if (!isValidIdentifier(rValue) && !isAllNumeric(rValue))
        throw InvalidAssignmentException()

    if (!isValidVar(rValue) && !isAllNumeric(rValue))
        throw UnknownVariableException()

    if (isValidVar(rValue))
        vars[identifier] = vars[rValue]
    else
        vars[identifier] = rValue.toInt()
}

fun isAllNumeric(input: String): Boolean {
    return input.matches("""^[-+]?\s*\d+$""".toRegex())
}

fun hasMultipleAssignments(input: String): Boolean {
    return """=""".toRegex().findAll(input).count() > 1
}

fun hasInvalidLvalue(input: String): Boolean {
    val possibleAssignment = """=""".toRegex().findAll(input).count() == 1
    val lValue = input.split("=")[0].trim()
    return possibleAssignment && !isValidIdentifier(lValue)
}

class Stack {
    private val store = mutableListOf<String>()

    fun push(item: String?) {
        store.add(item.toString())
    }

    fun pop(): String? {
        return if (store.isNotEmpty()) {
            val i = store.last()
            store.removeAt(store.lastIndex)
            i
        } else
            null
    }

    fun peek(): String? {
        return if (store.isNotEmpty()) store.last() else null
    }

    fun isEmpty(): Boolean {
        return store.size == 0
    }

    fun isNotEmpty(): Boolean = !this.isEmpty()
}

class Queue {
    private val s1 = Stack()
    private val s2 = Stack()

    fun enqueue(e: String) {
        s1.push(e)
    }

    fun dequeue(): String? {
        if (s2.isEmpty())
            if (s1.isEmpty())
                throw EmptyQueueException()

        while (s1.isNotEmpty())
            s2.push(s1.pop())
        return s2.pop()
    }

    fun isEmpty(): Boolean {
        return s1.isEmpty() && s2.isEmpty()
    }

    fun isNotEmpty(): Boolean {
        return !this.isEmpty()
    }
}

enum class Operator(
    val symbol: String,
    val precedence: Int,
    val method: (Int, Int) -> Int
) {
    PLUS("+", 1, { a: Int, b: Int -> a + b }),
    MINUS("-", 1, { a: Int, b: Int -> a - b }),
    MULTIPLY("*", 2, { a: Int, b: Int -> a * b }),
    DIVIDE("/", 2, { a: Int, b: Int -> a / b }),
    POWER("^", 3, { a: Int, b: Int -> a.toDouble().pow(b.toDouble()).toInt() }),
    NULL("", 10, { a: Int, _: Int -> a });

    companion object {
        fun findBySymbol(symbol: String): Operator {
            for (enum in values()) {
                if (symbol == enum.symbol)
                    return enum
            }
            return NULL
        }
    }
}

class UnknownCommandException(message: String = "Unknown command") : Exception(message)
class InvalidExpressionException(message: String = "Invalid expression") : Exception(message)
class EmptyQueueException(message: String = "Empty queue exception") : Exception(message)
class UnknownVariableException(message: String = "Unknown variable") : Exception(message)
class InvalidAssignmentException(message: String = "Invalid assignment") : Exception(message)
class InvalidIdentifierException(message: String = "Invalid identifier") : Exception(message)
