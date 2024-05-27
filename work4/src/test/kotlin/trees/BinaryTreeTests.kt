package trees

import kotlinx.coroutines.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import kotlin.random.Random
import kotlin.test.Test

class BinaryTreeTests {
    private lateinit var tree: CoarseGrainedBinaryTree<Int>

    @BeforeEach
    fun setUp() {
        tree = CoarseGrainedBinaryTree()
    }

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    @Test
    fun addingValuesTwoThreadsTest() {
        val valuesToAdd1 = List(1000) { Random.nextInt(1000) }
        val valuesToAdd2 = List(1000) { Random.nextInt(1000) }
        runBlocking {
            launch(newSingleThreadContext("Thread1")) {
                valuesToAdd1.forEach { tree.add(it) }
            }
            launch(newSingleThreadContext("Thread2")) {
                valuesToAdd2.forEach { tree.add(it) }
            }
        }
        Assertions.assertEquals(tree.getValues(), (valuesToAdd1 + valuesToAdd2).sorted().distinct())
    }

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    @Test
    fun deletingValuesTwoThreadsTest() {
        val valuesToRemove1 = List(1000) { Random.nextInt(1000) }
        val valuesToRemove2 = List(1000) { Random.nextInt(1000) }
        runBlocking {
            val j1 = launch(newSingleThreadContext("Thread1")) {
                valuesToRemove1.forEach { tree.add(it) }
            }
            val j2 = launch(newSingleThreadContext("Thread2")) {
                valuesToRemove2.forEach { tree.add(it) }
            }
            j1.join()
            j2.join()

            launch(newSingleThreadContext("Thread1")) {
                valuesToRemove1.forEach { tree.remove(it) }
            }
            launch(newSingleThreadContext("Thread2")) {
                valuesToRemove2.forEach { tree.remove(it) }
            }
        }
        Assertions.assertEquals(tree.getValues(), emptyList<Int>())
    }
}