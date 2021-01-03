package calculator

import kotlin.math.pow
import kotlin.system.exitProcess
import java.math.BigInteger

val vars = mutableMapOf<String, BigInteger?>()

fun main() {

    while (true) {
        val input = readLine()!!.trim()
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

fun evaluate(inputString: String): BigInteger? {
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

    // handle number with spaces
    if ("""\d+\s+\d+""".toRegex().containsMatchIn(tokensString))
        throw InvalidExpressionException()

    val postfix = toPostfix(tokensString)

    if (postfix.contains("("))
        throw InvalidExpressionException()

    return evaluatePostfix(postfix)
}

fun toPostfix(input: String): Queue {
    val opStack = Stack()
    val result = Queue()

    // tokenize
    val tokenizer = MathExpressionTokenizer()
    val tokens = tokenizer.tokenize(input)

    // process expression
    for (token in tokens) {
        // number or variable, add to result
        if (isValidOneTerm(token) || isValidVar(token))
            try {
                result.enqueue(tokenToVal(token).toString())
            } catch (e: UnknownVariableException) {
                println(e.message)
            }

        // stack is empty or stack top == "(";
        // push operator to stack
        else if (isOperator(token)) {
            if (opStack.isEmpty() || opStack.peek() == "(")
                opStack.push(token)
            // operator.precedence > stack.top.precedence
            else if (Operator.findBySymbol(token).precedence >
                Operator.findBySymbol(opStack.peek().toString()).precedence
            )
                opStack.push(token)
            // incoming operator has lower or equal precedence
            // compared to the top of the stack
            else if (Operator.findBySymbol(token).precedence <=
                Operator.findBySymbol(opStack.peek().toString()).precedence
            ) {
                while (opStack.isNotEmpty() && (
                            (Operator.findBySymbol(token).precedence <=
                                    Operator.findBySymbol(opStack.peek().toString()).precedence) &&
                                    opStack.peek() != "(")
                ) {
                    result.enqueue(opStack.pop().toString())
                }

                opStack.push(token)
            }
        }
        // incoming element is a left parenthesis;
        // push it on the stack
        else if (token == "(") opStack.push(token)
        // incoming element is a right parenthesis;
        // pop the stack and add operators to the result until you see
        // a left parenthesis.
        // Discard the pair of parentheses
        else if (token == ")") {
            while (opStack.isNotEmpty() && opStack.peek() != "(")
                result.enqueue(opStack.pop().toString())
            if (opStack.peek() == "(") opStack.pop()               // discard "("
            else
                throw InvalidExpressionException()
        }
    }
    // expression is empty;
    // pop all operator and add to result
    while (opStack.isNotEmpty())
        result.enqueue(opStack.pop().toString())

    return result
}

fun evaluatePostfix(postfix: Queue): BigInteger? {
    val calcStack = Stack()

    while(postfix.isNotEmpty()) {
        val token = postfix.dequeue()
        if (isValidOneTerm(token.toString()) || isValidVar(token.toString()))
            try {
                calcStack.push(tokenToVal(token.toString()).toString())
            } catch (e: UnknownVariableException) {
                println(e.message)
            }
        else if (isOperator(token.toString())) {
            val opObj = Operator.findBySymbol(token.toString())
            val a = calcStack.pop()?.toBigInteger()
            val b = calcStack.pop()?.toBigInteger()

            var result = 0.toBigInteger()
            if (a != null && b != null)
                result = opObj.method.invoke(b, a)
            calcStack.push(result.toString())
        }
    }
    return calcStack.pop()?.toBigInteger()
}

fun isOperator(op: String): Boolean = "+-/*^".contains(op)

fun tokenToVal(token: String): BigInteger {

    if (token.startsWith("-")) {
        val newToken = token.substring(token.indexOf('-') + 1)
        return if (isValidVar(newToken)) -1.toBigInteger() * vars[newToken]!! else -1.toBigInteger() * newToken.toBigInteger()
    }

    return when {
        isValidVar(token) -> vars[token]!!
        isValidIdentifier(token) -> throw UnknownVariableException()
        else -> token.toBigInteger()
    }
}

fun isAssignment(input: String): Boolean {
    return input.split("=").size > 1
}

fun isExpression(input: String): Boolean {
//    val op = "[-+/*^]+"
//    val expr = "^(\\w+)(\\s*$op\\s*\\w+)*$"
//    val withoutParenthesis = input.replace("[()]".toRegex(), "")
//    return withoutParenthesis.matches(expr.toRegex())
    return """[-+*/^()a-zA-Z0-9]""".toRegex().containsMatchIn(input)
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
            val helpMessage = """
                Smart Calculator Help:
                
                the calculator uses basic arithmetic operations: 
                Add, Subtract, Multiply, Divide, and Power
                you can use + - * / ^ for the respective operations.
                
                 e.g. 
                 Enter the following expression at the prompt,
                 and press the ENTER key to display the expression result.
                  
                 3 + 4 * 12 / (6 - 2) ^ 2
                 
                 You can also define variables and store values in them.
                 
                 e.g. 
                 a = 5
                 b = 7
                 c = a
                 
                 Enter the name of the variable and press ENTER
                 to display the value of the variable:
                 a
                 5
                 
                 Enter the following commands for respective actions:
                 /help      to display this help screen
                 /listvars  to display defined variables
                 /exit      to exit the calculator
                 
            """.trimIndent()
            println(helpMessage)
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

fun doAssignment(input: String): BigInteger? {
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
        vars[identifier] = rValue.toBigInteger()

    return vars[identifier]
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

    fun contains(item: String): Boolean {
        val q = Queue()
        var isFound = false
        while (this.isNotEmpty()) {
            val x = this.dequeue()
            q.enqueue(x.toString())
            if (x == item) isFound = true
        }
        while (q.isNotEmpty()) {
            val y = q.dequeue()
            this.enqueue(y.toString())
        }
        return isFound
    }
}

enum class Operator(
    val symbol: String,
    val precedence: Int,
    val method: (BigInteger, BigInteger) -> BigInteger
) {
    PLUS("+", 1, { a: BigInteger, b: BigInteger -> a + b }),
    MINUS("-", 1, { a: BigInteger, b: BigInteger -> a - b }),
    MULTIPLY("*", 2, { a: BigInteger, b: BigInteger -> a * b }),
    DIVIDE("/", 2, { a: BigInteger, b: BigInteger -> a / b }),
    POWER("^", 3, { a: BigInteger, b: BigInteger -> a.pow(b.toInt()) }),
    SPACE(" ", 0, { _: BigInteger, _: BigInteger -> throw InvalidExpressionException() } ),
    NULL("", 10, { _: BigInteger, _: BigInteger -> 0.toBigInteger() });

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

class MathExpressionTokenizer {
    private val TT_OPERATOR: Int = 1
    private val TT_NUMBER: Int = 2
    private val TT_LEFT_PAR: Int = 3
    private val TT_RIGHT_PAR: Int = 4
    private val TT_WORD: Int = 5
    private val TT_SPACE: Int = 6
    private val TT_IGNORE = 7

    private val tokenizedExpr = mutableListOf<String>()

    private fun ttype(token: String): Int? {
        val ttypes = mapOf(
            "\\+" to TT_OPERATOR,
            "\\-" to TT_OPERATOR,
            "\\*" to TT_OPERATOR,
            "\\/" to TT_OPERATOR,
            "\\^" to TT_OPERATOR,
            "[.0-9]" to TT_NUMBER,
            "[a-zA-Z]" to TT_WORD,
            "\\(" to TT_LEFT_PAR,
            "\\)" to TT_RIGHT_PAR,
            "\\s" to TT_SPACE,
            "," to TT_IGNORE
        )

        val tokenType = -1
        for ((symbol, _) in ttypes) {
            if (token.matches(symbol.toRegex()))
                return ttypes[symbol]
        }
        return tokenType
    }

    fun tokenize(expr: String): MutableList<String> {
        var i = 0
        var t = ""
        while (i < expr.length) {
            var c = expr[i]
            when {
                ttype(c.toString()) == TT_OPERATOR -> tokenizedExpr.add(c.toString())
                ttype(c.toString()) == TT_NUMBER -> {
                    t += c
                    while (i + 1 < expr.length && ttype(expr[i + 1].toString()) == TT_NUMBER) {
                        i += 1
                        c = expr[i]
                        t += c
                    }
                    tokenizedExpr.add(t)
                    t = ""
                }
                ttype(c.toString()) == TT_WORD -> {
                    t += c
                    while (i + 1 < expr.length && ttype(expr[i + 1].toString()) == TT_WORD) {
                        i += 1
                        c = expr[i]
                        t += c
                    }
                    tokenizedExpr.add(t)
                    t = ""
                }
                ttype(c.toString()) == TT_SPACE -> tokenizedExpr.add(c.toString())
                ttype(c.toString()) == TT_LEFT_PAR -> tokenizedExpr.add(c.toString())
                ttype(c.toString()) == TT_RIGHT_PAR -> tokenizedExpr.add(c.toString())
                ttype(c.toString()) == TT_IGNORE -> {
                }
                else -> throw InvalidExpressionException()
            }
            i += 1
        }
        return tokenizedExpr
    }
}

class UnknownCommandException(message: String = "Unknown command") : Exception(message)
class InvalidExpressionException(message: String = "Invalid expression") : Exception(message)
class EmptyQueueException(message: String = "Empty queue exception") : Exception(message)
class UnknownVariableException(message: String = "Unknown variable") : Exception(message)
class InvalidAssignmentException(message: String = "Invalid assignment") : Exception(message)
class InvalidIdentifierException(message: String = "Invalid identifier") : Exception(message)