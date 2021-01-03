import java.util.Scanner

fun main() {
    val scanner = Scanner(System.`in`)
    val (a, b, c, d) = Array(4) { scanner.nextBigInteger() }

    println(-a * b + c - d)
}
