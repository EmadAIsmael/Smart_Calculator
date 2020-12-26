// Posted from EduTools plugin
fun main() {
    // write your code here
    val url = readLine()!!

    val parms = url.split("?")[1].split("&")
    val mapping = parms.map { it.split("=") }.map { it[0] to it[1] }.toMap()

    for ((k, v) in mapping)
        println(if (v.isEmpty()) "$k : not found" else "$k : $v")

    if (mapping.containsKey("pass"))
        println("password : ${mapping["pass"]}")
}
