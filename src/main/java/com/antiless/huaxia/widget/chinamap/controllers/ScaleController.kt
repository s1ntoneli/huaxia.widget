package com.antiless.huaxia.widget.chinamap.controllers

import com.antiless.huaxia.widget.chinamap.ChinaMapView
import com.antiless.huaxia.widget.gesture.*

class ScaleController(transformNode: BaseTransformNode, recognizer: PinchGestureRecognizer) : BaseTransformationController<PinchGesture>(transformNode, recognizer) {
    override fun canStartTransformation(gesture: PinchGesture): Boolean {
        return true
    }

    var lastRatio = 1f
    override fun onContinueTransformation(gesture: PinchGesture) {
        if (transformNode.view is ChinaMapView) {
            val pinchRatio = gesture.gap / gesture.startGap
            val startCenterPoint = gesture.startPosition1.center(gesture.startPosition2)
            val scale = pinchRatio / lastRatio
            lastRatio = pinchRatio
            transformNode.view.scale(scale, startCenterPoint.x, startCenterPoint.y)
        }
    }

    override fun onEndTransformation(gesture: PinchGesture) {
        lastRatio = 1f
    }
}