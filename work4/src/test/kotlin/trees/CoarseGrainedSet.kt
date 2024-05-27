package trees

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class CoarseGrainedSet {
    private val set = mutableSetOf<Int>()
    private val mutex = Mutex()

    suspend fun contains(x: Int): Boolean = mutex.withLock {
        set.contains(x)
    }

    suspend fun add(x: Int) = mutex.withLock {
        set.add(x)
    }

    suspend fun remove(x: Int) = mutex.withLock {
        set.remove(x)
    }
}