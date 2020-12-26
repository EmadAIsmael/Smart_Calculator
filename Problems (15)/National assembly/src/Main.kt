import java.util.*

fun main() {
    val scanner = Scanner(System.`in`)
    // write your code here
    val population = scanner.nextInt()
    println(Math.cbrt(population.toDouble()).toInt())
}
