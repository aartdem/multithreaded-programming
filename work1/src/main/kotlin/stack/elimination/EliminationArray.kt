package stack.elimination

import kotlin.random.Random

class EliminationArray<T>(
    private val capacity: Int = 10,
    private val maxAttemptsCount: Long = 50,
    randomSeed: Long = System.currentTimeMillis()
) {
    private val exchanger = Array<LockFreeExchanger<T>>(capacity) { LockFreeExchanger() }
    private val random = Random(randomSeed)

    fun visit(value: T?): Result<T?> {
        val slot = random.nextInt(capacity)
        return exchanger[slot].exchange(value, maxAttemptsCount)
    }
}