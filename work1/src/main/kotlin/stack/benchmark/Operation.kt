package stack.benchmark

import stack.common.ConcurrentStack

sealed interface Operation<T> {
    class Push<T>(val value: T) : Operation<T>
    class Pop<T> : Operation<T>
    class Top<T> : Operation<T>

    fun toStackOperation(): ConcurrentStack<T>.() -> Unit = when (this) {
        is Push<T> -> fun ConcurrentStack<T>.() = (::push)(value)
        is Pop<T> -> fun ConcurrentStack<T>.() { (::pop)() }
        is Top<T> -> fun ConcurrentStack<T>.() { (::top)() }
    }
}