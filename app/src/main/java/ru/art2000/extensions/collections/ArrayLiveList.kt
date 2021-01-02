package ru.art2000.extensions.collections

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback

class ArrayLiveList<E> : LiveList<E> {

    private val arrayList = ArrayList<E>()

    private val map = HashMap<LiveList.LiveListObserver<E>,
            LiveObserverWrapper<LiveList.LiveListObserver<E>>>()


    override fun observe(lifecycleOwner: LifecycleOwner, observer: LiveList.LiveListObserver<E>) {
        val observerWrapper = LifecycleObserverWrapper(lifecycleOwner, observer)
        observerWrapper.subscribe {
            removeObserver(observer)
        }

        map[observer] = observerWrapper
    }

    override fun observeForever(observer: LiveList.LiveListObserver<E>) {
        val observerWrapper = PermanentObserverWrapper(observer)
        observerWrapper.subscribe {
            removeObserver(observer)
        }

        map[observer] = observerWrapper
    }

    override fun removeObserver(observer: LiveList.LiveListObserver<E>) {
        map.remove(observer)?.unsubscribe()
    }

    override fun removeAllObservers(lifecycleOwner: LifecycleOwner) {
        map.entries.extendedRemoveIf({
            val entryValue = it.value
            entryValue is LifecycleObserverWrapper && entryValue.lifecycleOwner == lifecycleOwner
        }, {
            it.value.unsubscribe()
        })
    }

    private fun forEachObserver(foo: (observer: LiveList.LiveListObserver<E>) -> Unit) {
        map.forEach { entry ->
            foo(entry.key)
        }
    }

    private fun clearImpl() {
        arrayList.clear()
    }

    private fun addImpl(element: E): Boolean {
        return arrayList.add(element)
    }

    private fun addImpl(index: Int, element: E) {
        return arrayList.add(index, element)
    }

    private fun addAllImpl(collection: Collection<E>): Boolean {
        return arrayList.addAll(collection)
    }

    private fun addAllImpl(index: Int, collection: Collection<E>): Boolean {
        return arrayList.addAll(index, collection)
    }

    override fun setAll(collection: Collection<E>) {

        val prev = mutableListOf<E>().apply { addAll(arrayList) }

        clearImpl()
        addAllImpl(collection)

        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return prev[oldItemPosition] == get(newItemPosition)
            }

            override fun getOldListSize(): Int {
                return prev.size
            }

            override fun getNewListSize(): Int {
                return size
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return areItemsTheSame(oldItemPosition, newItemPosition)
            }
        })

        diffResult.dispatchUpdatesTo(object : ListUpdateCallback {
            override fun onChanged(position: Int, count: Int, payload: Any?) {
                val map = mutableMapOf<Int, E>()
                for (i in position until position + count) {
                    map[i] = get(i)
                }

                forEachObserver {
                    it.onItemsReplaced(prev, map)
                }
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {

                forEachObserver {
                    it.onItemsReplaced(prev, mapOf(fromPosition to prev[toPosition]))
                }
            }

            override fun onInserted(position: Int, count: Int) {
                forEachObserver {
                    it.onItemsInserted(prev, subList(position, position + count), position)
                }
            }

            override fun onRemoved(position: Int, count: Int) {
                val list = mutableListOf<Int>()
                for (i in position until position + count) {
                    list.add(i)
                }
                forEachObserver {
                    it.onItemsRemoved(prev, list)
                }
            }
        })

//        forEachObserver {
//            it.onItemsReplaced(prev, collection.indexedAssociateBy { i, _ -> i })
//        }
    }

    override fun addAllNew(collection: Collection<E>) {
        val s = size
        val previousCopy = mutableListOf<E>().apply { addAll(this) }

        val insertedItems = mutableListOf<E>()

        collection.forEach {
            if (!contains(it)) {
                addImpl(it)
                insertedItems.add(it)
            }
        }

        if (s < size) {
            forEachObserver {
                it.onItemsInserted(previousCopy, insertedItems, s)
            }
        }

    }

    override fun contains(element: E): Boolean {
        return arrayList.contains(element)
    }

    override fun containsAll(elements: Collection<E>): Boolean {
        return arrayList.containsAll(elements)
    }

    override operator fun get(index: Int): E {
        return arrayList[index]
    }

    override fun indexOf(element: E): Int {
        return arrayList.indexOf(element)
    }

    override fun isEmpty(): Boolean {
        return arrayList.isEmpty()
    }

    override fun iterator(): MutableIterator<E> {
        return arrayList.iterator()
    }

    override fun lastIndexOf(element: E): Int {
        return arrayList.lastIndexOf(element)
    }

    override fun add(element: E): Boolean {
        val s = size
        val previousCopy = arrayListOf<E>().apply { addAll(arrayList) }
        val res = addImpl(element)
        if (res) {
            forEachObserver {
                it.onItemsInserted(previousCopy, listOf(element), s)
            }
        }
        return res
    }

    override fun add(index: Int, element: E) {
        val previousCopy = arrayListOf<E>().apply { addAll(arrayList) }
        addImpl(index, element)
        forEachObserver {
            it.onItemsInserted(previousCopy, listOf(element), index)
        }
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        val previousCopy = arrayListOf<E>().apply { addAll(arrayList) }
        val res = addAllImpl(index, elements)
        if (res) {
            forEachObserver {
                it.onItemsInserted(previousCopy, elements.toList(), index)
            }
        }
        return res
    }

    override fun addAll(elements: Collection<E>): Boolean {
        val s = size
        val previousCopy = arrayListOf<E>().apply { addAll(arrayList) }
        val res = addAllImpl(elements)
        if (res) {
            forEachObserver {
                it.onItemsInserted(previousCopy, elements.toList(), s)
            }
        }
        return res
    }

    override fun clear() {
        if (isEmpty())
            return

        val previousCopy = arrayListOf<E>().apply { addAll(arrayList) }
        clearImpl()
        forEachObserver {
            it.onItemsRemoved(previousCopy, previousCopy.mapIndexed { index, _ -> index })
        }
    }

    override fun listIterator(): MutableListIterator<E> {
        return arrayList.listIterator()
    }

    override fun listIterator(index: Int): MutableListIterator<E> {
        return arrayList.listIterator(index)
    }

    override fun remove(element: E): Boolean {
        val previousCopy = arrayListOf<E>().apply { addAll(arrayList) }
        val from = arrayList.indexOf(element)

        if (from < 0)
            return false

        arrayList.removeAt(from)

        forEachObserver {
            it.onItemsRemoved(previousCopy, listOf(from))
        }

        return true
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        val previousCopy = arrayListOf<E>().apply { addAll(arrayList) }
        val removedElements = mutableListOf<Int>()

        elements.forEachIndexed { index, e ->
            if (remove(e)) {
                removedElements.add(index)
            }
        }

        val modified = removedElements.isNotEmpty()

        if (modified) {
            forEachObserver {
                it.onItemsRemoved(previousCopy, removedElements)
            }
        }

        return modified
    }

    override fun removeAll(from: Int, count: Int) {
        if (from < 0 || from >= size)
            return

        val diff = from + count - size

        val to = if (diff > 0) size else from + count

        val removedItems = mutableListOf<Int>()

        for (i in from until to) {
            arrayList.removeAt(from)
            removedItems.add(i)
        }

        if (removedItems.isNotEmpty()) {
            forEachObserver {
                it.onItemsRemoved(this, removedItems)
            }
        }
    }

    override fun removeAt(index: Int): E {
        val previousCopy = arrayListOf<E>().apply { addAll(arrayList) }
        val element = arrayList.removeAt(index)

        forEachObserver {
            it.onItemsRemoved(previousCopy, listOf(index))
        }

        return element
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        val previousList = mutableListOf<E>().apply { addAll(arrayList) }
        val removedItems = mutableListOf<Int>()
        val each: MutableIterator<E> = arrayList.iterator()
        var i = 0
        while (each.hasNext()) {
            val element = each.next()
            if (!elements.contains(element)) {
                each.remove()
                removedItems.add(i)
            }
            ++i
        }

        if (removedItems.isNotEmpty()) {
            forEachObserver {
                it.onItemsRemoved(previousList, removedItems)
            }
        }

        return removedItems.isNotEmpty()
    }

    override operator fun set(index: Int, element: E): E {
        val previousList = mutableListOf<E>().apply { addAll(arrayList) }
        val previousElement = arrayList.set(index, element)
        if (previousElement != element) {
            forEachObserver {
                it.onItemsReplaced(previousList, mapOf(index to element))
            }
        }
        return previousElement
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> {
        return arrayList.subList(fromIndex, toIndex)
    }

    override val size: Int
        get() = arrayList.size

    override fun replaceAll(map: Map<E, E>) {
        val previousList = mutableListOf<E>().also { it.addAll(arrayList) }
        val replacedItems = mutableMapOf<Int, E>()
        for (entry in map) {
            if (entry.key == entry.value)
                continue

            val pos = indexOf(entry.key)

            if (pos < 0)
                continue

            replacedItems[pos] = entry.key
            arrayList[pos] = entry.value
        }

        if (replacedItems.isNotEmpty()) {
            forEachObserver {
                it.onItemsReplaced(previousList, replacedItems)
            }
        }

    }

}