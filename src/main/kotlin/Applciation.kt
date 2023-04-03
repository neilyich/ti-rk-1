import com.fasterxml.jackson.core.JacksonException
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.kotlinModule
import org.ejml.simple.SimpleMatrix
import java.io.File
import java.io.IOException

fun main() {
    val mapper = configMapper()
    val task = mapper.readValue(File("task.json"), Rk1Task::class.java)
    FormattedPrinter.PRINT_SCALE = task.formatting.scale
    FormattedPrinter.PRINT_WIDTH = task.formatting.width

    println("Решение для 1-го игрока:")
    val (firstStrategy, cost) = solve(task.A, task.maxE)
    FormattedPrinter.printList("Оптимальная стратегия 1-го игрока", firstStrategy)
    FormattedPrinter.printNumber("Цена игры", cost)
    println("-".repeat(30))
    println()
    println("Решение для 2-го игрока:")
    val (secondStrategy, cost2) = solve(task.A.transposed(), task.maxE)
    FormattedPrinter.printList("Оптимальная стратегия 2-го игрока", secondStrategy)
    FormattedPrinter.printNumber("Цена игры", cost2)
    println("-".repeat(30))
    println()
    println("Итоговый результат:")
    FormattedPrinter.printList("Оптимальная стратегия 1-го игрока", firstStrategy)
    FormattedPrinter.printList("Оптимальная стратегия 2-го игрока", secondStrategy)
    FormattedPrinter.printNumber("Цена игры", cost)
}

private fun solve(m: List<List<Double>>, maxE: Double): Pair<List<Double>, Double> {
    FormattedPrinter.printMatrix("Матрица игры", m)
    val alg = MonotoneIterativeAlgorithm(m)
    while (alg.makeIteration() > maxE) {
        /* no-op */
    }
    return alg.strategy() to alg.cost()
}

private fun configMapper(): ObjectMapper {
    val objectMapper = ObjectMapper()
    val module = SimpleModule()
    module.addDeserializer(SimpleMatrix::class.java, object : StdDeserializer<SimpleMatrix?>(SimpleMatrix::class.java) {
        @Throws(IOException::class, JacksonException::class)
        override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): SimpleMatrix {
            val matrix = jsonParser.readValueAs(
                Array<DoubleArray>::class.java
            )
            return SimpleMatrix(matrix)
        }
    })
    objectMapper.registerModule(module)
    objectMapper.enable(JsonParser.Feature.ALLOW_COMMENTS)
    objectMapper.registerModule(kotlinModule())
    return objectMapper
}

private fun <T> List<List<T>>.transposed(): List<List<T>> {
    val result = ArrayList<ArrayList<T>>(this[0].size)
    repeat(this[0].size) { result.add(ArrayList(this.size)) }
    for (oldRow in this.indices) {
        for (oldCol in this[0].indices) {
            result[oldCol].add(this[oldRow][oldCol])
        }
    }
    return result
}
