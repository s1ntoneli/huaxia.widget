package com.antiless.huaxia.widget.gesture

import android.view.MotionEvent
import com.antiless.huaxia.widget.gesture.BaseGestureRecognizer

class TwistGestureRecognizer(gesturePointersUtility: GesturePointersUtility) : BaseGestureRecognizer<TwistGesture>(gesturePointersUtility) {
    override fun tryCreateGestures(event: MotionEvent) {
        // Pinch gestures require at least two fingers to be touching.
        if (event.pointerCount < 2) {
            return
        }

        val actionId = event.getPointerId(event.actionIndex)
        val action = event.actionMasked

        val touchBegan =
                action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN

        if (!touchBegan || gesturePointersUtility.isPointerIdRetained(actionId)) {
            return
        }

        // Determine if there is another pointer Id that has not yet been retained.
        for (it in 0 until event.pointerCount) {
            val pointerId = event.getPointerId(it)
            if (pointerId == actionId) {
                continue
            }

            if (gesturePointersUtility.isPointerIdRetained(pointerId)) {
                continue
            }

            gestures.add(TwistGesture(gesturePointersUtility, event, pointerId))
        }
    }

}