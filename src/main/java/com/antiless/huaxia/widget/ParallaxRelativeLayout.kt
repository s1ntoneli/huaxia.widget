package com.antiless.huaxia.widget

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.RelativeLayout
import android.widget.ScrollView

class ParallaxRelativeLayout(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        RelativeLayout(context, attrs, defStyleAttr), ViewTreeObserver.OnScrollChangedListener {
    constructor(context: Context, attrs: AttributeSet): this(context, attrs, 0)
    constructor(context: Context): this(context, null, 0)

    companion object {
        class LayoutParams : RelativeLayout.LayoutParams {
            var parallaxSpeed: Float = 1f
            constructor(context: Context, attrs: AttributeSet?): super(context, attrs) {
                val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.ParallaxRelativeLayout)
                parallaxSpeed = a.getFloat(R.styleable.ParallaxRelativeLayout_layout_parallax_speed, 1f)
                a.recycle()

            }
            constructor(width: Int, height: Int): super(width, height)
            constructor(layoutParams: MarginLayoutParams): super(layoutParams)
            constructor(layoutParams: LayoutParams): super(layoutParams)
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): RelativeLayout.LayoutParams {
        debuglog("generateLayoutParams")
        return LayoutParams(context, attrs)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (parent != null && parent is ScrollView) {
            (parent as ViewGroup).viewTreeObserver.addOnScrollChangedListener(this)
        }
    }
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (parent != null && parent is ScrollView) {
            (parent as ScrollView).viewTreeObserver.removeOnScrollChangedListener(this)
        }
    }

    class ParallaxChild(val view: View, val speed: Float) {
        override fun equals(other: Any?): Boolean {
            return other is ParallaxChild && other.view == view
        }

        override fun hashCode(): Int {
            return view.hashCode()
        }
    }

    /**
     *    记住哪些 view 有 parallax 参数
     */
    private val parallaxChildren = ArrayList<ParallaxChild>()

    override fun addView(child: View, params: ViewGroup.LayoutParams?) {
        super.addView(child, params)
        if (params is LayoutParams) {
            parallaxChildren.add(ParallaxChild(child, params.parallaxSpeed))
        }
        debuglog("addView ${parallaxChildren.size}")
    }

    override fun removeView(view: View) {
        super.removeView(view)
        parallaxChildren.remove(ParallaxChild(view, 1f))
        debuglog("removeView ${parallaxChildren.size}")
    }

    /**
     * parent 的 onScrollChanged 事件
     * 父亲滚动改变时，根据每个元素的滚动速度进行调整 view.translationY
     */
    private var parentLastScrollY = 0
    override fun onScrollChanged() {
        val currentScrollY = (parent as ViewGroup).scrollY
        val delta = currentScrollY - parentLastScrollY
        for (child in parallaxChildren) {
            val translationDelta = -delta * (child.speed - 1f)
            child.view.translationY += translationDelta.toInt()
        }
        parentLastScrollY = currentScrollY
    }
}