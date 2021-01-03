import java.math.BigInteger

fun main() {
    val number = readLine()!!.toBigInteger()
    val exabyte = 2.toBigInteger().pow(63)
    println("${number * exabyte}")
}
