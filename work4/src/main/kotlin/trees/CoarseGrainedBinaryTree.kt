package trees

import kotlinx.coroutines.sync.Mutex

class CoarseGrainedBinaryTree<T : Comparable<T>> {
    private var root: Node<T>? = null
    private val rootMutex = Mutex()

    suspend fun contains(x: T): Boolean {
        fun containsRecursively(currentNode: Node<T>?): Boolean {
            currentNode ?: return false

            return if (x == currentNode.key) {
                true
            } else if (x < currentNode.key) {
                containsRecursively(currentNode.left)
            } else {
                containsRecursively(currentNode.right)
            }
        }
        rootMutex.lock()
        val result = containsRecursively(root)
        rootMutex.unlock()
        return result
    }

    suspend fun add(x: T) {
        fun addRecursively(currentNode: Node<T>?): Node<T> {
            currentNode ?: return Node(x, null, null)

            if (x < currentNode.key) {
                currentNode.left = addRecursively(currentNode.left)
            } else if (x > currentNode.key) {
                currentNode.right = addRecursively(currentNode.right)
            }
            return currentNode
        }
        rootMutex.lock()
        root = addRecursively(root)
        rootMutex.unlock()
    }

    suspend fun remove(x: T) {
        fun removeNode(node: Node<T>): Node<T>? {
            if (node.left != null && node.right != null) {
                var curChild = node.left
                while (curChild?.right != null) {
                    curChild = curChild.right
                }
                curChild?.right = node.right
                return node.left
            } else if (node.left == null) {
                return node.right
            } else {
                return node.left
            }
        }

        fun removeRecursively(currentNode: Node<T>?): Node<T>? {
            currentNode ?: return null

            if (x == currentNode.key) {
                return removeNode(currentNode)
            } else if (x < currentNode.key) {
                currentNode.left = removeRecursively(currentNode.left)
            } else {
                currentNode.right = removeRecursively(currentNode.right)
            }
            return currentNode
        }
        rootMutex.lock()
        root = removeRecursively(root)
        rootMutex.unlock()
    }

    fun getValues(): List<T> {
        val res = mutableListOf<T>()
        fun getValuesRecursively(currentNode: Node<T>?) {
            currentNode ?: return
            getValuesRecursively(currentNode.left)
            res.add(currentNode.key)
            getValuesRecursively(currentNode.right)
        }
        getValuesRecursively(root)
        return res
    }
}