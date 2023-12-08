package ru.art2000.extensions.fragments

interface IReplaceableItem<in R: IReplaceableItem<R>> {

    /**
     * Called when current item becomes primary in the collection
     *
     * @param previousReplaceable object that was previously shown or whatever
     */
    fun onReplace(previousReplaceable: R?) {}

    /**
     * Called when current item becomes secondary in collection
     *
     * @param nextReplaceable item that has replaced current
     */
    fun onReplaced(nextReplaceable: R?) {}

    /**
     * Called when current item is reselected as primary
     *
     */
    fun onReselected()

}