package com.antiless.huaxia.widget.gesture

import android.view.MotionEvent

abstract class BaseGesture<T : BaseGesture<T>>(val gesturePointersUtility: GesturePointersUtility) {
    /** Interface definition for callbacks to be invoked by a [BaseGesture].  */
    interface OnGestureEventListener<T : BaseGesture<T>> {
        fun onUpdated(gesture: T)

        fun onFinished(gesture: T)
    }

    var hasStarted: Boolean = false
    var justStarted: Boolean = false
    var hasFinished: Boolean = false
    var wasCancelled: Boolean = false

    var eventListener: OnGestureEventListener<T>? = null

    fun onTouch(event: MotionEvent) {
        if (!hasStarted && canStart(event)) {
            start(event)
            return
        }
        justStarted = false
        if (hasStarted) {
            if (updateGesture(event)) {
                dispatchUpdateEvent()
            }
        }
    }

    private fun dispatchFinishedEvent() {
        eventListener?.onFinished(getSelf())
    }

    abstract fun updateGesture(event: MotionEvent): Boolean

    abstract fun canStart(event: MotionEvent): Boolean

    abstract fun onStart(event: MotionEvent)

    private fun dispatchUpdateEvent() {
        eventListener?.onUpdated(getSelf())
    }

    protected abstract fun onCancel()

    protected abstract fun onFinish()

    fun cancel() {
        wasCancelled = true
        onCancel()
        complete()
    }

    protected fun complete() {
        hasFinished = true
        if (hasStarted) {
            onFinish()
            dispatchFinishedEvent()
        }
    }

    fun start(event: MotionEvent) {
        hasStarted = true
        justStarted = true
        onStart(event)
    }

    protected abstract fun getSelf(): T
}