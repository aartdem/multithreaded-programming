package stack

import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.ModelCheckingOptions
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import stack.common.ConcurrentStack
import stack.elimination.ConcurrentStackWithElimination
import stack.simple.ConcurrentTreiberStack
import kotlin.test.Test

abstract class ConcurrentStackTests(private val stack: ConcurrentStack<Int>) {
    @Operation
    fun push(value: Int) = stack.push(value)

    @Operation
    fun pop(): Int? = stack.pop()

    @Operation
    fun top(): Int? = stack.top()

    @Test
    fun stressTest() = StressOptions()
        .iterations(50)
        .actorsPerThread(3)
        .invocationsPerIteration(1000)
        .sequentialSpecification(SequentialStack::class.java)
        .check(this::class)

    @Test
    fun fourThreadsStressTest() = StressOptions()
        .threads(4)
        .iterations(50)
        .actorsPerThread(3)
        .invocationsPerIteration(1000)
        .sequentialSpecification(SequentialStack::class.java)
        .check(this::class)

    @Test
    fun modelCheckingTest() = ModelCheckingOptions()
        .iterations(50)
        .actorsPerThread(3)
        .invocationsPerIteration(1000)
        .sequentialSpecification(SequentialStack::class.java)
        .checkObstructionFreedom()
        .check(this::class)

    @Test
    fun fourThreadsModelCheckingTest() = ModelCheckingOptions()
        .threads(4)
        .iterations(50)
        .actorsPerThread(3)
        .invocationsPerIteration(1000)
        .sequentialSpecification(SequentialStack::class.java)
        .checkObstructionFreedom()
        .check(this::class)
}

class ConcurrentTreiberStackTests : ConcurrentStackTests(ConcurrentTreiberStack())

class ConcurrentStackWithEliminationTests : ConcurrentStackTests(ConcurrentStackWithElimination())