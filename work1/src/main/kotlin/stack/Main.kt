package stack

import de.m3y.kformat.Table
import de.m3y.kformat.table
import org.apache.commons.math3.geometry.euclidean.oned.Interval
import stack.benchmark.MeasureScenario
import stack.benchmark.Operation
import stack.benchmark.StackBenchmark
import stack.simple.ConcurrentTreiberStack

fun main() {
    // prepare cases
//    val threadNums = listOf(1, 2, 4, 6)
//    val operations = buildList {
//        for (i in 1e7.toInt()..1e8.toInt() step 1e7.toInt()) {
//            add(i)
//        }
//    }
    val threadNums = listOf(6)
    val operations = listOf(100)
    // calculating
    val stackBenchmark = StackBenchmark<Int>()
    val results = mutableListOf(mutableListOf<Interval>())
    for (threadNum in threadNums) {
        val currentResult = mutableListOf<Interval>()
        for (opCount in operations) {
            val singleThreadRandomMeasureScenario = MeasureScenario(
                threadNum, opCount / threadNum,
                ConcurrentTreiberStack(),
                Operation.Push(0),
                MeasureScenario.randomScenarioIntGenerator
            )
            stackBenchmark.loadScenario(singleThreadRandomMeasureScenario)
            val workTime = stackBenchmark.startAndMeasure(20) ?: throw IllegalStateException()
            currentResult.add(workTime)
            println("Threads: $threadNum | Operations: $opCount | DONE")
        }
        results.add(currentResult)
    }
    table {
        header(listOf("") + operations.map { it.toString() })

        // TODO("add my rows")
        row(10, "b...1", 2.1f, "foo")

        hints {
            borderStyle = Table.BorderStyle.SINGLE_LINE // or NONE
        }
    }.print()
}
