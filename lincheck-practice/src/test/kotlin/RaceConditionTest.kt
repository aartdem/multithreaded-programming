import org.example.Counter

import org.jetbrains.kotlinx.lincheck.annotations.*
import org.jetbrains.kotlinx.lincheck.*
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import kotlin.test.Test

class RaceConditionTest {
    var x = 0; var y = 0; var rx = 0; var ry = 0
    @Operation
    fun thread1(): Int {
        x = 1
        ry = y
        return ry
    }
    @Operation
    fun thread2(): Int {
        y = 1
        rx = x
        return rx
    }
    @Test
    fun test() {
        // change to ModelCheckingOptions() and show the difference
        val options = StressOptions().actorsPerThread(1).actorsBefore(0).threads(2)
            .invocationsPerIteration(100_000).iterations(10_000)
        LinChecker.check(this::class.java, options)
    }
}
