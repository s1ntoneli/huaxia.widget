package com.antiless.huaxia.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * Round ImageView
 * support equal-ratio scale
 */
public class ShapedImageView extends AppCompatImageView {
    private int widthWeight;
    private int heightWeight;
    private int base;
    private int radius;
    private int strokeColor;
    private int strokeWidth;

    public final static int HEIGHT_BASED = 0;

    public final static int WIDTH_BASED = 1;
    private final Path mRoundPath = new Path();
    private final Paint mPaint = new Paint();
    private final RectF mRect = new RectF();
    private boolean mIsDirty = true;

    public ShapedImageView(Context context) {
        this(context, null);
    }

    public ShapedImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShapedImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        final TypedArray attr = getContext().obtainStyledAttributes(attrs, R.styleable.ShapedImageView);
        widthWeight = attr.getInteger(R.styleable.ShapedImageView_widthWeight, 0);
        heightWeight = attr.getInteger(R.styleable.ShapedImageView_heightWeight, 0);
        base = attr.getInteger(R.styleable.ShapedImageView_base, 0);
        radius = attr.getDimensionPixelSize(R.styleable.ShapedImageView_radius, 0);
        strokeWidth = attr.getDimensionPixelSize(R.styleable.ShapedImageView_strokeWidth, 0);
        strokeColor = attr.getColor(R.styleable.ShapedImageView_strokeColor, 0x00000000);
        attr.recycle();

        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setColor(strokeColor);
    }

    public void setCornerRadius(int px) {
        radius = px;
        mIsDirty = true;
    }

    public void setWidthWeight(int widthWeight) {
        this.widthWeight = widthWeight;
        mIsDirty = true;
    }

    public void setHeightWeight(int heightWeight) {
        this.heightWeight = heightWeight;
        mIsDirty = true;
    }

    public void setBase(int base) {
        this.base = base;
        mIsDirty = true;
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        mPaint.setColor(strokeColor);
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        mPaint.setStrokeWidth(strokeWidth);
        mIsDirty = true;
    }

    @Override
    public void draw(Canvas canvas) {
        if (!ensureValidRect()) {
            return;
        }
        canvas.save();
        canvas.clipPath(mRoundPath);
        super.draw(canvas);
        canvas.restore();
        canvas.drawRoundRect(mRect, radius, radius, mPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mIsDirty = true;
    }

    private boolean ensureValidRect() {
        if (mIsDirty) {
            mIsDirty = false;

            Rect bounds = new Rect();
            float inset = mPaint.getStrokeWidth() * 0.5f;
            getDrawingRect(bounds);
            mRect.set(bounds.left + inset, bounds.top + inset,
                    bounds.right - inset, bounds.bottom - inset);
            mRoundPath.addRoundRect(mRect, radius, radius, Path.Direction.CCW);
        }
        return !mRect.isEmpty();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (widthWeight != 0 && heightWeight != 0) {
            if (base == HEIGHT_BASED) {
                int height = MeasureSpec.getSize(heightMeasureSpec);
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(height * widthWeight / heightWeight, MeasureSpec.EXACTLY);
            } else {
                int width = MeasureSpec.getSize(widthMeasureSpec);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(width * heightWeight / widthWeight, MeasureSpec.EXACTLY);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}