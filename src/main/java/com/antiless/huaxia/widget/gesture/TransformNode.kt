package com.antiless.huaxia.widget.gesture

import android.view.View
import com.antiless.huaxia.widget.chinamap.controllers.DragController
import com.antiless.huaxia.widget.chinamap.controllers.RotateController
import com.antiless.huaxia.widget.chinamap.controllers.ScaleController
import com.antiless.huaxia.widget.chinamap.controllers.VisualController

class TransformNode(view: View, transformSystem: TransformSystem) : BaseTransformNode(view, transformSystem) {
    private val scaleController: ScaleController = ScaleController(this, transformSystem.pinchGestureRecognizer)
    private val dragController: DragController = DragController(this, transformSystem.doubleFingerMoveGestureRecognizer)
    private val rotateController: RotateController = RotateController(this, transformSystem.twistGestureRecognizer)
    private val visualController: VisualController = VisualController(this, transformSystem.swipeGestureRecognizer)

    init {
        addTransformationController(dragController)
        addTransformationController(scaleController)
        addTransformationController(rotateController)
        addTransformationController(visualController)
    }
}