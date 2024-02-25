package stack

import stack.Node
import java.util.EmptyStackException
import java.util.concurrent.atomic.AtomicReference

class Stack {
    private var head = AtomicReference<Node?>()
    fun push(x: Int) {
        while (true) {
            val lastHead = head.get() ?: throw EmptyStackException()
            val newHead = Node(x, lastHead)
            if (head.compareAndSet(lastHead, newHead)) {
                return
            }
        }
    }
}