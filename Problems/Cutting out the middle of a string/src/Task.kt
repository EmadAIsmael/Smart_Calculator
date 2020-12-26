// Posted from EduTools plugin
fun main() {
    // write your code here
    val s = readLine()!!

    val mid = s.length / 2
    println(
        if (s.length % 2 == 0) s.substring(0, mid - 1) + s.substring(mid + 1) else s.substring(
            0,
            mid
        ) + s.substring(mid + 1)
    )
}
