package ru.art2000.extensions.activities

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.graphics.Insets
import androidx.core.view.*
import ru.art2000.extensions.layout.isLtr
import ru.art2000.extensions.views.whenAttachedToWindow

interface EdgeToEdgeScreen {

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

interface IEdgeToEdgeFragment : EdgeToEdgeScreen

interface IEdgeToEdgeActivity : EdgeToEdgeScreen

fun <S> S.applyEdgeToEdgeIfAvailable() where S : EdgeToEdgeScreen {
    val top = topViews
    val bottom = bottomViews
    var left = leftViews
    var right = rightViews

    if (top.isEmpty() && bottom.isEmpty() && left.isEmpty() && right.isEmpty()) return

    if (!isLtr) {
        left = right.also { right = left }
    }

    if (insetAsPadding) {
        applyEdgeToEdgePaddingIfAvailable(
            topViews = top,
            bottomViews = bottom,
            leftViews = left,
            rightViews = right,
        )
    } else {
        applyEdgeToEdgeMarginIfAvailable(
            topViews = top,
            bottomViews = bottom,
            leftViews = left,
            rightViews = right,
        )
    }
}

private fun applyEdgeToEdgePaddingIfAvailable(
    topViews: List<View>,
    bottomViews: List<View>,
    leftViews: List<View>,
    rightViews: List<View>,
) {

    applyEdgeToEdgeIfAvailable(
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

private fun applyEdgeToEdgeMarginIfAvailable(
    leftViews: List<View>,
    topViews: List<View>,
    rightViews: List<View>,
    bottomViews: List<View>,
) {

    applyEdgeToEdgeIfAvailable(
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

private inline fun applyEdgeToEdgeIfAvailable(
    leftViews: List<View>,
    topViews: List<View>,
    rightViews: List<View>,
    bottomViews: List<View>,
    crossinline left: View.() -> Int = { 0 },
    crossinline top: View.() -> Int = { 0 },
    crossinline right: View.() -> Int = { 0 },
    crossinline bottom: View.() -> Int = { 0 },
    crossinline consumer: View.(left: Int, top: Int, right: Int, bottom: Int) -> Unit,
) {

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

@Suppress("NAME_SHADOWING")
private inline fun <T : View> T.consumeSystemInsets(
    crossinline left: T.() -> Int,
    crossinline top: T.() -> Int,
    crossinline right: T.() -> Int,
    crossinline bottom: T.() -> Int,
    crossinline consumer: T.(Insets, left: Int, top: Int, right: Int, bottom: Int) -> Unit,
)  = consumeInsets(left, top, right, bottom) { windowInsetsCompat, left, top, right, bottom ->
    consumer(
        windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars()),
        left, top, right, bottom,
    )
}

private inline fun <T : View> T.consumeInsets(
    crossinline left: T.() -> Int,
    crossinline top: T.() -> Int,
    crossinline right: T.() -> Int,
    crossinline bottom: T.() -> Int,
    crossinline consumer: T.(WindowInsetsCompat, left: Int, top: Int, right: Int, bottom: Int) -> Unit,
) {

    val topLazy by lazy { top() }
    val bottomLazy by lazy { bottom() }
    val leftLazy by lazy { left() }
    val rightLazy by lazy { right() }

    whenAttachedToWindow {
        val insets = ViewCompat.getRootWindowInsets(this)!!
        consumer(insets, leftLazy, topLazy, rightLazy, bottomLazy)
    }

    ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
        consumer(insets, leftLazy, topLazy, rightLazy, bottomLazy)
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