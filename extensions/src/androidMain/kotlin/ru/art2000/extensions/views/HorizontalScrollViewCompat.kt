package ru.art2000.extensions.views

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.widget.HorizontalScrollView
import androidx.core.view.ViewCompat

/**
 * Adopted from https://stackoverflow.com/a/20033587
 */
class HorizontalScrollViewCompat @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.horizontalScrollViewStyle,
) : HorizontalScrollView(context, attrs, defStyleAttr) {

    private var mGravityRight = false

    @SuppressLint("RtlHardcoded")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.onLayout(changed, left, top, right, bottom)
        } else {
            val childView = getChildAt(0)
            super.onLayout(changed, left, top, right, bottom)
            val p = childView.layoutParams as LayoutParams
            val horizontalGravity = p.gravity and Gravity.HORIZONTAL_GRAVITY_MASK
            val verticalGravity = p.gravity and Gravity.VERTICAL_GRAVITY_MASK
            if (isRightGravity(horizontalGravity)) {
                if (getScrollRange() > 0) {
                    mGravityRight = true
                    p.gravity =
                        (if (horizontalGravity == Gravity.RIGHT) Gravity.LEFT else Gravity.END) or verticalGravity
                    childView.layoutParams = p
                    val layoutLeft = 0
                    val layoutRight = 0
                    super.onLayout(changed, layoutLeft, top, layoutRight, bottom)
                }
            } else if (mGravityRight) {
                if (getScrollRange() == 0) {
                    mGravityRight = false
                    p.gravity =
                        (if (horizontalGravity == Gravity.LEFT) Gravity.RIGHT else Gravity.END) or verticalGravity
                    childView.layoutParams = p
                    super.onLayout(changed, left, top, right, bottom)
                }
            }
        }
    }

    private fun isRightGravity(gravity: Int): Boolean {
        return gravity == Gravity.RIGHT || (gravity == Gravity.END
                && ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_LTR)
    }

    private fun getScrollRange(): Int {
        var scrollRange = 0
        if (childCount > 0) {
            val child = getChildAt(0)
            scrollRange = 0.coerceAtLeast(child.width - (width - paddingLeft - paddingRight))
        }
        return scrollRange
    }
}