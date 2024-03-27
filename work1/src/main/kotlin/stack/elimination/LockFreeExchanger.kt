package stack.elimination

import java.util.concurrent.atomic.AtomicStampedReference

class LockFreeExchanger<T> {
    private enum class State {
        EMPTY, WAITING, BUSY
    }

    val slot: AtomicStampedReference<T?> = AtomicStampedReference(null, 0)

    fun exchange(myItem: T) {
        val stampHolder = intArrayOf(State.EMPTY.ordinal)
        while (true) {
            val anotherItem = slot.get(stampHolder) ?: TODO()
            when (stampHolder[0]) {
                State.EMPTY.ordinal -> {
                    if (slot.compareAndSet(anotherItem, myItem, State.EMPTY.ordinal, State.WAITING.ordinal)) {

                    }
                }

                State.WAITING.ordinal -> {}
                State.BUSY.ordinal -> {}
            }
        }
    }
}