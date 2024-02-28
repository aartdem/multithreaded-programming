import kotlinx.coroutines.sync.Mutex
import org.jetbrains.kotlinx.lincheck.LinChecker
import org.jetbrains.kotlinx.lincheck.LoggingLevel
import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.ModelCheckingOptions
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import kotlin.test.Test

class TwoMutexTest {
    private val m1 = Mutex()
    private val m2 = Mutex()
    @Operation
    suspend fun captureMutex1() {
        m1.lock()
        m2.lock()
        // some work
        m1.unlock()
        m2.unlock()
    }
    @Operation
    suspend fun captureMutex2() {
        m2.lock()
        m1.lock()
        // some work
        m2.unlock()
        m1.unlock()
    }

    @Test
    fun test() = ModelCheckingOptions().check(this::class)

}