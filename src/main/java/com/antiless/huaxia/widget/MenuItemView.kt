package com.antiless.huaxia.widget

import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import android.widget.OverScroller
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import kotlin.math.abs

class MenuItemView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        FrameLayout(context, attrs, defStyleAttr) {
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    companion object {
        const val FAST_SLOP = 1000
    }

    private lateinit var coverView: View
    private lateinit var backView: View
    private var coverViewId: Int
    private var backViewId: Int

    var dragEnable: Boolean = true

    private val scroller = OverScroller(context)

    object DragState {
        const val IDLE = 0
        const val DRAGGING = 1
        const val SETTLING = 2
    }

    private var onDragStateChangedListener: ((Int) -> Unit)? = null
    fun setOnDragStateChangedListener(listener: ((Int) -> Unit)?) {
        onDragStateChangedListener = listener
    }

    private var dragState: Int = DragState.IDLE

    private fun setDragStateInternal(newDragState: Int) {
        if (dragState != newDragState) {
            dragState = newDragState
            onDragStateChangedListener?.let {
                it(dragState)
            }
        }
    }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.MenuItemView)
        coverViewId = a.getResourceId(R.styleable.MenuItemView_coverViewId, 0)
        backViewId = a.getResourceId(R.styleable.MenuItemView_backViewId, 0)
        dragEnable = a.getBoolean(R.styleable.MenuItemView_dragEnable, true)
        a.recycle()
    }

    private lateinit var viewDragHelper: ViewDragHelper

    init {
        viewDragHelper = ViewDragHelper.create(this, 1f, object : ViewDragHelper.Callback() {
            override fun tryCaptureView(child: View, pointerId: Int): Boolean {
                return coverViewId != 0 && child.id == coverViewId
            }

            override fun getViewHorizontalDragRange(child: View): Int {
                return 1
            }

            override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
                setDragStateInternal(DragState.DRAGGING)
                return left.coerceAtLeast(-backView.width).coerceAtMost(0)
            }

            override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
                debuglog("xvel $xvel")
                val maxWidth = backView.width
                val finalLeft =
                        if (isFast(xvel) && xvel < 0 || (!isFast(xvel) && releasedChild.left < -maxWidth / 2)) -maxWidth else 0
                if (viewDragHelper.settleCapturedViewAt(finalLeft, 0)) {
                    setDragStateInternal(DragState.SETTLING)
                    invalidate()
                } else {
                    setDragStateInternal(DragState.IDLE)
                }
            }
        })
    }

    private fun isFast(v: Float): Boolean {
        return abs(v) > FAST_SLOP
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (coverViewId != 0) {
            coverView = findViewById(coverViewId)
        }
        if (backViewId != 0) {
            backView = findViewById(backViewId)
            backView.layoutParams = (backView.layoutParams as LayoutParams).apply {
                gravity = Gravity.END
            }
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return if (dragEnable) {
            val intercept = viewDragHelper.shouldInterceptTouchEvent(ev)
            debuglog("intercept $intercept")
            intercept
        } else {
            super.onInterceptTouchEvent(ev)
        }
    }

    private val initialPoint = PointF()
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (dragEnable) {
            viewDragHelper.processTouchEvent(event)
            val vc: ViewConfiguration? = ViewConfiguration.get(context)
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> initialPoint.set(event.x, event.y)
                MotionEvent.ACTION_MOVE -> {
                    if (abs(event.x - initialPoint.x) > vc!!.scaledTouchSlop) {
                        parent.requestDisallowInterceptTouchEvent(true)
                    }
                }
            }
            true
        } else {
            super.onTouchEvent(event)
        }
    }

    override fun computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            invalidate()
        } else {
            setDragStateInternal(DragState.IDLE)
        }
        if (scroller.computeScrollOffset()) {
            debuglog("currX left ${scroller.currX} - ${coverView.left}")
            ViewCompat.offsetLeftAndRight(coverView, scroller.currX - coverView.left)
            invalidate()
        } else if (autoSettling) {
            autoSettling = false
        }
    }

    private var isBackShowed = false
    private var autoSettling = false
    fun showBackView() {
        autoSettling = true
        scroller.startScroll(0, 0, -backView.width, 0)
        invalidate()
    }
    fun resetBackView() {
        autoSettling = true
        scroller.startScroll(-backView.width, 0, backView.width, 0)
        invalidate()
    }
    fun isBackShowed(): Boolean {
        return coverView.left == -backView.width && dragState == DragState.IDLE && !autoSettling
    }
}