package org.example

import kotlinx.coroutines.sync.Mutex

class TwoMutex() {
    private val m1 = Mutex()
    private val m2 = Mutex()
    suspend fun captureMutex1() {
        m1.lock()
        m2.lock()

    }

    suspend fun captureMutex2() {
        m2.lock()
        m1.lock()

    }
}