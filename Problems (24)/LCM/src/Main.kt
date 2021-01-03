import java.math.BigInteger
import java.util.Scanner

fun main() {
    val scanner = Scanner(System.`in`)
    val (a, b) = Array(2) { scanner.nextBigInteger() }

    println(lcm(a, b))
}

fun lcm(a: BigInteger, b: BigInteger): BigInteger {
    return (a * b) / a.gcd(b)
}
