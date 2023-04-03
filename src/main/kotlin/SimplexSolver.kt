object SimplexSolver {
    fun solve(a: List<List<Double>>): List<Double> {
        val process = ProcessBuilder("cmd.exe", "/c", "simplex/simplex2.exe")
            .redirectErrorStream(true)
            .start()
        process.outputWriter().use { writer ->
            writer.write(a.size.toString())
            writer.newLine()
            for (row in a) {
                writer.write(row.joinToString(separator = " ") { it.toString() })
                writer.newLine()
            }
        }
        return process.inputReader().use { reader ->
            val result = reader.readLine() ?: throw RuntimeException("error solving by simplex method")
            val numbers = result.split(" ")
            return@use try {
                numbers.map { it.toDouble() }
            } catch (e: Exception) {
                var resultOpt: String? = result
                while (resultOpt != null) {
                    println(resultOpt)
                    resultOpt = reader.readLine()
                }
                throw e
            }
        }
    }
}