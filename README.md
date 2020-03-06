# 华夏万象 Android 版 widget 库

这个库在「华夏万象」Android 版开发过程中产出，仅用做查阅参考、不适合工业使用。

## 目录
1. MarqueeDrawable
跑马灯进度条 drawable

2. StarrySky
星空 drawable

3. ChinaMapView
中国地图带手势版本

4. MenuItemView
带底部菜单的View

5. ParallaxRelativeLayout
纵向的视差滚动布局

6. SegmentProgressBar
分段、可拖动的进度条

7. VerticalTextView
纵向 TextView

8. ShapedImageView
可控制宽高比的 ImageView

9. com.antiless.huaxia.widget.gesture
一个易于扩展的手势检测框架

## 用法

### 1. MarqueeDrawable
```kotlin
/**
 * 跑马灯进度条 drawable
 * @param width 期望宽度 Pixel
 * @param height 期望高度 Pixel
 * @param perWidth 跑马灯每段宽度 Pixel
 */
class MarqueeDrawable(val width: Int, val height: Int, val perWidth: Int) : Drawable(), Animatable

val marqueeDrawable = MarqueeDrawable(width, height, perWidth)
ImageView.setImageDrawable(marqueeDrawable)
marqueeDrawable.progress = 50
```

### 2. StarrySky

```kotlin
/**
* 星空 drawable
* @param widthPixels 宽度
* @param heightPixels 高度
*/
class StarrySky(val widthPixels: Int, val heightPixels: Int) : Drawable(), Animatable


val starrySky = StarrySky(widthPixels, heightPixels).apply {
    for (i in 0 until 50) {
        // 添加 50 个随机位置的星星
        addRandomStar()
    }
    // 监听星星跑出范围
    setOnStarOutListener {
        removeStar(it)
        addRandomStar()
    }
}
ImageView.setImageDrawable(starrySky)
// 开始运动
starrySky.start()
// 停止运动
starrySky.stop()
```

3. ChinaMapView
中国地图带手势版本

这个控件基于[ChinaMapView](https://github.com/xin10000/ChinaMapView)
加入了双指操作手势，双指基于第 9 项的手势检测框架
```xml
<com.antiless.huaxia.widget.chinamap.ChinaMapView
    android:id="@+id/itemMap"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```

4. MenuItemView
带底部菜单的View

5. ParallaxRelativeLayout
纵向的视差滚动布局

6. SegmentProgressBar
分段、可拖动的进度条

7. VerticalTextView
纵向 TextView

8. ShapedImageView
可控制宽高比的 ImageView

9. com.antiless.huaxia.widget.gesture
一个易于扩展的手势检测框架