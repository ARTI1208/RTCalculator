package ru.art2000.extensions.views

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.SparseArray
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.art2000.extensions.fragments.INavigationFragment
import ru.art2000.extensions.fragments.IReplaceableFragment

class ReplaceableBottomNavigation @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.bottomNavigationStyle,
    defStyleRes: Int = R.style.Widget_Design_BottomNavigationView,
) : BottomNavigationView(context, attrs, defStyleAttr, defStyleRes) {

    private val replaceables = SparseArray<IReplaceableFragment>()
    private var currentReplaceable: IReplaceableFragment? = null
    private var firstReplaceDone = false
    private var attachedPager2: ViewPager2? = null

    private var smoothScrollNext: Boolean = true

    init {
        super.setOnItemSelectedListener { item ->
            onNavigationItemSelected(item)
            true
        }
    }

    fun setSelectedItemId(@IdRes itemId: Int, smoothScroll: Boolean) {
        smoothScrollNext = smoothScroll
        super.setSelectedItemId(itemId)
        smoothScrollNext = true
    }

    override fun setSelectedItemId(itemId: Int) {
        setSelectedItemId(itemId, true)
    }

    private fun getReplaceable(position: Int): IReplaceableFragment? {
        return replaceables[position, null]
    }

    private fun onNavigationItemSelected(item: MenuItem) {
        val replaceable = getReplaceable(item.order) ?: return
        sendReplaceCallback(replaceable)
        attachedPager2?.setCurrentItem(replaceables.indexOfValue(replaceable), smoothScrollNext)
    }

    private fun onNavigationItemReselected(item: MenuItem) {
        if (!firstReplaceDone) {
            onNavigationItemSelected(item)
            firstReplaceDone = true
        } else {
            currentReplaceable?.onReselected()
        }
    }

    override fun setOnItemReselectedListener(listener: OnItemReselectedListener?) {
        super.setOnItemReselectedListener { item ->
            onNavigationItemReselected(item)
            if (listener != null && firstReplaceDone) {
                listener.onNavigationItemReselected(item)
            }
        }
    }

    override fun setOnItemSelectedListener(listener: OnItemSelectedListener?) {
        super.setOnItemSelectedListener superSetListener@{ item ->
            if (listener == null) return@superSetListener false
            val value = listener.onNavigationItemSelected(item)
            onNavigationItemSelected(item)
            firstReplaceDone = true
            value
        }
    }

    fun setupWithViewPager2(
        parentActivity: FragmentActivity,
        pager2: ViewPager2,
        vararg replaceableFragments: INavigationFragment,
    ) {
        attachedPager2 = pager2
        setReplaceableFragments(*replaceableFragments)
        pager2.offscreenPageLimit = replaceableFragments.size
        pager2.isUserInputEnabled = false
        pager2.adapter = object : FragmentStateAdapter(parentActivity) {
            override fun createFragment(position: Int): Fragment {
                return replaceableFragments[position] as Fragment
            }

            override fun getItemCount(): Int {
                return replaceableFragments.size
            }
        }
    }

    private fun setReplaceableFragments(vararg replaceableFragments: INavigationFragment) {
        menu.clear()
        for (order in replaceableFragments.indices) {
            val replaceableFragment = replaceableFragments[order]
            var id = replaceableFragment.getReplaceableId()
            id = if (id == -1) Menu.NONE else id
            val item = menu.add(
                Menu.NONE,
                id,
                order,
                replaceableFragment.getTitle()
            )
            val iconRes = replaceableFragment.getIcon()
            if (iconRes != -1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    item.setIcon(iconRes)
                } else {
                    val iconDrawable = ResourcesCompat.getDrawable(
                        context.resources, iconRes, context.theme
                    )
                    item.icon = iconDrawable
                }
            }
            replaceables.put(order, replaceableFragment)
        }
    }

    private fun sendReplaceCallback(replaceable: IReplaceableFragment?) {
        if (replaceable != null) {
            if (replaceable === currentReplaceable) {
                replaceable.onReselected()
                return
            }
            replaceable.onReplace(currentReplaceable)
        }
        currentReplaceable?.onReplaced(replaceable)
        currentReplaceable = replaceable
    }

}