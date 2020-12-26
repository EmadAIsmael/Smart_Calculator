// Posted from EduTools plugin
fun main() {
    // write your code here
    val str = readLine()!!

    var i = 0
    for (c in str.substring(1)) {
        if (c - str[i] != 1) {
            println(false)
            return
        }
        i++
    }
    println(true)
}
