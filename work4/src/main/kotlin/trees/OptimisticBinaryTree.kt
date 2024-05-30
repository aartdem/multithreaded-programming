package trees

class OptimisticBinaryTree<T : Comparable<T>> : BinarySearchTree<T>() {
    override suspend fun contains(x: T): Boolean {
        treeRootMutex.lock()
        if (root == null) {
            treeRootMutex.unlock()
            return false
        } else {
            treeRootMutex.unlock()
            while (true) {
                val pair1 = searchHelper(root, null, x)
                pair1.first ?: return false
                pair1.first?.lock()
                pair1.second?.lock()

                val pair2 = searchHelper(root, null, x)
                if (pair1.first === pair2.first) {
                    pair2.first?.unlock()
                    pair2.second?.unlock()
                    return true
                }
            }
        }
    }

    override suspend fun add(x: T) {
        treeRootMutex.lock()
        if (root == null) {
            root = Node(x, null, null)
            treeRootMutex.unlock()
        } else {
            treeRootMutex.unlock()

            while (true) {
                val pair1 = searchHelper(root, null, x)
                pair1.first?.let { return }
                pair1.second?.lock()

                val pair2 = searchHelper(root, null, x)
                if (pair2.first != null || pair2.second !== pair1.second) {
                    pair1.second?.unlock()
                    continue
                }

                if (x < pair2.second!!.key) {
                    pair2.second?.left = Node(x, null, null)
                } else if (x > pair2.second!!.key) {
                    pair2.second?.right = Node(x, null, null)
                }
                pair2.second?.unlock()
                return
            }
        }
    }

    override suspend fun remove(x: T) {
        fun findRightSuccessor(node: Node<T>): Node<T> {
            var currentNode = node
            while (currentNode.right != null) {
                currentNode = currentNode.right!!
            }
            return currentNode
        }

        fun removeNode(nodeToRemove: Node<T>): Node<T>? {
            val newNode = if (nodeToRemove.left != null && nodeToRemove.right != null) {
                val rightSuccessor = findRightSuccessor(nodeToRemove.left!!)
                rightSuccessor.right = nodeToRemove.right
                nodeToRemove.left
            } else if (nodeToRemove.left == null) {
                nodeToRemove.right
            } else {
                nodeToRemove.left
            }
            return newNode
        }

        treeRootMutex.lock()
        if (root == null) {
            treeRootMutex.unlock()
            return
        }

        treeRootMutex.unlock()
        while (true) {
            val pair1 = searchHelper(root, null, x)
            pair1.first ?: return
            val nodeToDelete = pair1.first!!
            val parentNode = pair1.second
            nodeToDelete.lock()
            parentNode?.lock()
            val pair2 = searchHelper(root, null, x)
            if (nodeToDelete !== pair2.first || parentNode !== pair2.second) {
                nodeToDelete.unlock()
                parentNode?.unlock()
                continue
            }

            val newNode = removeNode(nodeToDelete)
            if (parentNode == null) {
                root = newNode
            } else if (x < parentNode.key) {
                parentNode.left = newNode
            } else if (x > parentNode.key) {
                parentNode.right = newNode
            }

            nodeToDelete.unlock()
            parentNode?.unlock()
            return
        }
    }

    private fun searchHelper(currentNode: Node<T>?, parentNode: Node<T>?, x: T): Pair<Node<T>?, Node<T>?> {
        currentNode ?: return Pair(null, parentNode)
        return if (currentNode.key == x) {
            Pair(currentNode, parentNode)
        } else if (x < currentNode.key) {
            searchHelper(currentNode.left, currentNode, x)
        } else {
            searchHelper(currentNode.right, currentNode, x)
        }
    }
}