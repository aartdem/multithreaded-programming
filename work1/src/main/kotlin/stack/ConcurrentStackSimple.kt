package stack

import java.util.concurrent.atomic.AtomicReference

class ConcurrentStackSimple<T> : ConcurrentStack<T> {
    private var head = AtomicReference<Node<T>?>()
    override fun push(x: T) {
        while (true) {
            val lastHead = head.get()
            val newHead = Node(x, lastHead)
            if (head.compareAndSet(lastHead, newHead)) {
                return
            }
        }
    }

    // returns null if stack is empty
    override fun pop(): T? {
        while (true) {
            val lastHead = head.get() ?: return null
            if (head.compareAndSet(lastHead, lastHead.next)) {
                return lastHead.value
            }
        }
    }

    // returns null if stack is empty
    override fun top(): T? = head.get()?.value
}