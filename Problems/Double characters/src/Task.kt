// Posted from EduTools plugin
fun main() {
    // write your code here
    val s = readLine()!!

    var output = ""
    for (c in s) {
        output += c.toString() + c.toString()
    }
    println(output)
}
