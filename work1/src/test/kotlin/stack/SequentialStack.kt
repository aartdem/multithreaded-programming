package stack

class SequentialStack {
    private val deque = ArrayDeque<Int>()

    fun push(x: Int) = deque.addLast(x)

    fun pop(): Int? = deque.removeLastOrNull()

    fun top(): Int? = deque.lastOrNull()
}
