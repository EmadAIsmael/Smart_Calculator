// Posted from EduTools plugin
fun main() {
    // write your code here
    val s = readLine()!!

    for (i in 0..s.length - 3) {
        if (s.substring(i, i + 3).toLowerCase() == "the") {
            println(i)
            return
        }
    }
    println(-1)
}
