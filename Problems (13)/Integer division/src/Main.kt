fun intDivision(x: String, y: String): Int {
    try {
        val xi = x.toInt()
        val yi = y.toInt()

        return xi / yi
    } catch (e: ArithmeticException) {
        println("Exception: division by zero!")
    } catch (e: NumberFormatException) {
        println("Read values are not integers.")
    }
    return 0
}

fun main() {
    val x = readLine()!!
    val y = readLine()!!
    println(intDivision(x, y))

}
