package calculator

fun main() {

    while (true) {
        val input = readLine()!!.split(' ')
        when {
            input[0].isNotEmpty() -> {
                when (input.size) {
                    1 -> {
                        if (input[0] == "/exit") {
                            println("Bye!")
                            return
                        } else if (input[0] == "/help") {
                            println("The program calculates the sum of numbers")
                        } else {
                            println(input[0].toInt())
                        }
                    }
                    else -> {
                        val nums = input.map { it.toInt() }
                        println(nums.sum())
                    }
                }
            }

        }
    }
}

data class Expr(val left: String, val operator: String, val right: String) {

    val add = { a: Int, b: Int -> a + b }

    val subtract = { a: Int, b: Int -> a - b }

    val ops = mapOf(
            "+" to add,
            "-" to subtract
    )

    fun eval(): Int? {
        val f: ((Int, Int) -> Int)? = ops?.get(normalizedOperator())
        return f?.invoke(left.toInt(), right.toInt())
    }

    fun normalizedOperator(): String {

        var normalized = ""
        for (op in operator) {
            when {
                normalized == "" -> normalized = op.toString()
                normalized == "+" && op.toString() == "-" -> normalized = "-"
                normalized == "-" && op.toString() == "+" -> normalized = "-"
                normalized == "-" && op.toString() == "-" -> normalized = "+"
                else -> normalized
            }
        }
        return normalized
    }

}
/*
var expr1 = Expr("4", "+", "6")

println("expr evalued = ${expr1.eval()}")
*/
