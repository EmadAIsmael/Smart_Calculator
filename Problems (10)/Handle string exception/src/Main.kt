fun main() {
    val index = readLine()!!
    val word = readLine()!!

    try {
        val i = index.toInt()
        println(word[i])
    } catch (e: Exception) {
        println("There isn't such an element in the given string, please fix the index!")
    }
}
