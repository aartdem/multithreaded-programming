package stack

import java.util.concurrent.atomic.AtomicReference

// Main idea taken from https://people.csail.mit.edu/shanir/publications/Lock_Free.pdf

class ThreadInfo<T>(
    val op: Char,
    val node: Node<T>?,
    val spin: Int = 1000,
) {
    val id: Long = Thread.currentThread().id
}

class ConcurrentStackWithElimination<T>(
    private val maxTreadsNum: Int,
) : ConcurrentStack<T> {
    private val head = AtomicReference<Node<T>?>()
    override fun push(x: T) {
        val lastHead = head.get()
        val p = ThreadInfo('+', Node(x, lastHead))
        if (!head.compareAndSet(lastHead, p.node)) {
            lesOp(p)
        }
    }

    override fun pop(): T? {
        val lastHead = head.get() ?: return null
        if (!head.compareAndSet(lastHead, lastHead.next)) {
            return lastHead.value
        }
        else {
            val p = ThreadInfo<T>('-', null) // ???
            return lesOp(p)
        }
    }

    override fun top(): T? = head.get()?.value

    private fun lesOp(p: ThreadInfo<T>) : T? {
        TODO("Not yet implemented")
    }

}
