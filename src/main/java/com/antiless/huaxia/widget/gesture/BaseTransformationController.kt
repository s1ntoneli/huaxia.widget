package com.antiless.huaxia.widget.gesture

import androidx.annotation.Nullable

abstract class BaseTransformationController<T : BaseGesture<T>>(
        val transformNode: BaseTransformNode, val gestureRecognizer: BaseGestureRecognizer<T>) :
        BaseGestureRecognizer.OnGestureStartedListener<T>,
        BaseGesture.OnGestureEventListener<T> {

    init {
        setEnabled(true)
    }

    private var activeGesture: T? = null
    private var enabled: Boolean = false
    private var activeAndEnabled: Boolean = false

    fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
        updateActiveAndEnabled()
    }

    fun isEnabled(): Boolean {
        return enabled
    }

    fun isTransforming(): Boolean {
        return activeGesture != null
    }

    override fun onGestureStarted(gesture: T) {
        if (isTransforming()) return

        if (canStartTransformation(gesture)) {
            setActiveGesture(gesture)
        }
    }

    override fun onUpdated(gesture: T) {
        onContinueTransformation(gesture)
    }

    override fun onFinished(gesture: T) {
        onEndTransformation(gesture)
        setActiveGesture(null)
    }

    protected abstract fun canStartTransformation(gesture: T): Boolean

    protected abstract fun onContinueTransformation(gesture: T)

    protected abstract fun onEndTransformation(gesture: T)

    private fun setActiveGesture(@Nullable gesture: T?) {
        activeGesture?.eventListener = null
        activeGesture = gesture
        activeGesture?.eventListener = this
    }

    private fun connectToRecognizer() {
        gestureRecognizer.addOnGestureStartedListener(this)
    }

    private fun disconnectFromRecognizer() {
        gestureRecognizer.removeOnGestureStartedListener(this)
    }

    private fun updateActiveAndEnabled() {
        val newActiveEnabled = enabled
        if (newActiveEnabled == activeAndEnabled) return

        activeAndEnabled = newActiveEnabled
        if (activeAndEnabled) {
            connectToRecognizer()
        } else {
            disconnectFromRecognizer()
            activeGesture?.cancel()
        }
    }
}