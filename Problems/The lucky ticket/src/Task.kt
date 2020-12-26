// Posted from EduTools plugin
fun main() {
    // write your code here
    val ticket = readLine()!!

    var sumFirst = 0
    var sumLast = 0
    for (c in ticket.substring(0, 3)) {
        sumFirst += c.toString().toInt()
    }
    for (c in ticket.substring(ticket.length - 3)) {
        sumLast += c.toString().toInt()
    }
    println(if (sumFirst == sumLast) "Lucky" else "Regular")
}
