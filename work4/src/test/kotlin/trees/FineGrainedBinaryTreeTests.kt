package trees

import org.junit.jupiter.api.BeforeEach

class FineGrainedBinaryTreeTests : BinarySearchTreeTests() {
    @BeforeEach
    override fun setUp() {
        tree = FineGrainedBinaryTree()
    }
}