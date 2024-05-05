package stack.elimination

import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicStampedReference

class LockFreeExchanger<T> {
    private enum class State {
        EMPTY, WAITING, BUSY
    }

    private val slot = AtomicStampedReference<T>(null, State.EMPTY.ordinal)
    fun exchange(myItem: T?, maxAttemptsCount: Long): Result<T?> {
        val stampHolder = IntArray(1) { State.EMPTY.ordinal }
        var i = 0
        while (i < maxAttemptsCount) {
            var yrItem = slot.get(stampHolder)
            when (stampHolder[0]) {
                State.EMPTY.ordinal -> {
                    if (slot.compareAndSet(yrItem, myItem, State.EMPTY.ordinal, State.WAITING.ordinal)) {
                        while (i < maxAttemptsCount) {
                            yrItem = slot.get(stampHolder)
                            if (stampHolder[0] == State.BUSY.ordinal) {
                                slot.set(null, State.EMPTY.ordinal)
                                return Result.success(yrItem)
                            }
                            i++
                        }
                        if (slot.compareAndSet(myItem, null, State.WAITING.ordinal, State.EMPTY.ordinal)) {
                            return Result.failure(TimeoutException())
                        } else {
                            yrItem = slot.get(stampHolder)
                            slot.set(null, State.EMPTY.ordinal)
                            return Result.success(yrItem)
                        }
                    }
                }

                State.WAITING.ordinal -> {
                    if (slot.compareAndSet(yrItem, myItem, State.WAITING.ordinal, State.BUSY.ordinal)) {
                        return Result.success(yrItem)
                    }
                }
            }
            i++
        }
        return Result.failure(TimeoutException())
    }
}