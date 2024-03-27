package stack.elimination

import stack.common.ConcurrentStack
import stack.common.Node
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.atomic.AtomicStampedReference

class EliminationArray<T>() {
    fun visit(): T? {
        TODO()
    }
}

class ConcurrentStackWithElimination<T>(

) : ConcurrentStack<T> {
    private val head = AtomicReference<Node<T>?>()
    private val eliminationArray = EliminationArray<T>()
    override fun push(x: T) {
        val newHead = Node(x)
        while (true) {
            if (tryPush(newHead)) return
            eliminationArray.visit() ?: return
        }
    }

    override fun pop(): T? {
        while (true) {
            val (tryRes, valRes) = tryPop()
            if (tryRes) return valRes
            eliminationArray.visit()?.let {
                return it
            }
        }
    }

    private fun tryPush(newHead: Node<T>): Boolean {
        val oldHead = head.get()
        newHead.next = oldHead
        return head.compareAndSet(oldHead, newHead)
    }

    private fun tryPop(): Pair<Boolean, T?> {
        val oldHead = head.get() ?: return Pair(true, null)
        val newHead = oldHead.next
        return Pair(head.compareAndSet(oldHead, newHead), oldHead.value)
    }

    override fun top(): T? = head.get()?.value


}


