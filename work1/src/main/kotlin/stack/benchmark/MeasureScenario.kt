package stack.benchmark

import stack.common.ConcurrentStack

data class MeasureScenario<T>(
    val threadsNum: Int,
    val operationsPerThread: Int,
    val initialStack: ConcurrentStack<T>,
    val mode: Mode,
    val pushValueProvider: () -> T
)