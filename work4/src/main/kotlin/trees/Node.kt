package trees

import kotlinx.coroutines.sync.Mutex

class Node<T : Comparable<T>>(
    var key: T,
    var left: Node<T>?,
    var right: Node<T>?
) {
    private val mutex = Mutex()

    suspend fun lock() {
        mutex.lock()
    }

    fun unlock() {
        if (mutex.isLocked) mutex.unlock()
    }
}