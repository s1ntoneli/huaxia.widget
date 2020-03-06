package com.antiless.huaxia.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.sqrt

class SegmentProgressBar : View {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    private val defaultBarHeight = dp(14)
    private val defaultStrokeColor = Color.WHITE
    private val defaultBackgroundColor = Color.TRANSPARENT
    private val defaultTranversalColor = Color.WHITE
    private val defaultSegmentColor = Color.RED
    private val defaultBubbleColor = Color.WHITE
    private val defaultBubbleTextColor = Color.BLACK
    private val defaultBubbleHeight = dp(35)

    private val strokePaint = Paint().apply {
        color = defaultStrokeColor
        strokeWidth = dp(3).toFloat()
        style = Paint.Style.STROKE
        isAntiAlias = true
    }
    private val backgroundPaint = Paint().apply {
        color = defaultBackgroundColor
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val tranversalPaint = Paint().apply {
        color = defaultTranversalColor
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val segmentPaint = Paint().apply {
        color = defaultSegmentColor
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val bubblePaint = Paint().apply {
        color = defaultBubbleColor
        style = Paint.Style.FILL
        isAntiAlias = true
        pathEffect = CornerPathEffect(10f)
    }
    private val bubbleTextPaint = Paint().apply {
        color = defaultBubbleTextColor
        textSize = dp(13).toFloat()
        isAntiAlias = true
    }

    /**
     * 结束进度
     */
    var maxSegment: Int = 0
        set(value) {
            if (field != value) {
                field = value
                if (currentSegment > value) {
                    currentSegment = value
                }
                invalidate()
            }
        }
    /**
     * 起始进度
     */
    var minSegment: Int = 0
        set(value) {
            if (field != value) {
                field = value
                if (currentSegment > value) {
                    currentSegment = value
                }
                invalidate()
            }
        }
    /**
     * 当前进度
     */
    var currentSegment: Int = 0
        set(value) {
            if (field != value) {
                field = when {
                    value > maxSegment -> maxSegment
                    value < minSegment -> minSegment
                    else -> value
                }
                invalidate()
            }
        }

    /**
     * 特殊标记的点
     */
    var markedSegments = arrayListOf<Int>()
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    /**
     * bubble scale
     */
    var bubbleScale = 0f
        set(value) {
            if (field != value) {
                field = value
                updateBubblePath()
                invalidate()
            }
        }

    var showBubbleAnimator: ValueAnimator? = null
    var bubbleAnimating = false
    /**
     * 显示气泡
     * @param anim 是否显示中间动画
     */
    fun showBubble(anim: Boolean = true) {
        if (!anim) bubbleScale = 1f
        else {
            showBubbleAnimator = ValueAnimator.ofFloat(bubbleScale, 1.2f, 1f)
            showBubbleAnimator?.addUpdateListener {
                bubbleScale = it.animatedValue as Float
                bubbleAnimating = it.animatedFraction != 1f
                if (it.animatedFraction == 1f) {
                    onBubbleShowEnd()
                }
            }
            showBubbleAnimator?.start()
            onBubbleShowStart()
        }
    }

    var onBubbleHideStart = {}
    var onBubbleHideEnd = {}
    var onBubbleShowStart = {}
    var onBubbleShowEnd = {}
    /**
     * 隐藏气泡
     * @param anim 是否显示中间动画
     */
    fun hideBubble(anim: Boolean = true) {
        if (!anim) bubbleScale = 0f
        else {
            showBubbleAnimator?.cancel()
            val animator = ValueAnimator.ofFloat(bubbleScale, 0f)
            animator.addUpdateListener {
                bubbleScale = it.animatedValue as Float
                bubbleAnimating = it.animatedFraction != 1f
                if (it.animatedFraction == 1f) {
                    onBubbleHideEnd()
                }
            }
            animator.start()
            onBubbleHideStart()
        }
    }

    private fun updateBubblePath() {
        val radius = bubbleRadius

        val triangle1X = -sqrt(3.0).toFloat() / 2f * radius
        val triangle1Y = radius / 2

        val triangle2X = sqrt(3.0).toFloat() / 2 * radius
        val triangle2Y = radius / 2

        val triangle3X = 0f
        val triangle3Y = radius / 2 + radius * 3f / 2

        bubblePath.reset()
        bubblePath.moveTo(triangle1X, triangle1Y)
        bubblePath.lineTo(triangle2X, triangle2Y)
        bubblePath.lineTo(triangle3X, triangle3Y)
        bubblePath.close()
    }

    fun addSegment(vararg positions: Int) {
        positions.forEach { markedSegments.add(it) }
        invalidate()
    }

    fun removeSegment(vararg positions: Int) {
        positions.forEach { markedSegments.remove(it) }
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val hms = MeasureSpec.makeMeasureSpec(defaultBarHeight + defaultBubbleHeight + dp(10), MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, hms)
    }

    fun dp(dp: Int): Int {
        return (context.resources.displayMetrics.density * dp + 0.5f).toInt()
    }

    override fun onDraw(canvas: Canvas?) {
        debuglog("height $height width $width $left $top $right $bottom")
        canvas?.let {
            drawBar(canvas)
            drawBubble(canvas)
        }
    }

    private val bubbleRadius = dp(20) / 2f
    private val bubblePath = Path()

    private fun drawBubble(canvas: Canvas) {
        if (currentSegment < minSegment || currentSegment > maxSegment || maxSegment < minSegment) return
        val width = width - paddingStart1 - paddingEnd1
        val perWidth = width.toFloat() / (maxSegment - minSegment + 1)
        val centerX = perWidth * (currentSegment - minSegment) + perWidth / 2 + paddingStart1.toFloat()
        val centerY = bubbleRadius
        canvas.save()
        canvas.translate(centerX, centerY)
        canvas.scale(bubbleScale, bubbleScale)

        canvas.drawCircle(0f, 0f, bubbleRadius, bubblePaint)
        canvas.drawPath(bubblePath, bubblePaint)
        val textHeight = bubbleTextPaint.fontMetricsInt.leading - bubbleTextPaint.fontMetricsInt.ascent
        val text = "$currentSegment"
        canvas.drawText(text, -bubbleTextPaint.measureText(text) / 2, textHeight.toFloat() / 2, bubbleTextPaint)

        canvas.restore()
    }

    private var paddingStart1 = dp(20)
    private var paddingEnd1 = dp(20)
    private var dividerWidth = 1
    private var strokeWidth = dp(3)
    private fun drawBar(canvas: Canvas) {
        val barHeight = defaultBarHeight
        val bubbleHeight = defaultBubbleHeight
        val width = width - paddingStart1 - paddingEnd1
        val divider = dividerWidth
        val strokeWidth = strokeWidth.toFloat()
        canvas.save()
        canvas.translate(paddingStart1.toFloat(), bubbleHeight.toFloat())

        // 画全局的背景色
        canvas.drawRoundRect(0f, 0f, width.toFloat(), barHeight.toFloat(), barHeight / 2f, barHeight / 2f, backgroundPaint)

        // 画内圈
        canvas.save()
        canvas.translate(strokeWidth + divider, strokeWidth + divider)
        val innerWidth = width - (strokeWidth + divider) * 2f
        val innerHeight = barHeight - (strokeWidth + divider) * 2f
        val tranversaledWidth = if (maxSegment < minSegment) 0f else innerWidth / (maxSegment - minSegment + 1) * (currentSegment - minSegment + 1)
        canvas.drawRoundRect(0f, 0f, tranversaledWidth, innerHeight, innerHeight / 2f, innerHeight / 2f, tranversalPaint)
        for (i in markedSegments.indices) {
            val it = markedSegments[i]
            if (it < minSegment || it > maxSegment || maxSegment < minSegment) continue
            // 最小的点不画，单独说明
            val perWidth = innerWidth / (maxSegment - minSegment + 1)
            canvas.drawRect((it - minSegment) * perWidth, 0f, (it - minSegment + 1) * perWidth, innerHeight, segmentPaint)
        }
        canvas.restore()

        // 继续画外圈
        canvas.drawRoundRect(0f, 0f, width.toFloat(), barHeight.toFloat(), barHeight / 2f, barHeight / 2f, strokePaint)

        canvas.restore()
    }
}