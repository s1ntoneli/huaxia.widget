package com.antiless.huaxia.widget.gesture

import android.graphics.PointF
import android.util.Log
import android.view.MotionEvent
import com.antiless.huaxia.widget.BuildConfig
import kotlin.math.abs

class TwistGesture(gesturePointersUtility: GesturePointersUtility, motionEvent: MotionEvent, val pointerId2: Int) : BaseGesture<TwistGesture>(gesturePointersUtility) {
    private val pointerId1 = motionEvent.getPointerId(motionEvent.actionIndex)
    private val startPosition1 = GesturePointersUtility.motionEventToPosition(motionEvent, pointerId1)
    private val startPosition2 = GesturePointersUtility.motionEventToPosition(motionEvent, pointerId2)
    private val previousPosition1 = PointF(startPosition1.x, startPosition1.y)
    private val previousPosition2 = PointF(startPosition2.x, startPosition2.y)

    var deltaRotationDegrees: Float = 0f
    var startCenterPoint = startPosition1.center(startPosition2)


    companion object {
        private val SLOP_ROTATION_DEGREES = 15.0f
        private val TWIST_GESTURE_DEBUG = BuildConfig.DEBUG
        private val TAG = TwistGesture::class.java.simpleName
    }

    override fun updateGesture(event: MotionEvent): Boolean {
        val actionId = event.getPointerId(event.actionIndex)
        val action = event.actionMasked

        if (action == MotionEvent.ACTION_CANCEL) {
            cancel()
            return false
        }

        val touchEnded = action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP

        if (touchEnded && (actionId == pointerId1 || actionId == pointerId2)) {
            complete()
            return false
        }

        if (action != MotionEvent.ACTION_MOVE) {
            return false
        }

        val newPosition1 = GesturePointersUtility.motionEventToPosition(event, pointerId1)
        val newPosition2 = GesturePointersUtility.motionEventToPosition(event, pointerId2)

        deltaRotationDegrees = previousPosition1.sub(previousPosition2).degreeTo(newPosition1.sub(newPosition2))

        previousPosition1.set(newPosition1)
        previousPosition2.set(newPosition2)


        return true
    }

    override fun canStart(event: MotionEvent): Boolean {
        if (gesturePointersUtility.isPointerIdRetained(pointerId1) ||
                gesturePointersUtility.isPointerIdRetained(pointerId2)) {
            cancel()
            return false
        }

        val actionId = event.getPointerId(event.actionIndex)
        val action = event.actionMasked

        if (action == MotionEvent.ACTION_CANCEL) {
            cancel()
            return false
        }

        val touchEnd = action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP
        if (touchEnd && (actionId == pointerId1 || actionId == pointerId2)) {
            cancel()
            return false
        }

        if (action != MotionEvent.ACTION_MOVE) {
            return false
        }

        val newPosition1 = GesturePointersUtility.motionEventToPosition(event, pointerId1)
        val newPosition2 = GesturePointersUtility.motionEventToPosition(event, pointerId2)

        val deltaPosition1 = newPosition1.sub(previousPosition1)
        val deltaPosition2 = newPosition2.sub(previousPosition2)

        previousPosition1.set(newPosition1)
        previousPosition2.set(newPosition2)

        debugLog("deltaPosition $deltaPosition1 $deltaPosition2")
        if (deltaPosition1.equals(0f, 0f) || deltaPosition2.equals(0f, 0f)) {
            return false
        }

        val rotation = startPosition1.sub(startPosition2).degreeTo(newPosition1.sub(newPosition2))
        debugLog("rotation $rotation")
        if (abs(rotation) < SLOP_ROTATION_DEGREES) {
            return false
        }

        return true
    }

    override fun onStart(event: MotionEvent) {
        gesturePointersUtility.retainPointerId(pointerId1)
        gesturePointersUtility.retainPointerId(pointerId2)
    }

    override fun onCancel() {
    }

    override fun onFinish() {
        gesturePointersUtility.releasePointerId(pointerId1)
        gesturePointersUtility.releasePointerId(pointerId2)
    }

    override fun getSelf(): TwistGesture {
        return this
    }

    private fun debugLog(log: String) {
        if (TWIST_GESTURE_DEBUG) {
            Log.d(TAG, "TwistGesture:[$log]")
        }
    }
}