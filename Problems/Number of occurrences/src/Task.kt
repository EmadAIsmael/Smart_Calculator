// Posted from EduTools plugin
fun main() {
    // write your code here
    val input = readLine()!!
    val substr = readLine()!!

    println(frequency(substr, input))
}

fun frequency(substr: String, input: String): Int {
    var count = 0
    val len = substr.length
    for (i in 0..input.length - len) {
        if (input.substring(i, i + len) == substr) count++
    }
    return count
}
