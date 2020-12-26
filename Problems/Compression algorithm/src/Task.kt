// Posted from EduTools plugin
fun rle(string: String): String {
    var curSymbol: Char
    var symbolCount = 0
    var output = ""

    var i = 0
    while (i < string.length) {
        curSymbol = string[i]
        do {
            symbolCount++
            i++
        } while (i < string.length && curSymbol == string[i])
        output += curSymbol + symbolCount.toString()
        symbolCount = 0
    }
    return output
}

fun main() {
    // write your code here
    val dna = readLine()!!
    println(rle(dna))
}
