package stack

import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.ModelCheckingOptions
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import stack.common.ConcurrentStack
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
        .sequentialSpecification(SequentialStack::class.java)
        .check(this::class)

    @Test
    fun fourThreadsStressTest() = StressOptions()
        .sequentialSpecification(SequentialStack::class.java)
        .threads(4)
        .iterations(10)
        .invocationsPerIteration(50)
        .check(this::class)

    @Test
    fun modelCheckingTest() = ModelCheckingOptions()
        .sequentialSpecification(SequentialStack::class.java)
        .checkObstructionFreedom()
        .check(this::class)

    @Test
    fun fourThreadsModelCheckingTest() = ModelCheckingOptions()
        .sequentialSpecification(SequentialStack::class.java)
        .checkObstructionFreedom()
        .threads(4)
        .iterations(50)
        .invocationsPerIteration(500)
        .check(this::class)
}

class ConcurrentTreiberStackTests : ConcurrentStackTests(ConcurrentTreiberStack())