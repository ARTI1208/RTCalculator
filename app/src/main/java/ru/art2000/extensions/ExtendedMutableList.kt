package ru.art2000.extensions

interface ExtendedMutableList<E> : MutableList<E> {


    fun setAll(collection: Collection<E>)

    fun addAllNew(collection: Collection<E>)

    fun removeAll(from: Int, count: Int)

    fun replaceAll(map: Map<E, E>)

}