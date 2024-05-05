package stack.common

interface ConcurrentStack<T> {
    fun push(x: T)

    /**
    Returns null if stack is empty
     */
    fun pop(): T?

    /**
    Returns null if stack is empty
     */
    fun top(): T?
}
