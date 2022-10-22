package ru.art2000.extensions.views

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.SparseArray
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AccelerateInterpolator
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.transition.Slide
import androidx.transition.Transition
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
    private var isTransitionRunning = false
    private var attachedPager2: ViewPager2? = null
    private var mFragmentManager: FragmentManager? = null

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
        val replaceable = getReplaceable(item.order)
        if (replaceable != null) {
            val position = replaceables.indexOfValue(replaceable)
            if (attachedPager2 != null) {
                sendReplaceCallback(replaceable)
                attachedPager2!!.setCurrentItem(position, smoothScrollNext)
            }
            if (mFragmentManager != null) {
                beginFragmentReplace(replaceable)
            }
        }
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
            value && !isTransitionRunning
        }
    }

    fun setupWithViewPager2(
        parentActivity: AppCompatActivity?,
        pager2: ViewPager2,
        vararg replaceableFragments: INavigationFragment
    ) {
        setReplaceableFragments(*replaceableFragments)
        pager2.offscreenPageLimit = replaceableFragments.size - 1
        pager2.isUserInputEnabled = false
        pager2.adapter = object : FragmentStateAdapter(parentActivity!!) {
            override fun createFragment(position: Int): Fragment {
                return replaceableFragments[position] as Fragment
            }

            override fun getItemCount(): Int {
                return replaceableFragments.size
            }
        }
        attachedPager2 = pager2
    }

    @Suppress("unused")
    fun setupWithFragments(
        parentActivity: AppCompatActivity,
        containerId: Int,
        vararg replaceableFragments: INavigationFragment
    ) {
        setReplaceableFragments(*replaceableFragments)
        mFragmentManager = parentActivity.supportFragmentManager
        val fragmentTransaction = mFragmentManager!!.beginTransaction()
        for (navigationFragment in replaceableFragments) {
            val fragment = navigationFragment as Fragment
            val tag = fragment.javaClass.simpleName
            if (mFragmentManager!!.findFragmentByTag(tag) == null) {
                fragmentTransaction
                    .add(containerId, fragment, tag)
                    .hide(fragment)
            }
        }
        fragmentTransaction.commitNow()
    }

    private fun beginFragmentReplace(replaceable: IReplaceableFragment) {
        if (isTransitionRunning) {
            return
        }
        val previousFragment = currentReplaceable as Fragment?
        val nextFragment = replaceable as Fragment
        val previousPosition = replaceables.indexOfValue(currentReplaceable)
        val nextPosition = replaceables.indexOfValue(replaceable)
        val fragmentTransaction = mFragmentManager!!.beginTransaction()
        nextFragment.enterTransition = getEnterTransition(previousPosition, nextPosition)
            .addTarget(nextFragment.requireView())
            .addListener(object : Transition.TransitionListener {
                override fun onTransitionStart(transition: Transition) {
                    isTransitionRunning = true
                }

                override fun onTransitionEnd(transition: Transition) {
                    isTransitionRunning = false
                }

                override fun onTransitionCancel(transition: Transition) {
                    isTransitionRunning = false
                }

                override fun onTransitionPause(transition: Transition) {}
                override fun onTransitionResume(transition: Transition) {}
            })
        if (previousFragment != null) {
            previousFragment.exitTransition = getExitTransition(previousPosition, nextPosition)
                .addTarget(previousFragment.requireView())
            fragmentTransaction.hide(previousFragment)
        }
        fragmentTransaction
            .show(nextFragment)
            .runOnCommit { sendReplaceCallback(nextPosition) }
            .commitNow()
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
        if (currentReplaceable != null) {
            currentReplaceable!!.onReplaced(replaceable)
        }
        currentReplaceable = replaceable
    }

    private fun sendReplaceCallback(position: Int) {
        val replaceable = replaceables.valueAt(position)
        sendReplaceCallback(replaceable)
    }

    private fun getEnterTransition(fromPosition: Int, toPosition: Int): Transition {
        val transition = if (fromPosition < toPosition) {
            Slide(Gravity.END)
        } else {
            Slide(Gravity.START)
        }
        return transition.setInterpolator(AccelerateInterpolator())
    }

    private fun getExitTransition(fromPosition: Int, toPosition: Int): Transition {
        val transition = if (fromPosition < toPosition) {
            Slide(Gravity.START)
        } else {
            Slide(Gravity.END)
        }
        return transition.setInterpolator(AccelerateInterpolator())
    }
}