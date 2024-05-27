import kotlinx.coroutines.runBlocking
import trees.CoarseGrainedBinaryTree


fun main(): Unit = runBlocking {
    val tree = CoarseGrainedBinaryTree<Int>()
    tree.add(2)
    tree.add(1)
    tree.add(3)
    tree.contains(1)
    tree.remove(1)
    tree.contains(1)
}