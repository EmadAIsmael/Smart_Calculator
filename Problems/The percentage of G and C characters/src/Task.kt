// Posted from EduTools plugin
fun main() {
    // write your code here
    val input = readLine()!!.toLowerCase()
    val len = input.length
    val cg = input.count { it == 'c' || it == 'g' }

    println(cg.toDouble() / len * 100.0)
}
