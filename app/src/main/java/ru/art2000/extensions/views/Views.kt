package ru.art2000.extensions.views

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.text.Editable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnPreDrawListener
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.util.Consumer
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

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

@JvmOverloads
fun View.applyWindowTopInsets(margin: Boolean = true) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        applyWindowTopInsetsApi21(margin)
    }
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
private fun View.applyWindowTopInsetsApi21(margin: Boolean) {
    val originalTop by lazy {
        if (margin) {
            (layoutParams as ViewGroup.MarginLayoutParams).topMargin
        } else {
            paddingTop
        }
    }

    fun onAttachedToParent() {
        val rootInsets = ViewCompat.getRootWindowInsets(this)!!
        addTop(originalTop, rootInsets, margin)
    }

    if (isAttachedToWindow) {
        onAttachedToParent()
    } else {
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                onAttachedToParent()
            }

            override fun onViewDetachedFromWindow(v: View) {

            }
        })
    }

    ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
        addTop(originalTop, insets, margin)
        WindowInsetsCompat.CONSUMED
    }
}

private fun View.addTop(originalTop: Int, insets: WindowInsetsCompat, margin: Boolean = false) {
    val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

    if (margin) {
        updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin = originalTop + systemInsets.top
        }
    } else {
        updatePadding(
            top = originalTop + systemInsets.top,
        )
    }
}