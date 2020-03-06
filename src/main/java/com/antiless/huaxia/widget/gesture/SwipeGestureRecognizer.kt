package com.antiless.huaxia.widget.gesture

import android.view.MotionEvent

class SwipeGestureRecognizer(gesturePointersUtility: GesturePointersUtility) : BaseGestureRecognizer<SwipeGesture>(gesturePointersUtility) {
    override fun tryCreateGestures(event: MotionEvent) {
        if (event.pointerCount > 1) {
            return
        }
        val actionId = event.getPointerId(event.actionIndex)
        val action = event.actionMasked

        val touchBegan =
                action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN

        if (!touchBegan || gesturePointersUtility.isPointerIdRetained(actionId)) {
            return
        }

        gestures.add(SwipeGesture(gesturePointersUtility, event))
    }

}