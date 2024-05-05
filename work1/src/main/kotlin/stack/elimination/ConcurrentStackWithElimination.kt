package stack.elimination

import stack.common.ConcurrentStack
import stack.common.Node
import java.util.concurrent.atomic.AtomicReference

class ConcurrentStackWithElimination<T> : ConcurrentStack<T> {
    private val eliminationArray = EliminationArray<T>()
    private val head = AtomicReference<Node<T>?>()

    override fun push(x: T) {
        while (true) {
            val oldHead = head.get()
            val newHead = Node(x, oldHead)
            if (head.compareAndSet(oldHead, newHead)) {
                return
            }
            val visitResult = eliminationArray.visit(x)
            if (visitResult.isSuccess && visitResult.getOrNull() == null) {
                return
            }
        }
    }

    override fun pop(): T? {
        while (true) {
            val oldHead = head.get() ?: return null
            val newHead = oldHead.next
            if (head.compareAndSet(oldHead, newHead)) {
                return oldHead.value
            }
            val visitResult = eliminationArray.visit(null)
            if (visitResult.isSuccess && visitResult.getOrNull() != null) {
                return visitResult.getOrNull()
            }
        }
    }

    override fun top(): T? = head.get()?.value
}


