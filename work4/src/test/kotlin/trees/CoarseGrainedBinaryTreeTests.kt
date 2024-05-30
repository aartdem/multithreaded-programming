package trees

import org.junit.jupiter.api.BeforeEach

class CoarseGrainedBinaryTreeTests : BinarySearchTreeTests() {
    @BeforeEach
    override fun setUp() {
        tree = CoarseGrainedBinaryTree()
    }
}