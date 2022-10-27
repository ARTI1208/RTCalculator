package ru.art2000.extensions.views

import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnPreDrawListener
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.util.Consumer
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import ru.art2000.extensions.activities.isLtr
import kotlin.math.roundToInt

operator fun TextView.plusAssign(text: CharSequence) {
    append(text)
}

operator fun Editable?.plusAssign(text: CharSequence) {
    this?.append(text)
}

var TextView.textValue: CharSequence
    get() = this.text
    set(value) {
        text = value
    }

fun createTextEmptyView(context: Context, @StringRes text: Int): TextView {
    val emptyView = TextView(context)
    emptyView.setText(text)
    emptyView.layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
    )
    emptyView.gravity = Gravity.CENTER
    return emptyView
}

fun HorizontalScrollView.autoScrollOnInput(lifecycle: Lifecycle) {
    val childEditText = getChildAt(0) as? EditText ?: return

    lifecycle.addObserver(object : DefaultLifecycleObserver {

        private var textChanged = false

        private val textWatcher = object : SimpleTextWatcher() {
            override fun afterTextChanged(s: Editable) {
                textChanged = true
            }
        }

        private val onPreDrawListener = OnPreDrawListener {
            if (textChanged) {
                textChanged = false
                val layout = childEditText.layout ?: return@OnPreDrawListener true
                val (first, second) = childEditText.selectionStart to childEditText.selectionEnd
                if (first != second) return@OnPreDrawListener true
                var xCoordinate = layout.getPrimaryHorizontal(first).toInt()
                val xCoordinate2 = layout.getSecondaryHorizontal(first).toInt()

                val totalPadding: Int
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    xCoordinate = if (childEditText.layoutDirection == View.LAYOUT_DIRECTION_LTR)
                        xCoordinate
                    else
                        xCoordinate2
                    totalPadding =
                        paddingStart + paddingEnd + childEditText.paddingStart + childEditText.paddingEnd
                } else {
                    totalPadding =
                        paddingLeft + paddingRight + childEditText.paddingLeft + childEditText.paddingRight
                }

                var scrollToX =
                    if (xCoordinate > width) xCoordinate - width + totalPadding else xCoordinate
                var isOutOfScreenToStart = false
                if (first > 0) {
                    val previousX = layout.getPrimaryHorizontal(first - 1).toInt()
                    isOutOfScreenToStart = previousX - scrollX < 0
                    if (isOutOfScreenToStart) {
                        scrollToX = scrollToX - xCoordinate + previousX
                    }
                }
                val isOutOfScreenToEnd = xCoordinate - scrollX > width - totalPadding
                if (isOutOfScreenToStart || isOutOfScreenToEnd) {
                    scrollTo(scrollToX, 0)
                }
            }
            true
        }

        override fun onCreate(owner: LifecycleOwner) {
            childEditText.addTextChangedListener(textWatcher)
            childEditText.viewTreeObserver.addOnPreDrawListener(onPreDrawListener)
        }

        override fun onDestroy(owner: LifecycleOwner) {
            childEditText.removeTextChangedListener(textWatcher)
            childEditText.viewTreeObserver.removeOnPreDrawListener(onPreDrawListener)
        }
    })
}

fun View.addImeVisibilityListener(listener: Consumer<Boolean>): ListenerSubscription<Boolean> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        addImeVisibilityListenerApi21(listener)
    } else {
        addImeVisibilityListenerApi16(listener)
    }
}

/*
 * Heuristic implementation for old APIs. Based on https://stackoverflow.com/a/26964010
 * TODO not works for fullscreen keyboard
 * TODO may use many CPU resources because OnGlobalLayoutListener called regularly
 */
private fun View.addImeVisibilityListenerApi16(listener: Consumer<Boolean>): ListenerSubscription<Boolean> {
    var keyboardVisible = false

    val layoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        val r = Rect()
        getWindowVisibleDisplayFrame(r)
        val screenHeight = rootView.height

        // r.bottom is the position above soft keypad or device button.
        // if keypad is shown, the r.bottom is smaller than that before.

        // r.bottom is the position above soft keypad or device button.
        // if keypad is shown, the r.bottom is smaller than that before.
        val keypadHeight = screenHeight - r.bottom

        if (keypadHeight > screenHeight * 0.2) {
            // keyboard is opened
            if (!keyboardVisible) {
                keyboardVisible = true
                listener.accept(true)
            }
        } else {
            // keyboard is closed
            if (keyboardVisible) {
                keyboardVisible = false
                listener.accept(false)
            }
        }
    }

    return attachImeListener(viewTreeObserver, layoutListener, listener)
}

/*
 * Exact implementation using new APIs
 * TODO not works for fullscreen keyboard on API 21-29
 * TODO may use many CPU resources because OnGlobalLayoutListener called regularly
 */
@RequiresApi(21)
private fun View.addImeVisibilityListenerApi21(listener: Consumer<Boolean>): ListenerSubscription<Boolean> {
    var keyboardVisible = false

    // Not setOnApplyWindowInsetsListener because it doesn't report in landscape
    val layoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        val rootInsets = ViewCompat.getRootWindowInsets(this) ?: return@OnGlobalLayoutListener

        val isImeVisible = rootInsets.isVisible(WindowInsetsCompat.Type.ime())
        if (isImeVisible != keyboardVisible) {
            keyboardVisible = isImeVisible
            listener.accept(isImeVisible)
        }
    }

    return attachImeListener(viewTreeObserver, layoutListener, listener)
}

private fun attachImeListener(
    viewTreeObserver: ViewTreeObserver,
    layoutListener: ViewTreeObserver.OnGlobalLayoutListener,
    imeListener: Consumer<Boolean>
): ListenerSubscription<Boolean> {
    viewTreeObserver.addOnGlobalLayoutListener(layoutListener)
    return ListenerSubscription {
        if (it != null) imeListener.accept(it)
        if (viewTreeObserver.isAlive) {
            viewTreeObserver.removeOnGlobalLayoutListener(layoutListener)
        }
    }
}

fun interface ListenerSubscription<T> {
    operator fun invoke(value: T?)

    operator fun invoke() = invoke(null)
}

inline val Context.isLandscape: Boolean
    get() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

val Context.isDarkThemeApplied: Boolean
    get() {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
    }

@RequiresApi(Build.VERSION_CODES.KITKAT)
fun <T : View> T.whenAttachedToWindow(callback: (T) -> Unit) {
    if (isAttachedToWindow) {
        callback(this)
    } else {
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                @Suppress("UNCHECKED_CAST")
                callback(v as T)
            }

            override fun onViewDetachedFromWindow(v: View) {

            }
        })
    }
}

fun RecyclerView.addOrientationItemDecoration(drawForLastInRow: Boolean = false) {
    if (context.isLandscape) {
        addItemDecoration(FixedDividerItemDecoration(
            context, FixedDividerItemDecoration.HORIZONTAL, drawForLastInRow,
        ))
    }
}

/**
 * Copy of FixedDividerItemDecoration.
 * Draws divider of the same size as recycler item.
 * Also can skip drawing for last item in row.
 */
private class FixedDividerItemDecoration(
    context: Context,
    private var mOrientation: Int,
    private val drawForLastInRow: Boolean,
) : ItemDecoration() {

    companion object {

        const val HORIZONTAL = LinearLayout.HORIZONTAL
        const val VERTICAL = LinearLayout.VERTICAL

        private val attrs = intArrayOf(android.R.attr.listDivider)
    }

    private val mDivider: Drawable

    private val mBounds = Rect()

    init {
        val a = context.obtainStyledAttributes(attrs)
        val divider = a.getDrawable(0)
        mDivider = checkNotNull(divider) {
            a.recycle()
            "@android:attr/listDivider was not set in the theme used for this " +
                    "DividerItemDecoration. Please set that attribute all call setDrawable()"
        }
        a.recycle()
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.layoutManager == null) {
            return
        }
        if (mOrientation == VERTICAL) {
            drawVertical(c, parent)
        } else {
            drawHorizontal(c, parent)
        }
    }

    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        canvas.save()

        if (parent.clipToPadding) {
            val left = parent.paddingLeft
            val right = parent.width - parent.paddingRight
            canvas.clipRect(
                left, parent.paddingTop, right,
                parent.height - parent.paddingBottom
            )
        }

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)

            if (!drawForLastInRow && isLastInRow(parent, child) == true) continue

            parent.getDecoratedBoundsWithMargins(child, mBounds)
            val bottom = mBounds.bottom + child.translationY.roundToInt()
            val top = bottom - mDivider.intrinsicHeight
            mDivider.setBounds(child.x.toInt(), top, (child.x + child.width).toInt(), bottom)
            mDivider.draw(canvas)
        }
        canvas.restore()
    }

    private fun isLastInRow(recyclerView: RecyclerView, view: View): Boolean? {
        val manager = recyclerView.layoutManager as? GridLayoutManager ?: return null
        val params = view.layoutParams as? GridLayoutManager.LayoutParams ?: return null

        return params.spanIndex + params.spanSize == manager.spanCount
    }

    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
        canvas.save()

        if (parent.clipToPadding) {
            val top = parent.paddingTop
            val bottom = parent.height - parent.paddingBottom
            canvas.clipRect(
                parent.paddingLeft, top,
                parent.width - parent.paddingRight, bottom
            )
        }

        val ltr = isLtr

        val childCount = parent.childCount
        for (i in 0 until childCount) {

            val child = parent.getChildAt(i)

            if (!drawForLastInRow && isLastInRow(parent, child) == true) continue

            parent.layoutManager!!.getDecoratedBoundsWithMargins(child, mBounds)
            val right = if (ltr) mBounds.right else mBounds.left + child.translationX.roundToInt()
            val left = right - mDivider.intrinsicWidth
            mDivider.setBounds(left, child.y.toInt(), right, (child.y + child.height).toInt())
            mDivider.draw(canvas)
        }
        canvas.restore()
    }

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (mOrientation == VERTICAL) {
            outRect.set(0, 0, 0, mDivider.intrinsicHeight)
        } else {
            outRect.set(0, 0, mDivider.intrinsicWidth, 0)
        }
    }
}

@Suppress("FunctionName")
fun OrientationManger(context: Context, isFullSpan: (Int) -> Boolean = { false }): GridLayoutManager {
    val spanCount = if (context.isLandscape) 2 else 1
    return GridLayoutManager(context, spanCount).apply {
        spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (isFullSpan(position)) spanCount else 1
            }
        }
    }
}