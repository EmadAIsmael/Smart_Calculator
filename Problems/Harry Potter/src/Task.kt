// Posted from EduTools plugin
import java.util.*

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)

    // write your code here
    val str = scanner.next()

    println(
        when (str) {
            "gryffindor" -> "bravery"
            "hufflepuff" -> "loyalty"
            "slytherin" -> "cunning"
            "ravenclaw" -> "intellect"
            else -> "not a valid house"
        }
    )
}
