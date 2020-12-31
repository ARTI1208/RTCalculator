package ru.art2000.extensions

interface IReplaceableFragment : IReplaceable<IReplaceableFragment> {

    /**
     * Called when current fragment becomes primary in the collection and shown
     *
     * @param previousReplaceable fragment that was previously shown or null
     */
    fun onShown(previousReplaceable: IReplaceableFragment?)

}