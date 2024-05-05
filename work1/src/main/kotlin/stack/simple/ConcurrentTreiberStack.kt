package stack.simple

import stack.common.ConcurrentStack
import stack.common.Node
import java.util.concurrent.atomic.AtomicReference

class ConcurrentTreiberStack<T> : ConcurrentStack<T> {
    private var head = AtomicReference<Node<T>?>()
    override fun push(x: T) {
        val newHead = Node(x)
        while (true) {
            val lastHead = head.get()
            newHead.next = lastHead
            if (head.compareAndSet(lastHead, newHead)) {
                return
            }
        }
    }

    override fun pop(): T? {
        while (true) {
            val lastHead = head.get() ?: return null
            if (head.compareAndSet(lastHead, lastHead.next)) {
                return lastHead.value
            }
        }
    }

    override fun top(): T? = head.get()?.value
}
