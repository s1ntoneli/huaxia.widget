package com.antiless.huaxia.widget.gesture

import android.graphics.PointF
import android.view.MotionEvent
import com.antiless.huaxia.widget.gesture.BaseGesture
import kotlin.math.abs

class DoubleFingerMoveGesture(gesturePointersUtility: GesturePointersUtility, motionEvent: MotionEvent, val pointerId2: Int) : BaseGesture<DoubleFingerMoveGesture>(gesturePointersUtility) {
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
        private val SLOP_INCHES = 0.05f
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
        previousPosition1.set(newPosition1)
        previousPosition2.set(newPosition2)

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

        val deltaPosition1 = newPosition1.sub(startPosition1)
        val deltaPosition2 = newPosition2.sub(startPosition2)

        previousPosition1.set(newPosition1)
        previousPosition2.set(newPosition2)

//        val dot1 = deltaPosition1.normalized().dot(firstToSecondDirection)
//        val dot2 = deltaPosition2.normalized().dot(firstToSecondDirection)
//        val dotThreshold = SLOP_MOTION_DIRECTION_DEGREES
        val degree = deltaPosition1.degreeTo(deltaPosition2)
//        val dotThreshold = cos(Math.toRadians(SLOP_MOTION_DIRECTION_DEGREES.toDouble())).toFloat()

//        if (deltaPosition1.distance(ZERO) != 0f && abs(dot1) < dotThreshold) {
//            return false
//        }

        if (abs(degree) > SLOP_MOTION_DIRECTION_DEGREES) {
            return false
        }

        val startGap = startPosition1.distance(startPosition2)
        gap = newPosition1.distance(newPosition2)

        val centerStart = startPosition1.center(startPosition2)
        val centerNow = newPosition1.center(newPosition2)

        val separation = centerNow.distance(centerStart)
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

    override fun getSelf(): DoubleFingerMoveGesture {
        return this
    }
}