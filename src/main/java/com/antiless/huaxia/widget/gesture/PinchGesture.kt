package com.antiless.huaxia.widget.gesture

import android.graphics.PointF
import android.view.MotionEvent
import com.antiless.huaxia.widget.gesture.BaseGesture
import kotlin.math.abs
import kotlin.math.cos

class PinchGesture(gesturePointersUtility: GesturePointersUtility, motionEvent: MotionEvent, val pointerId2: Int) : BaseGesture<PinchGesture>(gesturePointersUtility) {
    val pointerId1 = motionEvent.getPointerId(motionEvent.actionIndex)
    val startPosition1 = GesturePointersUtility.motionEventToPosition(motionEvent, pointerId1)
    val startPosition2 = GesturePointersUtility.motionEventToPosition(motionEvent, pointerId2)
    val previousPosition1 = PointF(startPosition1.x, startPosition1.y)
    val previousPosition2 = PointF(startPosition2.x, startPosition2.y)
    var gap: Float = 0f
    var gapDelta: Float = 0f

    var startGap = startPosition1.distance(startPosition2)

    companion object {
        private val SLOP_MOTION_DIRECTION_DEGREES = 30.0f
        private val SLOP_INCHES = 0.01f
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

        val newGap = newPosition1.distance(newPosition2)

        if (newGap == gap) {
            return false
        }

        gapDelta = newGap - gap
        gap = newGap

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

        val firstToSecond = PointF(startPosition2.x - startPosition1.x, startPosition2.y - startPosition1.y)
        val firstToSecondDirection = firstToSecond.normalized()
        val firstToSecondDirectionNegate = PointF(-firstToSecondDirection.x, -firstToSecondDirection.y)

        val deltaPosition1 = newPosition1.sub(previousPosition1)
        val deltaPosition2 = newPosition2.sub(previousPosition2)

        previousPosition1.set(newPosition1)
        previousPosition2.set(newPosition2)

        val dot1 = deltaPosition1.normalized().dot(firstToSecondDirectionNegate)
        val dot2 = deltaPosition2.normalized().dot(firstToSecondDirection)
        val dotThreshold = cos(Math.toRadians(SLOP_MOTION_DIRECTION_DEGREES.toDouble())).toFloat()
//        val dotThreshold = SLOP_MOTION_DIRECTION_DEGREES
        val degree = deltaPosition1.degreeTo(deltaPosition2)

        if (abs(degree) < 90) {
            return false
        }
        if (deltaPosition1.distance(ZERO) != 0f && abs(dot1) < dotThreshold) {
            return false
        }

        if (deltaPosition2.distance(ZERO) != 0f && abs(dot2) < dotThreshold) {
            return false
        }

        val startGap = startPosition1.distance(startPosition2)
        gap = newPosition1.distance(newPosition2)

        val separation = abs(startGap - gap)
        val slopPixels = gesturePointersUtility.inchesToPixels(SLOP_INCHES)

        if (separation < slopPixels) {
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

    override fun getSelf(): PinchGesture {
        return this
    }
}