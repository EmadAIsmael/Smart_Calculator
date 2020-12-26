fun printFifthCharacter(input: String): String {
    var msg: String = ""
    try {
        msg = "The fifth character of the entered word is ${input[4]}"
    } catch (e: StringIndexOutOfBoundsException) {
        println("The input word is too short!")
    }
    return msg
}
