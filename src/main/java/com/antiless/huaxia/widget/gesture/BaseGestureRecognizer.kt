package com.antiless.huaxia.widget.gesture

import android.view.MotionEvent

abstract class BaseGestureRecognizer<T : BaseGesture<T>>(val gesturePointersUtility: GesturePointersUtility) {

    val gestures = ArrayList<T>()
    private val gestureStartedListeners = ArrayList<OnGestureStartedListener<T>>()

    /** Interface definition for a callbacks to be invoked when a [BaseGesture] starts.  */
    interface OnGestureStartedListener<T : BaseGesture<T>> {
        fun onGestureStarted(gesture: T)
    }

    fun onTouch(event: MotionEvent) {
        tryCreateGestures(event)

        gestures.forEach {
            it.onTouch(event)

            if (it.justStarted) {
                dispatchGestureStarted(it)
            }
        }

        removeFinishedGestures()
    }

    private fun removeFinishedGestures() {
        for (i in gestures.indices.reversed()) {
            val gesture = gestures[i]
            if (gesture.hasFinished) {
                gestures.removeAt(i)
            }
        }
    }

    private fun dispatchGestureStarted(gesture: T) {
        gestureStartedListeners.forEach {
            it.onGestureStarted(gesture)
        }
    }

    abstract fun tryCreateGestures(event: MotionEvent)


    fun addOnGestureStartedListener(listener: OnGestureStartedListener<T>) {
        if (!gestureStartedListeners.contains(listener)) {
            gestureStartedListeners.add(listener)
        }
    }

    fun removeOnGestureStartedListener(listener: OnGestureStartedListener<T>) {
        gestureStartedListeners.remove(listener)
    }
}