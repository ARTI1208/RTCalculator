package ru.art2000.extensions.fragments

import androidx.fragment.app.Fragment

class ReplaceableFragmentHelper<F>(
    private val fragment: F,
) where F: Fragment, F: IReplaceableFragment {

    private var previousReplaceable: IReplaceableFragment? = null
    private var callOnShownOnViewCreated = false

    private fun canBeShown(): Boolean {
        return fragment.view != null
    }

    /**
     * Called when current fragment becomes primary in the collection and shown
     *
     * @param previousReplaceable object that was previously shown or whatever
     */
    private fun onShown(previousReplaceable: IReplaceableFragment?) {
        fragment.onShown(previousReplaceable)
    }

    /**
     * Called when current fragment becomes primary in the collection. Fragment may not be fully
     * initialized by this time, so override this only if you don't bother that view may not be created,
     * otherwise use [CommonReplaceableFragment.onShown]
     * to be sure fragment view was already created
     *
     * @param previousReplaceable object that was previously shown or whatever
     */
    fun onReplace(previousReplaceable: IReplaceableFragment?) {
        if (canBeShown()) {
            onShown(previousReplaceable)
        } else {
            this.previousReplaceable = previousReplaceable
            callOnShownOnViewCreated = true
        }
    }

    fun onViewCreated() {
        if (callOnShownOnViewCreated) {
            onShown(previousReplaceable)
            previousReplaceable = null
            callOnShownOnViewCreated = false
        }
    }

}