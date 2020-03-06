package com.antiless.huaxia.widget.gesture

import android.graphics.PointF
import kotlin.math.acos
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.sqrt

fun PointF.normalized(): PointF {
    val distanceToZero = distance(PointF(0f, 0f))
    return PointF(x / distanceToZero, y / distanceToZero)
}

fun PointF.distance(point: PointF): Float {
    return sqrt((x - point.x).pow(2) + (y - point.y).pow(2))
}

fun PointF.sub(point: PointF): PointF {
    return PointF(x - point.x, y - point.y)
}

fun PointF.dot(point: PointF): Float {
    return x * point.x + y * point.y
}

fun PointF.degreeTo(point: PointF): Float {
    val cosValue = (x * point.x + y * point.y) / sqrt((x.pow(2) + y.pow(2)) * (point.x.pow(2) + point.y.pow(2)))
    val degree = Math.toDegrees(acos(cosValue).toDouble()).toFloat()
    val sign = sign(x * point.y - y * point.x)
    return degree * sign
}

fun PointF.center(point: PointF): PointF {
    return PointF((x + point.x) / 2, (y + point.y) / 2)
}

val ZERO get() = PointF()