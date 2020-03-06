package com.antiless.huaxia.widget.gesture

import android.util.DisplayMetrics
import android.view.MotionEvent
import com.antiless.huaxia.widget.gesture.BaseGestureRecognizer
import com.antiless.huaxia.widget.gesture.SwipeGestureRecognizer


class TransformSystem(val ds: DisplayMetrics, gesturePointersUtility: GesturePointersUtility) {

    val recognizers = ArrayList<BaseGestureRecognizer<*>>()

    var pinchGestureRecognizer: PinchGestureRecognizer = PinchGestureRecognizer(gesturePointersUtility)
    var doubleFingerMoveGestureRecognizer: DoubleFinglerMoveGestureRecognizer = DoubleFinglerMoveGestureRecognizer(gesturePointersUtility)
    var twistGestureRecognizer: TwistGestureRecognizer = TwistGestureRecognizer(gesturePointersUtility)
    var swipeGestureRecognizer: SwipeGestureRecognizer = SwipeGestureRecognizer(gesturePointersUtility)

    init {
        addGestureRecognizer(twistGestureRecognizer)
        addGestureRecognizer(doubleFingerMoveGestureRecognizer)
        addGestureRecognizer(pinchGestureRecognizer)
        addGestureRecognizer(swipeGestureRecognizer)
    }

    /** Dispatches touch events to the gesture recognizers contained by this transformation system. */
    fun onTouch(event: MotionEvent) {
        for (i in recognizers.indices) {
            recognizers[i].onTouch(event)
        }
    }

    /**
     * Adds a gesture recognizer to this transformation system. Touch events will be dispatched to the
     * recognizer when [.onTouch] is called.
     */
    fun addGestureRecognizer(gestureRecognizer: BaseGestureRecognizer<*>) {
        recognizers.add(gestureRecognizer)
    }
}