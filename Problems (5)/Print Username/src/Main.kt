fun main() {
    val name = readLine()!!
    if (name == "HIDDEN")
        greet()
    else
        greet(name)
}

fun greet(name: String = "secret user") = println("Hello, ${name}!")
