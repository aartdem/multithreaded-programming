package trees

import kotlinx.coroutines.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.random.Random

abstract class BinarySearchTreeTests {
    protected lateinit var tree: BinarySearchTree<Int>
    private val rnd = Random(0)

    @BeforeEach
    abstract fun setUp()

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    @ParameterizedTest
    @MethodSource("threadNumsProvider")
    fun addingValuesTest(threadsNum: Int) {
        val valuesToAddLists = List(threadsNum) { List(5000) { rnd.nextInt(5000) } }
        runBlocking {
            valuesToAddLists.forEachIndexed { id, list ->
                launch(newSingleThreadContext("Thread$id")) {
                    list.forEach { tree.add(it) }
                }
            }
        }
        runBlocking {
            valuesToAddLists.forEachIndexed { id, list ->
                launch(newSingleThreadContext("Thread$id")) {
                    list.forEach { Assertions.assertTrue(tree.contains(it)) }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    @ParameterizedTest
    @MethodSource("threadNumsProvider")
    fun deletingValuesTest(threadsNum: Int) {
        val valuesToRemoveLists = List(threadsNum) { List(5000) { rnd.nextInt(5000) } }
        val jobs = mutableListOf<Job>()
        runBlocking {
            valuesToRemoveLists.forEachIndexed { id, list ->
                launch(newSingleThreadContext("Thread$id")) {
                    list.forEach { tree.add(it) }
                }.let {
                    jobs.add(it)
                }
            }

            jobs.forEach {
                it.join()
            }
            valuesToRemoveLists.forEachIndexed { id, list ->
                launch(newSingleThreadContext("Thread$id")) {
                    list.forEach { tree.remove(it) }
                }
            }
        }
        runBlocking {
            valuesToRemoveLists.forEachIndexed { id, list ->
                launch(newSingleThreadContext("Thread$id")) {
                    list.forEach { Assertions.assertFalse(tree.contains(it)) }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun threadNumsProvider(): List<Arguments> =
            listOf(Arguments.of(1), Arguments.of(2), Arguments.of(4), Arguments.of(6))
    }
}
