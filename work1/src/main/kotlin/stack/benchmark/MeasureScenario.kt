package stack.benchmark

import stack.common.ConcurrentStack
import kotlin.random.Random

class MeasureScenario<T>(
    val threadsNum: Int,
    val operationsPerThread: Int,
    val initialStack: ConcurrentStack<T>,
    val firstOperation: Operation<T>,
    val operationGenerator: (Operation<T>) -> Operation<T>
) {
    companion object {
        val randomScenarioIntGenerator: (Operation<Int>) -> Operation<Int> = { _ ->
            when (Random.nextInt(3)) {
                0 -> Operation.Push(Random.nextInt())
                1 -> Operation.Pop()
                2 -> Operation.Top()
                else -> throw IllegalStateException()
            }
        }
        val pushPopIntGenerator: (Operation<Int>) -> Operation<Int> = { last ->
            when (last) {
                is Operation.Push -> Operation.Pop()
                is Operation.Pop -> Operation.Push(Random.nextInt())
                is Operation.Top -> Operation.Push(Random.nextInt())
            }
        }
    }
}