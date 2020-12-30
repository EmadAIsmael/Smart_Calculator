fun solution(strings: MutableList<String>, str: String): MutableList<String> {
    // put your code here
    return strings.apply { strings.forEachIndexed { index, s -> if (s == str) strings[index] = "Banana" } }
}
