package com.antiless.huaxia.widget.drawable

import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.text.TextPaint
import com.antiless.huaxia.widget.debuglog
import kotlin.math.abs
import kotlin.math.tan

class MarqueeDrawable(val width: Int, val height: Int, val perWidth: Int) : Drawable(), Animatable {
    var progress: Int = 0
        set(value) {
            if (value != field) {
                field = value
                updateSlicePaths()
                invalidateSelf()
            }
        }
    var parallaxValue = 0.7f
    var degree = 80

    val marqueePaint = Paint().apply {
        color = Color.parseColor("#A80007")
        isAntiAlias = true
    }
    val backgroundPaint = Paint().apply {
        shader = LinearGradient(0f, 0f, width.toFloat(), 20f, Color.parseColor("#7E3C41"), Color.parseColor("#FFC25258"),
                Shader.TileMode.CLAMP)
        isAntiAlias = true
    }
    val foregroundPaint = Paint().apply {
        shader = LinearGradient(0f, 0f, width.toFloat(), 20f, Color.parseColor("#12000000"), Color.parseColor("#00000000"),
                Shader.TileMode.CLAMP)
        isAntiAlias = true
    }
    val textPaint = TextPaint().apply {
        color = Color.WHITE
        textSize = 48f
        typeface = Typeface.DEFAULT_BOLD
        isAntiAlias = true
    }

    companion object {
        const val MAX_PROGRESS = 100
    }

    override fun isRunning(): Boolean {
        return false
    }

    override fun start() {
    }

    override fun stop() {
    }

    private val slicePaths = ArrayList<Path>()
    fun updateSlicePaths() {
        slicePaths.clear()
        val fullOffset = width * (parallaxValue - 1)
        val actureWidth = width + abs(fullOffset)
        var start = 0f
        while (start < actureWidth + perWidth) {
            val path = Path().apply {
                moveTo(start, 0f)
                lineTo(start + perWidth, 0f)
                lineTo((start + perWidth - height / tan(Math.toRadians(degree.toDouble()))).toFloat(), height * 1f)
                lineTo((start - height / tan(Math.toRadians(degree.toDouble()))).toFloat(), height * 1f)
                close()
            }
            start += perWidth * 2
            slicePaths.add(path)
        }
    }

    private val roundStrokePaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 1f
        isAntiAlias = true
    }

    override fun draw(canvas: Canvas) {
        if (slicePaths.isEmpty()) updateSlicePaths()
        val fullOffset = width * (parallaxValue - 1)
        val actureWidth = width + abs(fullOffset)
        val progressRatio = progress.toFloat() / MAX_PROGRESS
        val currentOffset = fullOffset * progressRatio
        val currentWidth = width.toFloat() * progress / MAX_PROGRESS
        debuglog("width $currentOffset $currentWidth")
        val clipPath = Path().apply {
            addRoundRect(0f, 0f, currentWidth, height.toFloat(), height.toFloat(), height.toFloat(), Path.Direction.CW)
        }
        canvas.save()
        canvas.clipPath(clipPath)
        canvas.save()
        canvas.translate(currentOffset + -width.toFloat() * (1 - progress) / MAX_PROGRESS - width, 0f)
        canvas.drawRect(0f, 0f, actureWidth * 2, height.toFloat(), backgroundPaint)
        slicePaths.forEach { canvas.drawPath(it, marqueePaint) }
        val text = "$progress%"
        canvas.restore()
        foregroundPaint.shader = LinearGradient(0f, 0f, currentWidth, 20f, Color.parseColor("#12000000"), Color.parseColor("#00000000"),
                Shader.TileMode.CLAMP)
        canvas.drawPaint(foregroundPaint)
        val textHeight = textPaint.fontMetricsInt.bottom - textPaint.fontMetricsInt.top
        val top = (height - textHeight) / 2 + abs(textPaint.fontMetricsInt.top)
        debuglog("textFontMatrics $height $top" + textPaint.fontMetricsInt)
        canvas.drawPath(clipPath, roundStrokePaint)
        canvas.drawText(text, currentWidth - textPaint.measureText(text) - 24, top.toFloat(), textPaint)
        canvas.restore()
    }

    override fun setAlpha(alpha: Int) {}

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {}
    override fun getIntrinsicWidth(): Int {
        return width
    }

    override fun getIntrinsicHeight(): Int {
        return height
    }
}