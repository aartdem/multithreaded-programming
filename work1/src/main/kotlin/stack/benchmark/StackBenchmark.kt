package stack.benchmark

import org.apache.commons.math3.distribution.TDistribution
import org.apache.commons.math3.geometry.euclidean.oned.Interval
import org.apache.commons.math3.stat.descriptive.SummaryStatistics
import kotlin.concurrent.thread
import kotlin.math.sqrt
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit


class StackBenchmark<T> {
    private val threads: MutableList<Thread> = mutableListOf()

    private fun clearScenario() {
        threads.clear()
    }

    /**
     * Load the scenario. If scenario was already loaded, overrides it
     */
    fun loadScenario(measureScenario: MeasureScenario<T>) {
        clearScenario()

        val operations = buildList(measureScenario.operationsPerThread) {
            add(measureScenario.firstOperation)
            repeat(measureScenario.operationsPerThread - 1) {
                add(measureScenario.operationGenerator(this.last()))
            }
        }.map { it.toStackOperation() }

        repeat(measureScenario.threadsNum) {
            threads.add(thread(start = false) {
                operations.forEach { it.invoke(measureScenario.initialStack) }
            })
        }
    }

    /**
     * Execute loaded scenario [n] times and returns work time in milliseconds. Returns null if scenario was not loaded
     */
    fun startAndMeasure(n: Int): Interval? {
        if (threads.isEmpty()) return null

        val results = buildList(n) {
            val initialTime = System.currentTimeMillis()
            threads.forEach { it.start() }
            threads.forEach { it.join() }
            val workTimeMills = (System.currentTimeMillis() - initialTime)
            add(workTimeMills.milliseconds.toDouble(DurationUnit.SECONDS))
        }

        return calculateConfidenceInterval(results)
    }

    /** Implemented using [source](https://gist.github.com/gcardone/5536578) */
    private fun calculateConfidenceInterval(results: List<Double>): Interval {
        val stats = SummaryStatistics().apply {
            results.forEach { this.addValue(it) }
        }
        val tDist = TDistribution(stats.n.toDouble())
        val crVal = tDist.inverseCumulativeProbability(0.975)
        val ci = crVal * stats.standardDeviation / sqrt(stats.n.toDouble())
        return Interval(stats.mean - ci, stats.mean + ci)
    }

    fun printLoadedScenario() {
        threads.forEach {
            println(it.toString())
        }
    }
}