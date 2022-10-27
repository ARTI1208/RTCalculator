package ru.art2000.extensions.activities

import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.annotation.RequiresApi
import androidx.core.graphics.Insets
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import ru.art2000.extensions.views.*

interface EdgeToEdgeScreen {

    val clearStatusBar: Boolean

    val clearNavigationBar: Boolean

    val insetAsPadding: Boolean
        get() = true

    val leftViews: List<View>
        get() = emptyList()

    val topViews: List<View>
        get() = emptyList()

    val rightViews: List<View>
        get() = emptyList()

    val bottomViews: List<View>
        get() = emptyList()

}

interface IEdgeToEdgeFragment : EdgeToEdgeScreen {

    override val clearStatusBar: Boolean
        get() = false

    override val clearNavigationBar: Boolean
        get() = false

    fun Fragment.applyEdgeToEdgeIfAvailable() {
        val top = topViews
        val bottom = bottomViews
        val left = leftViews
        val right = rightViews

        if (top.isEmpty() && bottom.isEmpty() && left.isEmpty() && right.isEmpty()) return

        if (insetAsPadding) {
            applyEdgeToEdgePaddingIfAvailable(
                clearStatusBar = clearStatusBar,
                clearNavigationBar = clearNavigationBar,
                topViews = top,
                bottomViews = bottom,
                leftViews = left,
                rightViews = right,
            )
        } else {
            applyEdgeToEdgeMarginIfAvailable(
                clearStatusBar = clearStatusBar,
                clearNavigationBar = clearNavigationBar,
                topViews = top,
                bottomViews = bottom,
                leftViews = left,
                rightViews = right,
            )
        }
    }
}

interface IEdgeToEdgeActivity : EdgeToEdgeScreen {

    override val clearStatusBar: Boolean
        get() = true

    override val clearNavigationBar: Boolean
        get() = true

    fun FragmentActivity.applyEdgeToEdgeIfAvailable() {
        val top = topViews
        val bottom = bottomViews
        val left = leftViews
        val right = rightViews

        if (top.isEmpty() && bottom.isEmpty() && left.isEmpty() && right.isEmpty()) return

        if (insetAsPadding) {
            applyEdgeToEdgePaddingIfAvailable(
                clearStatusBar = clearStatusBar,
                clearNavigationBar = clearNavigationBar,
                topViews = top,
                bottomViews = bottom,
                leftViews = left,
                rightViews = right,
            )
        } else {
            applyEdgeToEdgeMarginIfAvailable(
                clearStatusBar = clearStatusBar,
                clearNavigationBar = clearNavigationBar,
                topViews = top,
                bottomViews = bottom,
                leftViews = left,
                rightViews = right,
            )
        }
    }
}

fun Fragment.applyEdgeToEdgePaddingIfAvailable(
    clearStatusBar: Boolean = false,
    clearNavigationBar: Boolean = false,
    topViews: List<View>,
    bottomViews: List<View>,
    leftViews: List<View>,
    rightViews: List<View>,
) = requireActivity().applyEdgeToEdgePaddingIfAvailable(
    clearStatusBar, clearNavigationBar, topViews, bottomViews, leftViews, rightViews,
)

fun FragmentActivity.applyEdgeToEdgePaddingIfAvailable(
    clearStatusBar: Boolean = true,
    clearNavigationBar: Boolean = true,
    topViews: List<View>,
    bottomViews: List<View>,
    leftViews: List<View>,
    rightViews: List<View>,
) {

    applyEdgeToEdgeIfAvailable(
        clearStatusBar = clearStatusBar,
        clearNavigationBar = clearNavigationBar,
        leftViews = leftViews,
        topViews = topViews,
        rightViews = rightViews,
        bottomViews = bottomViews,
        left = { paddingLeft },
        top = { paddingTop },
        right = { paddingRight },
        bottom = { paddingBottom },
    ) { left, top, right, bottom ->

        when (this) {
            is ViewGroup -> clipToPadding = false
        }

        updatePadding(left, top, right, bottom)
    }
}

fun Fragment.applyEdgeToEdgeMarginIfAvailable(
    clearStatusBar: Boolean = false,
    clearNavigationBar: Boolean = false,
    topViews: List<View>,
    bottomViews: List<View>,
    leftViews: List<View>,
    rightViews: List<View>,
) = requireActivity().applyEdgeToEdgeMarginIfAvailable(
    clearStatusBar, clearNavigationBar, topViews, bottomViews, leftViews, rightViews,
)

fun FragmentActivity.applyEdgeToEdgeMarginIfAvailable(
    clearStatusBar: Boolean = true,
    clearNavigationBar: Boolean = true,
    leftViews: List<View>,
    topViews: List<View>,
    rightViews: List<View>,
    bottomViews: List<View>,
) {

    applyEdgeToEdgeIfAvailable(
        clearStatusBar = clearStatusBar,
        clearNavigationBar = clearNavigationBar,
        leftViews = leftViews,
        topViews = topViews,
        rightViews = rightViews,
        bottomViews = bottomViews,
        left = { (layoutParams as MarginLayoutParams).leftMargin },
        top = { (layoutParams as MarginLayoutParams).topMargin },
        right = { (layoutParams as MarginLayoutParams).rightMargin },
        bottom = { (layoutParams as MarginLayoutParams).bottomMargin },
    ) { left, top, right, bottom ->
        updateLayoutParams<MarginLayoutParams> {
            updateMargins(left, top, right, bottom)
        }
    }
}

fun FragmentActivity.applyEdgeToEdgeIfAvailable(
    clearStatusBar: Boolean = true,
    clearNavigationBar: Boolean = true,
    leftViews: List<View>,
    topViews: List<View>,
    rightViews: List<View> ,
    bottomViews: List<View>,
    left: View.() -> Int = { 0 },
    top: View.() -> Int = { 0 },
    right: View.() -> Int = { 0 },
    bottom: View.() -> Int = { 0 },
    consumer: View.(left: Int, top: Int, right: Int, bottom: Int,) -> Unit,
) {
    // dark statusbar and navigationbar
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 || isDarkThemeApplied) {
        window.isDrawingUnderSystemBarsAllowed = true
        if (clearStatusBar) {
            window.statusBarColor = Color.TRANSPARENT
        }
        if (clearNavigationBar) {
            window.navigationBarColor = Color.TRANSPARENT
        }

        topViews.forEach {
            it.consumeSystemInsets(left, top, right, bottom) { insets, left, top, right, bottom ->
                it.consumer(left, top + insets.top, right, bottom)
            }
        }

        bottomViews.forEach {
            it.consumeSystemInsets(left, top, right, bottom) { insets, left, top, right, bottom ->
                it.consumer(left, top, right, bottom + insets.bottom)
            }
        }

        leftViews.forEach {
            it.consumeSystemInsets(left, top, right, bottom) { insets, left, top, right, bottom ->
                it.consumer(left + insets.left, top, right, bottom)
            }
        }

        rightViews.forEach {
            it.consumeSystemInsets(left, top, right, bottom) { insets, left, top, right, bottom ->
                it.consumer(left, top, right + insets.right, bottom)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.KITKAT)
private fun <T : View> T.consumeSystemInsets(
    left: T.() -> Int,
    top: T.() -> Int,
    right: T.() -> Int,
    bottom: T.() -> Int,
    consumer: T.(Insets, left: Int, top: Int, right: Int, bottom: Int) -> Unit,
) {
    val topLazy by lazy { top() }
    val bottomLazy by lazy { bottom() }
    val leftLazy by lazy { left() }
    val rightLazy by lazy { right() }

    fun consume(insets: WindowInsetsCompat) {
        val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        consumer(systemInsets, leftLazy, topLazy, rightLazy, bottomLazy)
    }

    whenAttachedToWindow {
        consume(ViewCompat.getRootWindowInsets(this)!!)
    }

    ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
        consume(insets)
        WindowInsetsCompat.CONSUMED
    }
}