package com.antiless.huaxia.widget.chinamap.controllers

import com.antiless.huaxia.widget.chinamap.ChinaMapView
import com.antiless.huaxia.widget.gesture.BaseTransformNode
import com.antiless.huaxia.widget.gesture.BaseTransformationController
import com.antiless.huaxia.widget.gesture.TwistGesture
import com.antiless.huaxia.widget.gesture.TwistGestureRecognizer

class RotateController(transformNode: BaseTransformNode, recognizer: TwistGestureRecognizer) : BaseTransformationController<TwistGesture>(transformNode, recognizer) {
    override fun canStartTransformation(gesture: TwistGesture): Boolean {
        return true
    }

    var lastRotate = 0f
    override fun onContinueTransformation(gesture: TwistGesture) {
        if (transformNode.view is ChinaMapView) {
            transformNode.view.rotate(gesture.deltaRotationDegrees, gesture.startCenterPoint.x, gesture.startCenterPoint.y)
        }
    }

    override fun onEndTransformation(gesture: TwistGesture) {
        lastRotate = 0f
    }
}