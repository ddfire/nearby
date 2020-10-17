package us.faerman.investing.nearby.utils

import android.os.SystemClock
import kotlinx.coroutines.*

class DragEventHandler<T>(
    private val valuesEvery: Long,
    private val unfiltered: Boolean,
    private val listener: DragEventListener<T>
) {
    private var lastValue: T? = null
    private var start: Long = 0
    private var job: Job? = null
    private val halfTime = valuesEvery / 2
    private val stopTime = (valuesEvery * 1.5).toLong()
    private var lastJobStart: Long = 0

    fun onDragEvent(value: T) {
        lastValue = value
        if (start == 0L) {
            start = SystemClock.elapsedRealtime()
            listener.onDragStarted(value)
            return
        }
        val now = SystemClock.elapsedRealtime()

        if (lastJobStart == 0L) {
            lastJobStart = now
            startJob()
        }

        if (now - lastJobStart > halfTime) {
            lastJobStart = now
            startJob()
        }

        if (now - start > valuesEvery) {
            start = now
            listener.onIntermediateValue(value)
        }
        if (unfiltered) {
            listener.onUnfiltered(value)
        }
    }

    private fun startJob() {
        job?.cancel()
        job = GlobalScope.launch(Dispatchers.Default) {
            delay(stopTime)
            if (isActive) {
                listener.onDragStopped(lastValue!!)
                lastJobStart = 0
                start = 0
            }
        }
    }

    interface DragEventListener<T> {
        fun onDragStarted(value: T)
        fun onDragStopped(value: T)
        fun onIntermediateValue(value: T)
        fun onUnfiltered(value: T) {}
    }
}