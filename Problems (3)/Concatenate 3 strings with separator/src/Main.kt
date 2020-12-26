fun main() {
    val l1 = readLine()!!
    val l2 = readLine()!!
    val l3 = readLine()!!
    var sep = readLine()!!

    if (sep == "NO SEPARATOR")
        sep = " "

    println(concat(l1, l2, l3, sep))
}

fun concat(l1: String, l2: String, l3: String, sep: String = " "): String {

    return "$l1$sep$l2$sep$l3"
}
