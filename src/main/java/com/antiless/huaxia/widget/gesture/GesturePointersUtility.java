package com.antiless.huaxia.widget.gesture;

import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;

import java.util.HashSet;

/**
 * Retains/Releases pointer Ids so that each pointer can only be used in one gesture at a time.
 * Provides helper functions for converting touch coordinates between pixels and inches.
 */
public class GesturePointersUtility {
    private final DisplayMetrics displayMetrics;
    private final HashSet<Integer> retainedPointerIds;

    public GesturePointersUtility(DisplayMetrics displayMetrics) {
        this.displayMetrics = displayMetrics;
        retainedPointerIds = new HashSet<>();
    }

    public void retainPointerId(int pointerId) {
        if (!isPointerIdRetained(pointerId)) {
            retainedPointerIds.add(pointerId);
        }
    }

    public void releasePointerId(int pointerId) {
        retainedPointerIds.remove(Integer.valueOf(pointerId));
    }

    public boolean isPointerIdRetained(int pointerId) {
        return retainedPointerIds.contains(pointerId);
    }

    public float inchesToPixels(float inches) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN, inches, displayMetrics);
    }

    public float pixelsToInches(float pixels) {
        float inchOfPixels =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN, 1.0f, displayMetrics);
        return pixels / inchOfPixels;
    }

    public static PointF motionEventToPosition(MotionEvent me, int pointerId) {
        int index = me.findPointerIndex(pointerId);
        return new PointF(me.getX(index), me.getY(index));
    }

    public static Double distance(PointF pos1, PointF pos2) {
        return Math.sqrt((pos1.x - pos2.x) * (pos1.x - pos2.x) + (pos1.y - pos2.y) * (pos1.y - pos2.y));
    }
}