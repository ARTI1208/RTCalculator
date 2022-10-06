package ru.art2000.extensions.views

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.content.res.use
import com.google.android.material.appbar.MaterialToolbar

class ColoringToolbar : MaterialToolbar {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    override fun inflateMenu(resId: Int) {
        super.inflateMenu(resId)

        repeat(menu.size()) {
            menu.getItem(it).icon?.tint()
        }
    }

    override fun setNavigationIcon(icon: Drawable?) {
        icon?.tint()
        super.setNavigationIcon(icon)
    }

    private fun getToolbarItemColor(): Int {

        val toolbarTheme = context.theme.obtainStyledAttributes(
            intArrayOf(androidx.appcompat.R.attr.toolbarStyle)
        ).use {
            it.getResourceId(0, -1)
        }

        if (toolbarTheme < 0) return -1;

        val itemColor = context.theme.obtainStyledAttributes(
            toolbarTheme, intArrayOf(androidx.appcompat.R.attr.titleTextColor)
        ).use {
            it.getColor(0, -1)
        }

        return itemColor
    }

    private fun Drawable.tint(
        color: Int = getToolbarItemColor(),
    ) {
        colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }

}