package com.antiless.huaxia.widget.chinamap.controllers

import android.graphics.PointF
import com.antiless.huaxia.widget.chinamap.ChinaMapView
import com.antiless.huaxia.widget.gesture.*

class DragController(transformNode: BaseTransformNode, recognizer: DoubleFinglerMoveGestureRecognizer) : BaseTransformationController<DoubleFingerMoveGesture>(transformNode, recognizer) {
    override fun canStartTransformation(gesture: DoubleFingerMoveGesture): Boolean {
        return true
    }

    var lastDistance = PointF()
    override fun onContinueTransformation(gesture: DoubleFingerMoveGesture) {
//        Log.i("DragController", "onContinueTransformation ${gesture.gapDelta} ${gesture.gap} ")
        if (transformNode.view is ChinaMapView) {
            val startCenterPoint = gesture.startPosition1.center(gesture.startPosition2)
            val previousCenterPoint = gesture.previousPosition1.center(gesture.previousPosition2)
            val startToPrevious = previousCenterPoint.sub(startCenterPoint)
            val delta = startToPrevious.sub(lastDistance)
            lastDistance.set(startToPrevious)
            transformNode.view.translate(delta.x, delta.y)
        }
    }

    override fun onEndTransformation(gesture: DoubleFingerMoveGesture) {
        lastDistance = PointF()
    }
}