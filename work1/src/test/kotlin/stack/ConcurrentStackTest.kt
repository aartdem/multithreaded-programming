package stack

import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.ModelCheckingOptions
import kotlin.test.Test

abstract class ConcurrentStackTest(private val stack: ConcurrentStack<Int>) {
    @Operation
    fun push(value: Int) = stack.push(value)

    @Operation
    fun pop(): Int? = stack.pop()

    @Operation
    fun top(): Int? = stack.top()

    @Test
    fun modelCheckingTest() = ModelCheckingOptions()
        .sequentialSpecification(SequentialStack::class.java)
        .check(this::class)
}

class ConcurrentStackSimpleTest : ConcurrentStackTest(ConcurrentStackSimple())

