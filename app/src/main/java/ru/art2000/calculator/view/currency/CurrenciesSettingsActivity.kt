package ru.art2000.calculator.view.currency

import android.animation.Animator
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup.*
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.databinding.ObservableList
import androidx.databinding.ObservableList.OnListChangedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.art2000.calculator.R
import ru.art2000.calculator.model.currency.CurrencyItem
import ru.art2000.calculator.view_model.currency.CurrenciesSettingsModel
import ru.art2000.calculator.view_model.currency.CurrencyDependencies.getCurrencyDatabase
import ru.art2000.extensions.DayNightActivity
import ru.art2000.extensions.LiveList.LiveListObserver
import ru.art2000.extensions.LiveMap
import ru.art2000.extensions.ReplaceableFragment
import ru.art2000.extensions.SimpleOnListChangedCallback
import ru.art2000.helpers.AndroidHelper
import ru.art2000.helpers.PrefsHelper
import ru.art2000.helpers.SnackbarThemeHelper
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors
import kotlin.collections.ArrayList

class CurrenciesSettingsActivity : DayNightActivity() {
    var add = CurrenciesAddFragment()
    var edit = CurrenciesEditFragment()
    var mContext: Context? = null
    var fab: FloatingActionButton? = null
    var deselect: MenuItem? = null
    var select: MenuItem? = null
    var tabs: TabLayout? = null
    var selectedTab = 0

    @DrawableRes
    var checkDrawable = R.drawable.ic_currencies_done

    @DrawableRes
    var deleteDrawable = R.drawable.ic_clear_history

    @DrawableRes
    var currentDrawable = checkDrawable
    @JvmField
    var isFirstTimeTooltipShown = !PrefsHelper.isDeleteTooltipShown()
    @JvmField
    var deleteTooltip: Snackbar? = null
    var coordinatorLayout: CoordinatorLayout? = null
    var searchViewLayout: LinearLayout? = null
    @JvmField
    var barSearchView: SearchView? = null
    @JvmField
    var useViewPager2 = true
    var optionsMenuCreated = false
    var model: CurrenciesSettingsModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(PrefsHelper.getAppTheme())
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currencies_editor)
        mContext = this
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        model = ViewModelProvider(this,
                AndroidViewModelFactory(application)
        ).get(CurrenciesSettingsModel::class.java)
        fab = findViewById(R.id.floatingActionButton)
        fab?.addOnShowAnimationListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {
                fab?.setImageResource(currentDrawable)
            }

            override fun onAnimationEnd(animator: Animator) {}
            override fun onAnimationCancel(animator: Animator) {
                fab?.setImageResource(currentDrawable)
            }

            override fun onAnimationRepeat(animator: Animator) {}
        })
        fab?.addOnHideAnimationListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {}
            override fun onAnimationEnd(animator: Animator) {
                if (currentDrawable == deleteDrawable) {
                    showDeleteTip()
                }
            }

            override fun onAnimationCancel(animator: Animator) {}
            override fun onAnimationRepeat(animator: Animator) {}
        })
        coordinatorLayout = findViewById(R.id.coordinator)
        searchViewLayout = findViewById(R.id.search_view_layout)
        barSearchView = findViewById(R.id.search_view)
        tabs = findViewById(R.id.tabs)
        val pager = findViewById<ViewPager>(R.id.pager)
        val pager2 = findViewById<ViewPager2>(R.id.pager2)
        if (useViewPager2) {
            pager.visibility = View.GONE
            pager2.offscreenPageLimit = 2
            pager2.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    val maxScroll = pager2.measuredWidth
                    val currentScroll = maxScroll * position + positionOffsetPixels
                    searchViewLayout?.translationX = -currentScroll.toFloat()
                    if (deleteTooltip != null) {
                        deleteTooltip!!.view.translationX = maxScroll - currentScroll.toFloat()
                    }
                }
            })
            val pager2Adapter = CurrencyEditorPager2Adapter()
            pager2.adapter = pager2Adapter
            TabLayoutMediator(tabs!!, pager2, TabConfigurationStrategy { tab: TabLayout.Tab, position: Int -> tab.text = pager2Adapter.getPageTitle(position) }).attach()
        } else {
            pager2.visibility = View.GONE
            pager.addOnPageChangeListener(object : OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    val maxScroll = pager.measuredWidth
                    val currentScroll = maxScroll * position + positionOffsetPixels
                    searchViewLayout!!.setTranslationX(-currentScroll.toFloat())
                    if (deleteTooltip != null) {
                        deleteTooltip!!.view.translationX = maxScroll - currentScroll.toFloat()
                    }
                }

                override fun onPageSelected(position: Int) {}
                override fun onPageScrollStateChanged(state: Int) {}
            })
            pager.adapter = CurrencyEditorPagerAdapter(supportFragmentManager)
            tabs!!.setupWithViewPager(pager)
        }
        tabs!!.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                model!!.selectedTab = tab.position
                when (selectedTab) {
                    0 -> {
                        add.onReplaced(edit)
                        edit.onReplace(add)
                    }
                    1 -> {
                        edit.onReplaced(add)
                        add.onReplace(edit)
                    }
                    else -> {
                        add.onReplaced(edit)
                        edit.onReplace(add)
                    }
                }
                selectedTab = tab.position
                fab!!.hide()
                modifyVisualElements(tab.position)
                if (optionsMenuCreated) {
                    toggleElementsVisibility()
                }
                if (selectedTab == 1 && !fab!!.isShown()) {
                    showDeleteTip()
                } else if (selectedTab == 0 && deleteTooltip != null) {
                    deleteTooltip!!.dismiss()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> add.onReselected()
                    1 -> edit.onReselected()
                    else -> add.onReselected()
                }
            }
        })
//        toggleElementsVisibility()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        searchViewLayout!!.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        //TODO better solution
        add.recyclerViewBottomPadding = searchViewLayout!!.measuredHeight
        val layoutParams = fab!!.layoutParams as CoordinatorLayout.LayoutParams
        layoutParams.bottomMargin = add.recyclerViewBottomPadding
        fab!!.layoutParams = layoutParams
        selectedTab = tabs!!.selectedTabPosition
        modifyVisualElements(selectedTab)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.currencies_editor_menu, menu)
        deselect = menu.findItem(R.id.deselect_all)
        applyMenuIconTint(deselect!!.icon)
        deselect!!.isVisible = false
        select = menu.findItem(R.id.select_all)
        applyMenuIconTint(select!!.icon)
        select!!.isVisible = false

        optionsMenuCreated = true
        barSearchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                model!!.filterHiddenItems(newText)
                return true
            }
        })

        val liveListObserver: LiveListObserver<CurrencyItem> = object : LiveListObserver<CurrencyItem>() {
            override fun onAnyChanged(previousList: List<CurrencyItem>) {
                Log.d("obs", "prev: " +previousList.joinToString { it.code })
                toggleElementsVisibility()
            }
        }

        val liveListObserver1: LiveListObserver<CurrencyItem> = object : LiveListObserver<CurrencyItem>() {
            override fun onAnyChanged(previousList: List<CurrencyItem>) {
                Log.d("obs1", "prev: " +previousList.joinToString { it.code })
                toggleElementsVisibility()
            }
        }

        val liveListObserver2: LiveListObserver<CurrencyItem> = object : LiveListObserver<CurrencyItem>() {
            override fun onAnyChanged(previousList: List<CurrencyItem>) {
                Log.d("obs2", "prev: " +previousList.joinToString { it.code })
                toggleElementsVisibility()
            }
        }

        model!!.selectedHiddenItems.observe(this, liveListObserver)
        model!!.selectedVisibleItems.observe(this, liveListObserver1)
        model!!.displayedHiddenItems.observe(this, liveListObserver2)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.deselect_all -> if (selectedTab == 0) {
                model!!.selectedHiddenItems.clear()
            } else {
                model!!.selectedVisibleItems.clear()
            }
            R.id.select_all -> if (selectedTab == 0) {
                model!!.completeSelectedHiddenItems()
            } else {
                model!!.completeSelectedVisibleItems()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    fun showDeleteTip() {
        if (!isFirstTimeTooltipShown) {
            return
        }
        if (deleteTooltip != null) {
            if (!deleteTooltip!!.isShown) {
                deleteTooltip!!.show()
            }
            return
        }
        deleteTooltip = createStyledSnackbar(
                R.string.tooltip_remove_currency, Snackbar.LENGTH_INDEFINITE)
        deleteTooltip!!.addCallback(object : Snackbar.Callback() {
            override fun onShown(sb: Snackbar) {
                PrefsHelper.setDeleteTooltipShown()
            }

            override fun onDismissed(transientBottomBar: Snackbar, event: Int) {
                if (event == DISMISS_EVENT_ACTION
                        || event == DISMISS_EVENT_SWIPE) {
                    isFirstTimeTooltipShown = false
                }
            }
        })
        deleteTooltip!!.setAction(R.string.action_tooltip_got_it) { actionView: View? -> }
        deleteTooltip!!.show()
    }

    private fun applyMenuIconTint(icon: Drawable) {
        icon.colorFilter = PorterDuffColorFilter(
                AndroidHelper.getColorAttribute(this, R.attr.colorAccent),
                PorterDuff.Mode.SRC_ATOP)
    }

    fun toggleElementsVisibility() {
        if (selectedTab == 0) {
            val selectedCount = model!!.selectedHiddenItems.size
            val totalCount = model!!.displayedHiddenItems.size
            if (selectedCount > 0) {
                fab!!.show()
            } else {
                fab!!.hide()
            }
            select?.isVisible = selectedCount < totalCount
            deselect?.isVisible = selectedCount > 0
            searchViewLayout?.visibility = if (totalCount > 0) View.VISIBLE else View.GONE
            Log.d("SearchVis", searchViewLayout?.visibility.toString() + " | " + totalCount)
        } else {
            val selectedCount = model!!.selectedVisibleItems.size
            val totalCount = model!!.displayedVisibleItems.size
            Log.d("VisChange", "$selectedCount/$totalCount")
            if (model!!.isEditSelectionMode && selectedCount > 0) {
                fab!!.show()
            } else {
                fab!!.hide()
            }
            select?.isVisible = model!!.isEditSelectionMode && selectedCount < totalCount
            deselect?.isVisible = model!!.isEditSelectionMode && selectedCount > 0
        }
    }

    fun setNewFabImage(resId: Int) {
        currentDrawable = resId
        fab!!.hide()
    }

    private fun styleSnackbar(snackbar: Snackbar): Snackbar {
        val snackbarView = snackbar.view
        val cardView = findViewById<MaterialCardView>(R.id.card_wrapper)
        val cardLayoutParams = cardView.layoutParams as MarginLayoutParams
        var leftMargin = cardLayoutParams.leftMargin + cardView.paddingLeft
        var rightMargin = cardLayoutParams.rightMargin + cardView.paddingRight
        var height = cardView.getChildAt(0).measuredHeight
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            height += (cardView.paddingBottom
                    + cardView.strokeWidth
                    - cardView.cardElevation.toInt())
            leftMargin -= (cardView.cardElevation + cardView.strokeWidth).toInt()
            rightMargin -= (cardView.cardElevation + cardView.strokeWidth).toInt()
        } else {
            snackbarView.elevation = 0f
        }
        snackbarView.minimumHeight = height
        snackbarView.translationY = cardView.paddingTop - cardView.strokeWidth.toFloat()
        val snackLayoutParams = snackbarView.layoutParams as MarginLayoutParams
        snackLayoutParams.leftMargin = leftMargin
        snackLayoutParams.rightMargin = rightMargin
        snackbarView.layoutParams = snackLayoutParams
        return snackbar
    }

    private fun createStyledSnackbar(@StringRes message: Int, duration: Int): Snackbar {
        return styleSnackbar(
                SnackbarThemeHelper.createThemedSnackbar(coordinatorLayout!!, message, duration))
    }

    private fun createStyledSnackbar(message: CharSequence, duration: Int): Snackbar {
        return styleSnackbar(
                SnackbarThemeHelper.createThemedSnackbar(coordinatorLayout!!, message, duration))
    }

    fun generateUndoSnackbar(editedItems: List<CurrencyItem>, added: Boolean) {
        val message = if (editedItems.size == 1) {
            mContext!!.getString(
                    if (added) R.string.message_item_shown else R.string.message_item_hidden,
                    editedItems[0].code)
        } else {
            mContext!!.resources.getQuantityString(
                    if (added) R.plurals.message_items_shown else R.plurals.message_items_hidden,
                    editedItems.size,
                    editedItems.size)
        }
        val undoSnackbar = createStyledSnackbar(message, Snackbar.LENGTH_LONG)
        undoSnackbar.setAction(R.string.action_undo) { view: View? ->
            Completable
                    .fromRunnable {
                        if (added) {
                            model!!.makeItemsHidden(editedItems)
                        } else {
                            model!!.makeItemsVisible(editedItems)
                        }
                    }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete { generateUndoSnackbar(editedItems, !added) }.subscribe()
        }
        undoSnackbar.show()
    }

    fun modifyVisualElements(tabPos: Int) {
        if (tabPos == 0) {
            setNewFabImage(checkDrawable)
            fab!!.setOnClickListener { v: View? ->
                val selectedItems = ArrayList(model!!.selectedHiddenItems)
                Completable
                        .fromRunnable {
                            model?.makeItemsVisible(selectedItems)
                        }.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete { generateUndoSnackbar(selectedItems, true) }
                        .subscribe()
            }
        } else {
            setNewFabImage(deleteDrawable)
            fab!!.setOnClickListener { v: View? ->
                val selectedItems = ArrayList(model!!.selectedVisibleItems)
                Completable
                        .fromRunnable {
                            model?.makeItemsHidden(selectedItems)
                        }.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete { generateUndoSnackbar(selectedItems, false) }
                        .subscribe()
            }
        }
    }

    internal inner class CurrencyEditorPager2Adapter : FragmentStateAdapter(this@CurrenciesSettingsActivity) {
        var categories: Array<String> = resources.getStringArray(R.array.currency_categories)
        var fragments: Array<ReplaceableFragment> = arrayOf(add, edit)
        override fun getItemCount(): Int {
            return fragments!!.size
        }

        fun getPageTitle(position: Int): CharSequence? {
            return fragments!![position].getTitle(mContext)
        }

        override fun createFragment(position: Int): Fragment {
            return fragments!![position]
        }

        init {

//            List<Fragment> list = CurrenciesSettingsActivity.this.getSupportFragmentManager().getFragments();
//            if (list.size() > 0) {
//                fragments = new ReplaceableFragment[list.size()];
//                fragments = list.toArray(fragments);
//                add = (CurrenciesAddFragment) fragments[0];
//                if (list.size() > 1) {
//                    edit = (CurrenciesEditFragment) fragments[1];
//                }
//            }
//            if (fragments == null) {
//                fragments = arrayOf(add, edit)
//            }
        }
    }

    internal inner class CurrencyEditorPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        var categories: Array<String>
        var fragments: Array<Fragment> = arrayOf(add, edit)
        override fun getPageTitle(position: Int): CharSequence? {
            return categories[position]
        }

        override fun getItem(position: Int): Fragment {
            return fragments!![position]
        }

        override fun getCount(): Int {
            return categories.size
        }

        init {
            categories = resources.getStringArray(R.array.currency_categories)
            val list = fm.fragments
            if (list.size > 0) {
//                fragments = arrayOfNulls(list.size)
//                fragments = list.toArray(fragments)
                add = fragments.get(0) as CurrenciesAddFragment
                edit = fragments.get(1) as CurrenciesEditFragment
            }
            if (fragments == null) {
                fragments = arrayOf(add, edit)
            }
        }
    }
}