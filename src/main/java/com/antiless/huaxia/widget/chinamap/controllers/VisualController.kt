package com.antiless.huaxia.widget.chinamap.controllers

import android.graphics.PointF
import com.antiless.huaxia.widget.chinamap.ChinaMapView
import com.antiless.huaxia.widget.gesture.BaseTransformNode
import com.antiless.huaxia.widget.gesture.BaseTransformationController
import com.antiless.huaxia.widget.gesture.SwipeGesture
import com.antiless.huaxia.widget.gesture.SwipeGestureRecognizer

class VisualController(transformNode: BaseTransformNode, recognizer: SwipeGestureRecognizer) : BaseTransformationController<SwipeGesture>(transformNode, recognizer) {
    override fun canStartTransformation(gesture: SwipeGesture): Boolean {
        return true
    }

    var lastDistance = PointF()
    override fun onContinueTransformation(gesture: SwipeGesture) {
        if (transformNode.view is ChinaMapView) {
//            val offsetX = (gesture.delta.x / 300f * 45f)
//            val offsetY = (gesture.delta.y / 300f * 45f)
            transformNode.view.translate(gesture.delta.x, gesture.delta.y)
        }
    }

    override fun onEndTransformation(gesture: SwipeGesture) {
        lastDistance = PointF()
    }
}