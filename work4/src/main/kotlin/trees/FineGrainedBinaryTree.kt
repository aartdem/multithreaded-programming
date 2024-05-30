package trees

class FineGrainedBinaryTree<T : Comparable<T>> : BinarySearchTree<T>() {
    override suspend fun contains(x: T): Boolean {
        suspend fun containsRecursively(currentNode: Node<T>): Boolean {
            if (x == currentNode.key) {
                currentNode.unlock()
                return true
            } else if (x < currentNode.key) {
                if (currentNode.left == null) {
                    currentNode.unlock()
                    return false
                } else {
                    currentNode.left!!.lock()
                    currentNode.unlock()
                    return containsRecursively(currentNode.left!!)
                }
            } else {
                if (currentNode.right == null) {
                    currentNode.unlock()
                    return false
                } else {
                    currentNode.right!!.lock()
                    currentNode.unlock()
                    return containsRecursively(currentNode.right!!)
                }
            }
        }
        treeRootMutex.lock()
        if (root == null) {
            treeRootMutex.unlock()
            return false
        } else {
            root!!.lock()
            treeRootMutex.unlock()
            return containsRecursively(root!!)
        }
    }

    override suspend fun add(x: T) {
        suspend fun addRecursively(currentNode: Node<T>) {
            if (x == currentNode.key) {
                currentNode.unlock()
            } else if (x < currentNode.key) {
                if (currentNode.left == null) {
                    currentNode.left = Node(x, null, null)
                    currentNode.unlock()
                } else {
                    currentNode.left!!.lock()
                    currentNode.unlock()
                    addRecursively(currentNode.left!!)
                }
            } else {
                if (currentNode.right == null) {
                    currentNode.right = Node(x, null, null)
                    currentNode.unlock()
                } else {
                    currentNode.right!!.lock()
                    currentNode.unlock()
                    addRecursively(currentNode.right!!)
                }
            }
        }
        treeRootMutex.lock()
        if (root == null) {
            root = Node(x, null, null)
            treeRootMutex.unlock()
        } else {
            root!!.lock()
            treeRootMutex.unlock()
            addRecursively(root!!)
        }
    }

    override suspend fun remove(x: T) {
        suspend fun findRightSuccessor(node: Node<T>): Node<T> {
            node.right?.lock()
            if (node.right == null) {
                return node
            }
            var currentNode = node.right!!
            while (currentNode.right != null) {
                currentNode.right!!.lock()
                currentNode.unlock()
                currentNode = currentNode.right!!
            }
            return currentNode
        }

        /**
         * Return locked node
         */
        suspend fun removeNode(nodeToRemove: Node<T>): Node<T>? {
            nodeToRemove.left?.lock()
            nodeToRemove.right?.lock()
            val newNode = if (nodeToRemove.left != null && nodeToRemove.right != null) {
                val rightSuccessor = findRightSuccessor(nodeToRemove.left!!)
                rightSuccessor.right = nodeToRemove.right
                nodeToRemove.right?.unlock()
                if (rightSuccessor != nodeToRemove.left) rightSuccessor.unlock()
                nodeToRemove.left
            } else if (nodeToRemove.left == null) {
                nodeToRemove.right
            } else {
                nodeToRemove.left
            }
            return newNode
        }

        suspend fun removeRecursively(currentNode: Node<T>, parentNode: Node<T>?, left: Boolean) {
            if (x == currentNode.key) {
                val newNode = removeNode(currentNode)
                if (parentNode == null) {
                    root = newNode
                } else if (left) {
                    parentNode.left = newNode
                } else {
                    parentNode.right = newNode
                }
                currentNode.unlock()
                newNode?.unlock()
                parentNode?.unlock()
            } else if (x < currentNode.key) {
                currentNode.left?.lock()
                parentNode?.unlock()
                currentNode.left?.let {
                    removeRecursively(it, currentNode, true)
                } ?: run {
                    currentNode.unlock()
                }
            } else {
                currentNode.right?.lock()
                parentNode?.unlock()
                currentNode.right?.let {
                    removeRecursively(it, currentNode, false)
                } ?: run {
                    currentNode.unlock()
                }
            }
        }
        treeRootMutex.lock()
        if (root == null) {
            treeRootMutex.unlock()
        } else {
            root!!.lock()
            treeRootMutex.unlock()
            removeRecursively(root!!, null, true)
        }
    }

}