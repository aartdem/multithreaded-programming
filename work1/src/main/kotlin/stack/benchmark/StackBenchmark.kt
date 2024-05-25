package stack.benchmark

import org.apache.commons.math3.distribution.TDistribution
import org.apache.commons.math3.geometry.euclidean.oned.Interval
import org.apache.commons.math3.stat.descriptive.SummaryStatistics
import kotlin.concurrent.thread
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.time.DurationUnit
import kotlin.time.measureTime

class StackBenchmark<T>(private val measureScenario: MeasureScenario<T>) {
    /**
     * Execute loaded scenario [n] times and returns work time represented as confidence interval
     */
    fun startAndMeasure(n: Int): Interval {
        val results = List(n) {
            val stack = measureScenario.initialStack
            val threads = mutableListOf<Thread>()
            measureTime {
                repeat(measureScenario.threadsNum) { threadId ->
                    threads.add(thread {
                        repeat(measureScenario.operationsPerThread) {
                            when (measureScenario.mode) {
                                Mode.Random -> {
                                    val rnd = Random.nextInt()
                                    if (rnd % 2 == 0) stack.push(measureScenario.pushValueProvider())
                                    else stack.pop()
                                }

                                Mode.PushPop -> {
                                    if (threadId % 2 == 0) stack.push(measureScenario.pushValueProvider())
                                    else stack.pop()
                                }
                            }
                        }
                    })
                }
                threads.forEach { it.join() }
            }.toDouble(DurationUnit.SECONDS)
        }
        return calculateConfidenceInterval(results)
    }

    /**
     * Implemented using [source](https://gist.github.com/gcardone/5536578)
     * */
    private fun calculateConfidenceInterval(results: List<Double>): Interval {
        val stats = SummaryStatistics().apply {
            results.forEach { this.addValue(it) }
        }
        val tDist = TDistribution(stats.n.toDouble())
        val crVal = tDist.inverseCumulativeProbability(0.975)
        val ci = crVal * stats.standardDeviation / sqrt(stats.n.toDouble())
        return Interval(stats.mean - ci, stats.mean + ci)
    }
}