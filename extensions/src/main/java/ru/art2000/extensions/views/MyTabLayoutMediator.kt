package ru.art2000.extensions.views

import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy

/**
 * Modification of TabLayoutMediator that doesn't animate page switch
 * if it's caused by tab click
 *
 * TODO add tab switch animation on tab click
 */
class MyTabLayoutMediator(
    private val tabLayout: TabLayout,
    private val viewPager: ViewPager2,
    autoRefresh: Boolean,
    private val smoothScroll: Boolean,
    tabConfigurationStrategy: TabConfigurationStrategy,
) {

    constructor(
        tabLayout: TabLayout,
        viewPager: ViewPager2,
        tabConfigurationStrategy: TabConfigurationStrategy
    ): this(tabLayout, viewPager, true, tabConfigurationStrategy)

    constructor(
        tabLayout: TabLayout,
        viewPager: ViewPager2,
        autoRefresh: Boolean,
        tabConfigurationStrategy: TabConfigurationStrategy
    ): this(tabLayout, viewPager, autoRefresh, true, tabConfigurationStrategy)

    private val mediator = TabLayoutMediator(tabLayout, viewPager,
        autoRefresh, smoothScroll, tabConfigurationStrategy)

    private var myOnTabSelectedListener: OnTabSelectedListener? = null

    fun attach() {
        val wasAttached = mediator.isAttached
        mediator.attach()
        if (!wasAttached && mediator.isAttached) {
            tabLayout.removeOnTabSelectedListener(mediator.onTabSelectedListener!!)

            val myListener = MyViewPagerOnTabSelectedListener(
                viewPager,
                smoothScroll,
            )
            myOnTabSelectedListener = myListener

            tabLayout.addOnTabSelectedListener(myListener)
        }
    }

    fun detach() {
        mediator.detach()
        myOnTabSelectedListener?.also { tabLayout.removeOnTabSelectedListener(it) }
    }

    private class MyViewPagerOnTabSelectedListener(
        private val viewPager: ViewPager2,
        private val smoothScroll: Boolean,
    ) : OnTabSelectedListener {

        override fun onTabSelected(tab: TabLayout.Tab) {
            val isTabPressed = viewPager.currentItem != tab.position
            viewPager.setCurrentItem(tab.position, smoothScroll && !isTabPressed)
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {
            // No-op
        }

        override fun onTabReselected(tab: TabLayout.Tab) {
            // No-op
        }
    }

    companion object {

        private val TabLayoutMediator.onTabSelectedListener: OnTabSelectedListener?
            get() = onTabSelectedListenerField.get(this) as OnTabSelectedListener?

        private val onTabSelectedListenerField by lazy {
            TabLayoutMediator::class.java.getDeclaredField("onTabSelectedListener").apply {
                isAccessible = true
            }
        }
    }
}