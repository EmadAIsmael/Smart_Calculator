import java.math.BigInteger

fun main() {
    val a = BigInteger(readLine()!!)
    val b = BigInteger(readLine()!!)

    val sum = a + b
    println("${(a * BigInteger.valueOf(100)) / sum}% ${(b * BigInteger.valueOf(100)) / sum}%")
}
