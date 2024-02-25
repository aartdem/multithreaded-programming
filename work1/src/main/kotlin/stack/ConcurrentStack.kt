package stack

interface ConcurrentStack<T> {
    fun push(x: T)
    fun pop(): T?
    fun top(): T?
}