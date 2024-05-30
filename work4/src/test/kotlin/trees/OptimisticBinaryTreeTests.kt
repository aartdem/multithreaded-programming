package trees

import org.junit.jupiter.api.BeforeEach

class OptimisticBinaryTreeTests : BinarySearchTreeTests() {
    @BeforeEach
    override fun setUp() {
        tree = OptimisticBinaryTree()
    }
}