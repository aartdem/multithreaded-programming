package stack

import stack.simple.ConcurrentTreiberStack
import javax.print.attribute.standard.JobName
import kotlin.concurrent.thread
import kotlin.coroutines.coroutineContext

fun main() {
    val st = ConcurrentTreiberStack<Int>()
    st.push(1)
    st.pop()

    val threads = List(10) {
        Thread {
            println(Thread.currentThread().id)
        }
    }
    threads.forEach { it.start() }

    threads.forEach { it.join() }
}
