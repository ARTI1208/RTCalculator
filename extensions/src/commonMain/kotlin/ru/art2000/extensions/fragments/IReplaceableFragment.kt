package ru.art2000.extensions.fragments

interface IReplaceableFragment : IReplaceableItem<IReplaceableFragment> {

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
     */
    override fun onReplace(previousReplaceable: IReplaceableFragment?)

    /**
     * Called when current fragment becomes secondary in collection
     *
     * @param nextReplaceable fragment that has replaced current
     */
    override fun onReplaced(nextReplaceable: IReplaceableFragment?)

    /**
     * Called when current item is reselected as primary
     *
     */
    override fun onReselected()
}