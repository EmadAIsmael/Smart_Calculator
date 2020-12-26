// Posted from EduTools plugin
fun genPassword(aa: Int, bb: Int, cc: Int, n: Int): String {

    var a = aa
    var b = bb
    var c = cc

    val digits = "0123456789"
    val lowerCase = "abcdefghijklmnopqrstuvwxyz"
    val upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

    val funArray = arrayOf({ upperCase.random() }, { lowerCase.random() }, { digits.random() })

    val password = CharArray(n)
    var i = 0
    while (i < n) {
        if (a > 0) {
            val newChar = funArray[0]()
            if (i > 0 && newChar == password[i - 1]) continue
            password[i] = newChar
            a--
            i++
        }
        if (i < n && b > 0) {
            val newChar = funArray[1]()
            if (i > 0 && newChar == password[i - 1]) continue
            password[i] = newChar
            b--
            i++
        }
        if (i < n && c > 0) {
            val newChar = funArray[2]()
            if (i > 0 && newChar == password[i - 1]) continue
            password[i] = newChar
            c--
            i++
        }
        while (i < n && a == 0 && b == 0 && c == 0) {
            val funIdx = "012".random().toString().toInt()
            val newChar = funArray[funIdx]()
            if (i > 0 && newChar == password[i - 1]) continue
            password[i] = newChar
            i++
        }
    }
    return String(password)
}

fun main() {
    // write your code here
    val (a, b, c, n) = readLine()!!.split(" ").map { it.toInt() }

    val password = genPassword(a, b, c, n)

    println(password)
}
