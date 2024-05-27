package trees

class Node<T : Comparable<T>>(
    var key: T,
    var left: Node<T>?,
    var right: Node<T>?
)