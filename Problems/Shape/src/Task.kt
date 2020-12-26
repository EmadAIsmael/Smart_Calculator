// Posted from EduTools plugin
import java.util.*

fun main() {
    val scanner = Scanner(System.`in`)

    // write your code here
    val shape = mapOf(
        1 to "square",
        2 to "circle",
        3 to "triangle",
        4 to "rhombus"
    )
    when (val input = scanner.nextInt()) {
        in shape.keys -> println("You have chosen a ${shape[input]}")
        else -> println("There is no such shape!")
    }
}
