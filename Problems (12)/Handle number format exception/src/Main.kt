import java.lang.Exception

fun parseCardNumber(cardNumber: String): Long {
    if (cardNumber.length != 19)
        throw Exception("Invalid Card.")
    val card = cardNumber.split(" ").joinToString(separator = "")
    if (card.length == 16) {
        try {
            return card.toLong()
        } catch (e: Exception) {
            throw Exception("Invalid Card.")
        }
    } else
        throw Exception("Invalid Card.")
}
