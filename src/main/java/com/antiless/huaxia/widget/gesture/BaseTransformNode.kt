package com.antiless.huaxia.widget.gesture

import android.view.View
import com.antiless.huaxia.widget.gesture.BaseTransformationController

/**
 * val transformSystem = TransformSystem()
 * val node = BaseTransformNode(transformSystem)
 */
abstract class BaseTransformNode(val view: View, val transformSystem: TransformSystem) {
    private val controllers = ArrayList<BaseTransformationController<*>>()


    fun addTransformationController(
            transformationController: BaseTransformationController<*>) {
        controllers.add(transformationController)
    }

    fun removeTransformationController(
            transformationController: BaseTransformationController<*>) {
        controllers.remove(transformationController)
    }

    /** Returns true if any of the transformation controllers are actively transforming this node. */
    fun isTransforming(): Boolean {
        controllers.forEach {
            if (it.isTransforming()) return true
        }
        return false
    }
}