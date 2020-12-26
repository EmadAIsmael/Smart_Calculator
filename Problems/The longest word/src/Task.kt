// Posted from EduTools plugin
fun main() {
    // write your code here
    val input = readLine()!!.split(" ")
    var longest = ""
    for (word in input)
        if (word.length > longest.length)
            longest = word

    println(longest)
}
