import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.max

object FormattedPrinter {
    var PRINT_SCALE = 3
    var PRINT_WIDTH = 5

    fun printMatrix(label: String? = null, m: List<List<Number>>) {
        label?.let { println("$it:") }
        println(formatMatrix(m))
    }

    fun printList(label: String?, list: List<Number>) {
        label?.let { print("$it: ") }
        println(formatList(list))
    }

    fun printNumber(label: String?, number: Number) {
        label?.let { print("$it: ") }
        println(formatNumber(number))
    }

    private fun formatNumber(number: Number): String {
        val bd = when (number) {
            is Int -> BigDecimal.valueOf(number.toLong())
            else -> BigDecimal.valueOf(number.toDouble())
        }
        val formatted = bd.setScale(PRINT_SCALE, RoundingMode.HALF_EVEN).toString()
        return " ".repeat(max(PRINT_WIDTH - formatted.length, 0)) + formatted
    }

    private fun formatList(list: List<Number>): String {
        return list.map { formatNumber(it) }.joinToString(prefix = "[", postfix = "]") { it }
    }

    private fun formatMatrix(m: List<List<Number>>): String {
        return m.map { formatList(it) }.joinToString(separator = "\n", prefix = "", postfix = "") { it }
    }
}
