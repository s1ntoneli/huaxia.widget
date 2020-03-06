package com.antiless.huaxia.widget.gesture

import android.graphics.PointF
import android.util.Log
import android.view.MotionEvent
import com.antiless.huaxia.widget.gesture.BaseGesture

class SwipeGesture(gesturePointersUtility: GesturePointersUtility, motionEvent: MotionEvent) : BaseGesture<SwipeGesture>(gesturePointersUtility) {
    private val pointerId = motionEvent.getPointerId(motionEvent.actionIndex)
    private val startPosition = GesturePointersUtility.motionEventToPosition(motionEvent, pointerId)
    private val position = PointF(startPosition.x, startPosition.y)
    val delta = PointF(0f, 0f)

    companion object {
        private val TAG = SwipeGesture::class.java.getSimpleName()
        private const val SLOP_INCHES = 0.05f
        private const val DRAG_GESTURE_DEBUG = false
    }

    override fun updateGesture(event: MotionEvent): Boolean {
        val actionId = event.getPointerId(event.actionIndex)
        val action = event.actionMasked

        if (action == MotionEvent.ACTION_CANCEL) {
            cancel()
            return false
        }

        val touchEnded = action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP

        if (touchEnded && actionId == pointerId) {
            complete()
            return false
        }

        if (action != MotionEvent.ACTION_MOVE) {
            return false
        }

        val newPosition = GesturePointersUtility.motionEventToPosition(event, pointerId)
        if (newPosition != position) {
            delta.set(newPosition.sub(position))
            position.set(newPosition)
            debugLog("updated: $pointerId $position")
            return true
        }
        return false
    }

    override fun canStart(event: MotionEvent): Boolean {
        val actionId = event.getPointerId(event.actionIndex)
        val action = event.actionMasked

        if (gesturePointersUtility.isPointerIdRetained(actionId)) {
            cancel()
            return false
        }
        if (action == MotionEvent.ACTION_CANCEL) {
            cancel()
            return false
        }

        val touchEnd = action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP
        if (touchEnd && (actionId == pointerId)) {
            cancel()
            return false
        }

        if (action != MotionEvent.ACTION_MOVE) {
            return false
        }

        if (event.pointerCount > 1) {
            for (i in 0 until event.pointerCount) {
                val id = event.getPointerId(i)
                if (id != pointerId && !gesturePointersUtility.isPointerIdRetained(id)) {
                    return false
                }
            }
        }

        val newPosition = GesturePointersUtility.motionEventToPosition(event, pointerId)
        val diff = newPosition.distance(startPosition)
        val slopPixels = gesturePointersUtility.inchesToPixels(SLOP_INCHES)
        debugLog("slopPixels $slopPixels")
        if (diff > slopPixels) {
            return true
        }

        return false
    }

    override fun onStart(event: MotionEvent) {
        position.set(GesturePointersUtility.motionEventToPosition(event, pointerId))
        gesturePointersUtility.retainPointerId(pointerId)
    }

    override fun onCancel() {
    }

    override fun onFinish() {
        gesturePointersUtility.releasePointerId(pointerId)
    }

    override fun getSelf(): SwipeGesture {
        return this
    }

    private fun debugLog(log: String) {
        if (DRAG_GESTURE_DEBUG) {
            Log.d(TAG, "DragGesture:[$log]")
        }
    }
}