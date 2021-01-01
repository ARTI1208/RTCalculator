package ru.art2000.extensions.fragments

interface IReplaceableFragment {

    /**
     * Called when current fragment becomes primary in the collection and shown
     *
     * @param previousReplaceable fragment that was previously shown or null
     */
    fun onShown(previousReplaceable: IReplaceableFragment?)

    /**
     * Called when current fragment becomes primary in the collection. Fragment
     * may not be fully initialized by this time, so you must override [onShown]
     * to be sure fragment's view was already created
     *
     * @param previousReplaceable object that was previously shown or whatever
     *
     * @see CommonReplaceableFragment
     * @see UniqueReplaceableFragment
     * @see NavigationFragment
     * @see PreferenceNavigationFragment
     */
    fun onReplace(previousReplaceable: IReplaceableFragment?)

    /**
     * Called when current fragment becomes secondary in collection
     *
     * @param nextReplaceable fragment that has replaced current
     */
    fun onReplaced(nextReplaceable: IReplaceableFragment?)

    /**
     * Called when current fragment is reselected as primary
     *
     */
    fun onReselected()
}