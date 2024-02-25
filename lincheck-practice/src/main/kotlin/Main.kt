package org.example

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    println("Start")
    coroutineScope {
        val aboba = TwoMutex()
        launch {
            aboba.captureMutex1()
        }
        launch {
            aboba.captureMutex2()
        }
    }
    println("Done")
}
