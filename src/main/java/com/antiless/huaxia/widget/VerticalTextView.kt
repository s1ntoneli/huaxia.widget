package com.antiless.huaxia.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import java.lang.StringBuilder

class VerticalTextView : View {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {
        val a = context.obtainStyledAttributes(attributeSet, R.styleable.VerticalTextView)
        text = a.getString(R.styleable.VerticalTextView_text) ?: ""
        textSize = a.getDimensionPixelSize(R.styleable.VerticalTextView_textSize, 0).toFloat()
        textColor = a.getColor(R.styleable.VerticalTextView_textColor, Color.BLACK)
        measuredHeight = a.getDimensionPixelSize(R.styleable.VerticalTextView_height, 0).toFloat()
        letterSpace = a.getFloat(R.styleable.VerticalTextView_letterSpace, 0f)
        style = a.getInteger(R.styleable.VerticalTextView_textStyle, 0)
        a.recycle()

        remeasure()
    }

    private val paint = TextPaint().apply {
        isAntiAlias = true
    }
    private var textLength: Float = 0f
    var measuredWidth: Float = 0f
    private var textCountPerColumn: Int = 0
    private var columnCount: Int = 0
    private var rectHeight: Float = 0f
    private var rectWidth: Float = 0f

    var text: String = ""
        set(value) {
            val v = value.trim()
            if (field != v) {
                field = v
                remeasure()
            }
        }
    var measuredHeight: Float = 0f
        set(value) {
            if (field != value) {
                field = value
                remeasure()
            }
        }
    var letterSpace: Float = 0f
        set(value) {
            if (field != value) {
                field = value
                remeasure()
            }
        }
    var lineSpacing: Float = 0f
        set(value) {
            if (field != value) {
                field = value
                remeasure()
            }
        }
    var font: Typeface? = null
        set(value) {
            if (field != value) {
                field = value
                paint.typeface = font
                remeasure()
            }
        }
    var textSize: Float = 0f
        set(value) {
            if (field != value) {
                field = value
                remeasure()
            }
        }
    var textColor: Int = Color.WHITE
        set(value) {
            if (field != value) {
                field = value
                remeasure()
            }
        }
    var style: Int = 0
        set(value) {
            if (field != value) {
                field = value
                remeasure()
            }
        }

    fun remeasure() {
        paint.color = textColor
        if (textSize != 0f) paint.textSize = textSize
        textLength = paint.fontSpacing
        rectHeight = textLength + textLength * letterSpace
        rectWidth = textLength + textLength * lineSpacing

        textCountPerColumn = (measuredHeight / rectHeight).toInt()
        columnCount = calculateColumnCount(formatTextForPunctuation(text, textCountPerColumn), textCountPerColumn)
        // ceil(text.length * 1.0 / textCountPerColumn).toInt()
        measuredWidth = columnCount * rectWidth
    }

    fun formatTextForPunctuation(text: String, countPerColumn: Int): String {
        var currentLineCount = -1
        val sb = StringBuilder()
        text.forEachIndexed { index, c ->
            if (c == '\n') {
                currentLineCount = -1
            } else {
                currentLineCount += 1
                // 如果是当前行最后一个，判断下一个字符是否是标点
                if (currentLineCount == countPerColumn - 1 && index < text.length - 1) {
                    val next = text[index + 1]
                    if (next.isPunct()) {
                        sb.append('\n')
                        currentLineCount = -1
                    }
                } else if (currentLineCount == countPerColumn) {
                    currentLineCount = 0
                }
            }
            sb.append(c)
        }
        return sb.toString()
    }

    private fun calculateColumnCount(text: String, countPerColumn: Int): Int {
        var totalColumns = 1
        var currentLineCount = 0
        text.forEach {
            if (it == '\n') {
                currentLineCount = -1
                totalColumns++
            } else {
                currentLineCount += 1
                if (currentLineCount == countPerColumn) {
                    currentLineCount = 0
                    totalColumns++
                }
            }
        }
        return totalColumns
    }

    override fun onDraw(canvas: Canvas) {
        var currentColumnIndex = 0
        var currentLineCount = -1
        val text = formatTextForPunctuation(text, textCountPerColumn)
        for (i in text.indices) {
            val t = text[i]
            if (t == '\n') {
                currentLineCount = -1
                currentColumnIndex++
                continue
            } else {
                currentLineCount += 1
                if (currentLineCount == textCountPerColumn) {
                    currentLineCount = 0
                    currentColumnIndex++
                }
            }
            val column = columnCount - currentColumnIndex - 1
            val row = currentLineCount
            canvas.drawText(text, i, i + 1, column * rectWidth, row * rectHeight + paint.fontMetricsInt.leading - paint.fontMetricsInt.ascent, paint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val mode = MeasureSpec.getMode(widthMeasureSpec)
        val wms = MeasureSpec.makeMeasureSpec(measuredWidth.toInt(), MeasureSpec.EXACTLY)
        val hms = MeasureSpec.makeMeasureSpec(measuredHeight.toInt(), MeasureSpec.EXACTLY)

        super.onMeasure(wms, hms)
    }
}