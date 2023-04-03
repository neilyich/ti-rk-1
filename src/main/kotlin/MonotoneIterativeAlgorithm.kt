import java.util.LinkedList
import kotlin.math.abs

class MonotoneIterativeAlgorithm(
    private val strategies: List<List<Double>>
) {

    private val x = LinkedList<List<Double>>()
    private val c = LinkedList<List<Double>>()

    init {
        println(strategies.toString())
        x.add(ArrayList<Double>(strategies.size).apply {
            add(1.0)
            repeat(strategies.size - 1) { add(0.0) }
        })
        c.add(strategies[0])
    }

    fun strategy(): List<Double> {
        return x.last
    }

    fun cost(): Double {
        return c.last.minOf { it }
    }

    fun makeIteration(): Double {
        val i = c.size
        println("Итерация: $i")
        val indices = findMinIndicesInC()
        FormattedPrinter.printList("J${i - 1}", indices)
        val subGame = buildSubGame(indices)
        FormattedPrinter.printMatrix("Решаем подыгру - Г$i", subGame)
        val xCandidate = SimplexSolver.solve(subGame)
        FormattedPrinter.printList("Решение подыгры - ẋ$i", xCandidate)
        val cCandidate = calcCCandidate(xCandidate)
        FormattedPrinter.printList("ĉ$i", cCandidate)
        val cGame = listOf(c.last, cCandidate)
        FormattedPrinter.printMatrix("Решаем подыгру: [c${i - 1}, ĉ$i]", cGame)
        val alphaList = SimplexSolver.solve(listOf(c.last, cCandidate))
        val alpha = alphaList[0]
        FormattedPrinter.printList("Решение подыгры - α$i", alphaList)
        val newX = (1.0 - alpha) * xCandidate add alpha * x.last
        val newC = (1.0 - alpha) * cCandidate add alpha * c.last
        x.add(newX)
        c.add(newC)
        FormattedPrinter.printList("x$i", newX)
        FormattedPrinter.printList("c$i", newC)
        FormattedPrinter.printNumber("v$i", cost())
        return 1.0 - alpha
    }

    private fun findMinIndicesInC(): List<Int> {
        val c = c.last
        val min = c.minOf { it }
        val indices = LinkedList<Int>()
        for (i in c.indices) {
            if (abs(min - c[i]) < 0.00001) {
                indices.add(i)
            }
        }
        return indices
    }

    private fun buildSubGame(indices: List<Int>): List<List<Double>> {
        val subGame = ArrayList<ArrayList<Double>>(strategies.size)
        repeat(strategies.size) { subGame.add(ArrayList(indices.size)) }
        for (i in indices) {
            for (row in strategies.indices) {
                subGame[row].add(strategies[row][i])
            }
        }
        return subGame
    }

    private fun calcCCandidate(newX: List<Double>): List<Double> {
        var newC = DoubleArray(newX.size) { 0.0 }.toList()
        for (row in newX.indices) {
            newC = newC add newX[row] * strategies[row]
        }
        return newC.toList()
    }
}

operator fun Double.times(list: List<Double>): List<Double> {
    return list.map { it * this }
}

infix fun List<Double>.add(list: List<Double>): List<Double> {
    return (indices).map { this[it] + list[it] }
}
