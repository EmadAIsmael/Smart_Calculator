// Posted from EduTools plugin
import java.util.Scanner

fun main() {
    // write your code here
    val scanner = Scanner(System.`in`)
    val s = scanner.next()
    val n = scanner.nextInt()

    println(
        if (n < s.length) s.substring(n) + s.substring(0, n)
        else s
    )
}
