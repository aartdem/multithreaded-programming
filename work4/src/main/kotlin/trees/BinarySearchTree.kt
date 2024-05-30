package trees

import kotlinx.coroutines.sync.Mutex

abstract class BinarySearchTree<T : Comparable<T>> {
    protected var root: Node<T>? = null
    protected val treeRootMutex = Mutex()

    abstract suspend fun contains(x: T): Boolean
    abstract suspend fun add(x: T)
    abstract suspend fun remove(x: T)

    suspend fun getValues(): List<T> {
        val res = mutableListOf<T>()
        fun getValuesRecursively(currentNode: Node<T>?) {
            currentNode ?: return
            getValuesRecursively(currentNode.left)
            res.add(currentNode.key)
            getValuesRecursively(currentNode.right)
        }
        treeRootMutex.lock()
        getValuesRecursively(root)
        treeRootMutex.unlock()
        return res
    }
}