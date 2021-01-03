import java.math.BigInteger


fun main() {
    val (a, b, c) = Array(3) { readLine()!!.toBigInteger() }
    var count = 0
    if (a == b && b == c) count = 3
    else if (a == b || a == c || b == c) count = 2
    println(count)
}
