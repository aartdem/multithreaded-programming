package stack

import de.m3y.kformat.Table
import de.m3y.kformat.table
import org.apache.commons.math3.geometry.euclidean.oned.Interval
import stack.benchmark.MeasureScenario
import stack.benchmark.Mode
import stack.benchmark.StackBenchmark
import stack.simple.ConcurrentTreiberStack
import java.math.RoundingMode
import kotlin.random.Random

const val eps = 1e-3
const val repeatCount = 15

val Double.round3: String
    get() = this.toBigDecimal().setScale(3, RoundingMode.HALF_UP).toDouble().toString()

val Interval.formatted: String
    get() {
        val half = (this.sup - this.inf) / 2
        val mid = this.barycenter
        return if (half < eps) {
            mid.round3
        } else {
            "${mid.round3} \\pm ${half.round3}"
        }
    }

fun main() {
    // prepare cases
    val threadNums = listOf(1, 2, 4, 6)
    val operationsCount = buildList {
        for (i in 1e6.toInt()..5e6.toInt() step 1e6.toInt()) {
            add(i)
        }
    }
    // calculating
    val results = mutableListOf<List<Interval>>()
    for (threadNum in threadNums) {
        val currentResult = mutableListOf<Interval>()
        for (opCount in operationsCount) {
            val stackBenchmark = StackBenchmark(
                MeasureScenario(
                    threadNum,
                    opCount,
                    ConcurrentTreiberStack(),
                    Mode.Random
                ) { Random.nextInt() }
            )

            stackBenchmark.startAndMeasure(repeatCount).let {
                currentResult.add(it)
            }
            println("Debug Info | Threads: $threadNum | Operations: $opCount | DONE")
        }
        results.add(currentResult)
    }
    table {
        header(listOf("") + operationsCount.map { it.toString() })
        for ((id, threadValue) in threadNums.withIndex()) {
            val currentResults = listOf(threadValue.toString()) + results[id].map { it.formatted }
            row(*currentResults.toTypedArray())
        }
        hints {
            borderStyle = Table.BorderStyle.SINGLE_LINE
        }
    }.print()
}