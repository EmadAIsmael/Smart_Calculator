fun main() {
    // put your code here
    val (a, b) = readLine()!!.split(" ").map { it.toInt() }
    if (b == 0) {
        println("Division by zero, please fix the second argument!")
    } else
        println(a / b)
}
