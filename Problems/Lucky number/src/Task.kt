// Posted from EduTools plugin
fun main() {
    // write your code here
    val n = readLine()!!

    val first = n.substring(0, n.length / 2).map { it.toString().toInt() }.sum()
    val second = n.substring(n.length / 2).map { it.toString().toInt() }.sum()
    if (first == second)
        println("YES")
    else
        println("NO")
}
