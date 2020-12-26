// Posted from EduTools plugin
fun runsOfVowels(word: String, i: Int): IntArray {
    val vowelsList = charArrayOf('a', 'e', 'i', 'o', 'u', 'y')

    var counter = 0
    var idx = i
    do {
        counter++
        idx++
    } while (idx < word.length && word[idx] in vowelsList)
    return intArrayOf(counter, idx)
}

fun runsOfConsonants(word: String, i: Int): IntArray {
    val vowelsList = charArrayOf('a', 'e', 'i', 'o', 'u', 'y')

    var counter = 0
    var idx = i
    do {
        counter++
        idx++
    } while (idx < word.length && word[idx] !in vowelsList)
    return intArrayOf(counter, idx)
}

fun main() {
    // write your code here
    val word = readLine()!!
    val vowelsList = charArrayOf('a', 'e', 'i', 'o', 'u', 'y')

    var i = 0
    var changes = 0
    while (i < word.length) {
        if (word[i] in vowelsList) {
            val (runs, idx) = runsOfVowels(word, i)
            i = idx
            changes += if (runs % 2 == 0) (runs - 1) / 2 else runs / 2
        } else {
            val (runs, idx) = runsOfConsonants(word, i)
            i = idx
            changes += if (runs % 2 == 0) (runs - 1) / 2 else runs / 2
        }
    }
    println(changes)
}
