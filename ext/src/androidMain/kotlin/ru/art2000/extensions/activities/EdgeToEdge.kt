package ru.art2000.extensions.activities

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.annotation.RequiresApi
import androidx.core.graphics.Insets
import androidx.core.view.*
import androidx.fragment.app.Fragment
import ru.art2000.extensions.layout.isLtr
import ru.art2000.extensions.views.isDarkThemeApplied
import ru.art2000.extensions.views.isDrawingUnderSystemBarsAllowed
import ru.art2000.extensions.views.whenAttachedToWindow

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
}

interface IEdgeToEdgeActivity : EdgeToEdgeScreen {

    override val clearStatusBar: Boolean
        get() = true

    override val clearNavigationBar: Boolean
        get() = true
}

internal fun <S> S.applyEdgeToEdgeIfAvailable() where S : Fragment, S: EdgeToEdgeScreen {
    applyEdgeToEdgeIfAvailable(requireActivity(), this)
}

internal fun <S> S.applyEdgeToEdgeIfAvailable() where S : Activity, S: EdgeToEdgeScreen {
    applyEdgeToEdgeIfAvailable(this, this)
}

private fun applyEdgeToEdgeIfAvailable(activity: Activity, edgeToEdgeScreen: EdgeToEdgeScreen) {
    val top = edgeToEdgeScreen.topViews
    val bottom = edgeToEdgeScreen.bottomViews
    var left = edgeToEdgeScreen.leftViews
    var right = edgeToEdgeScreen.rightViews

    if (top.isEmpty() && bottom.isEmpty() && left.isEmpty() && right.isEmpty()) return

    if (!isLtr) {
        left = right.also { right = left }
    }

    if (edgeToEdgeScreen.insetAsPadding) {
        activity.applyEdgeToEdgePaddingIfAvailable(
            clearStatusBar = edgeToEdgeScreen.clearStatusBar,
            clearNavigationBar = edgeToEdgeScreen.clearNavigationBar,
            topViews = top,
            bottomViews = bottom,
            leftViews = left,
            rightViews = right,
        )
    } else {
        activity.applyEdgeToEdgeMarginIfAvailable(
            clearStatusBar = edgeToEdgeScreen.clearStatusBar,
            clearNavigationBar = edgeToEdgeScreen.clearNavigationBar,
            topViews = top,
            bottomViews = bottom,
            leftViews = left,
            rightViews = right,
        )
    }
}

private fun Activity.applyEdgeToEdgePaddingIfAvailable(
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

private fun Activity.applyEdgeToEdgeMarginIfAvailable(
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

private fun Activity.applyEdgeToEdgeIfAvailable(
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
    consumer: View.(left: Int, top: Int, right: Int, bottom: Int) -> Unit,
) {
    // dark statusbar and navigationbar
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return

    val clearStatusBarReally = clearStatusBar &&
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M || isDarkThemeApplied)

    val clearNavigationBarReally = clearNavigationBar &&
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 || isDarkThemeApplied)

    clearSystemBars(clearStatusBarReally, clearNavigationBarReally)

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

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun Activity.clearSystemBars(
    clearStatusBar: Boolean = true,
    clearNavigationBar: Boolean = true,
) {
    window.isDrawingUnderSystemBarsAllowed = true
    if (clearStatusBar) {
//      window.statusBarColor = ColorUtils.setAlphaComponent(window.statusBarColor, 200)
        window.statusBarColor = Color.TRANSPARENT
    }
    if (clearNavigationBar) {
        window.apply {
            navigationBarColor = Color.TRANSPARENT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                isNavigationBarContrastEnforced = false
            }
        }
    }
}

@Suppress("NAME_SHADOWING")
@RequiresApi(Build.VERSION_CODES.KITKAT)
private fun <T : View> T.consumeSystemInsets(
    left: T.() -> Int,
    top: T.() -> Int,
    right: T.() -> Int,
    bottom: T.() -> Int,
    consumer: T.(Insets, left: Int, top: Int, right: Int, bottom: Int) -> Unit,
)  = consumeInsets(left, top, right, bottom) { windowInsetsCompat, left, top, right, bottom ->
    consumer(
        windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars()),
        left, top, right, bottom,
    )
}

fun <T : View> T.consumeInsets(
    left: T.() -> Int,
    top: T.() -> Int,
    right: T.() -> Int,
    bottom: T.() -> Int,
    consumer: T.(WindowInsetsCompat, left: Int, top: Int, right: Int, bottom: Int) -> Unit,
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return

    val topLazy by lazy { top() }
    val bottomLazy by lazy { bottom() }
    val leftLazy by lazy { left() }
    val rightLazy by lazy { right() }

    fun consume(insets: WindowInsetsCompat) {
        consumer(insets, leftLazy, topLazy, rightLazy, bottomLazy)
    }

    whenAttachedToWindow {
        consume(ViewCompat.getRootWindowInsets(this)!!)
    }

    ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
        consume(insets)
        insets
    }
}

@Suppress("unused")
fun <T : View> T.consumeInsetsForPadding(
    consumer: T.(WindowInsetsCompat, left: Int, top: Int, right: Int, bottom: Int) -> Unit,
) = consumeInsets({ paddingLeft }, { paddingTop }, { paddingRight }, { paddingBottom }, consumer)

fun <T : View> T.consumeInsetsForMargin(
    consumer: T.(WindowInsetsCompat, left: Int, top: Int, right: Int, bottom: Int) -> Unit,
) = consumeInsets(
    left = { (layoutParams as MarginLayoutParams).leftMargin },
    top = { (layoutParams as MarginLayoutParams).topMargin },
    right = { (layoutParams as MarginLayoutParams).rightMargin },
    bottom = { (layoutParams as MarginLayoutParams).bottomMargin },
    consumer,
)