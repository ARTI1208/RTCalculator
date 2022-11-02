package ru.art2000.extensions.collections

import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import java.util.Comparator
import java.util.function.UnaryOperator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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

    private fun forEachObserverAndAny(
        previousList: List<E>,
        foo: (observer: LiveList.LiveListObserver<E>) -> Unit
    ) {
        map.forEach { entry ->
            foo(entry.key)
            entry.key.onAnyChanged(previousList, this)
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

        val previousList = snapshot()

        clearImpl()
        addAllImpl(collection)

        val addedIndices = mutableListOf<Int>()
        val removedIndices = mutableListOf<Int>()
        val changedIndices = mutableListOf<Int>()

//        repeat(min(previousList.size, size)) {
//            if ()
//        }


        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return previousList[oldItemPosition] == get(newItemPosition)
            }

            override fun getOldListSize(): Int {
                return previousList.size
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
                // TODO when this called?
                changedIndices += List(count) { position + it }
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
                // TODO when this called?
                changedIndices += fromPosition
            }

            override fun onInserted(position: Int, count: Int) {
                for (i in addedIndices.indices) {
                    if (addedIndices[i] >= position) addedIndices[i] += count
                }
                addedIndices += List(count) { position + it }
            }

            override fun onRemoved(position: Int, count: Int) {
                for (i in addedIndices.indices) {
                    if (addedIndices[i] >= position) addedIndices[i] -= count
                }
                removedIndices += List(count) { position + it }
            }
        })

        if (addedIndices.isNotEmpty()) {
            addedIndices.sort()
            forEachObserver {
                it.onItemsInserted(previousList, this, addedIndices)
            }
        }
        if (removedIndices.isNotEmpty()) {
            removedIndices.sort()
            forEachObserver {
                it.onItemsRemoved(previousList, this, removedIndices)
            }
        }
        if (changedIndices.isNotEmpty()) {
            changedIndices.sort()
            forEachObserver {
                it.onItemsReplaced(previousList, this, changedIndices)
            }
        }

        val anyChanged = addedIndices.isNotEmpty() || removedIndices.isNotEmpty()
                || changedIndices.isNotEmpty()

        if (anyChanged) {
            forEachObserver {
                it.onAnyChanged(previousList, this)
            }
        }
    }

    override fun addAllNew(collection: Collection<E>) {
        val s = size
        val previousList = snapshot()

        val insertedItems = mutableListOf<Int>()

        collection.forEach {
            if (!contains(it)) {
                addImpl(it)
                insertedItems += lastIndex
            }
        }

        if (s < size) {
            forEachObserverAndAny(previousList) {
                it.onItemsInserted(previousList, this, insertedItems)
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

    // TODO compare if values really changed?
    override fun replaceAll(operator: UnaryOperator<E>) {
        val previousList = snapshot()
        super.replaceAll(operator)
        forEachObserverAndAny(previousList) { observer ->
            observer.onItemsReplaced(previousList, this, List(size) { it })
        }
    }

    // TODO compare if values really changed?
    override fun sort(c: Comparator<in E>?) {
        val previousList = snapshot()
        super.sort(c)
        forEachObserverAndAny(previousList) { observer ->
            observer.onItemsReplaced(previousList, this, List(size) { it })
        }
    }

    override fun iterator(): MutableIterator<E> {
        return arrayList.iterator()
    }

    override fun lastIndexOf(element: E): Int {
        return arrayList.lastIndexOf(element)
    }

    override fun add(element: E): Boolean {
        val previousList = snapshot()
        val res = addImpl(element)
        if (res) {
            forEachObserverAndAny(previousList) {
                it.onItemsInserted(previousList, this, listOf(lastIndex))
            }
        }
        return res
    }

    override fun add(index: Int, element: E) {
        val previousList = snapshot()
        addImpl(index, element)
        forEachObserverAndAny(previousList) {
            it.onItemsInserted(previousList, this, listOf(index))
        }
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        val previousList = snapshot()
        val res = addAllImpl(index, elements)
        if (res) {
            forEachObserverAndAny(previousList) { observer ->
                observer.onItemsInserted(previousList, this, List(elements.size) { index + it })
            }
        }
        return res
    }

    override fun addAll(elements: Collection<E>): Boolean {
        val s = size
        val previousList = snapshot()
        val res = addAllImpl(elements)
        if (res) {
            
            forEachObserverAndAny(previousList) { observer ->
                observer.onItemsInserted(previousList, this, List(elements.size) { s + it })
            }
        }
        return res
    }

    override fun clear() {
        if (isEmpty()) return

        val previousList = snapshot()
        clearImpl()
        forEachObserverAndAny(previousList) {
            it.onItemsRemoved(previousList, this, List(previousList.size) { index -> index })
        }
    }

    override fun listIterator(): MutableListIterator<E> {
        return arrayList.listIterator()
    }

    override fun listIterator(index: Int): MutableListIterator<E> {
        return arrayList.listIterator(index)
    }

    override fun remove(element: E): Boolean {
        val previousList = snapshot()
        val from = arrayList.indexOf(element)

        if (from < 0)
            return false

        arrayList.removeAt(from)

        forEachObserverAndAny(previousList) {
            it.onItemsRemoved(previousList, this, listOf(from))
        }

        return true
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        val previousList = snapshot()
        val removedIndices = mutableListOf<Int>()

        fun List<E>.indexOf(e: E, fromIndex: Int = 0): Int {
            for (i in fromIndex until size) {
                if (this[i] == e) return i
            }
            return -1
        }

        elements.forEach { e ->
            var startFrom = 0
            do {
                val pos = arrayList.indexOf(e, startFrom)
                if (pos < 0) return@forEach
                removedIndices += pos
                startFrom = pos + 1
            } while (true)
        }

        removedIndices.sort()
        repeat(removedIndices.size) {
            arrayList.removeAt(removedIndices[it] - it)
        }

        val modified = removedIndices.isNotEmpty()

        if (modified) {
            forEachObserverAndAny(previousList) {
                it.onItemsRemoved(previousList, this, removedIndices)
            }
        }

        return modified
    }

    override fun removeAll(from: Int, count: Int) {
        if (from < 0 || from >= size)
            return

        val diff = from + count - size

        val to = if (diff > 0) size else from + count

        val previousList = snapshot()
        val removedIndices = mutableListOf<Int>()

        for (i in from until to) {
            arrayList.removeAt(from)
            removedIndices.add(i)
        }

        if (removedIndices.isNotEmpty()) {
            forEachObserverAndAny(previousList) {
                it.onItemsRemoved(previousList, this, removedIndices)
            }
        }
    }

    override fun removeAt(index: Int): E {
        val previousList = snapshot()
        val element = arrayList.removeAt(index)

        forEachObserverAndAny(previousList) {
            it.onItemsRemoved(previousList, this, listOf(index))
        }

        return element
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        val previousList = snapshot()
        val removedIndices = mutableListOf<Int>()
        val each: MutableIterator<E> = arrayList.iterator()
        var i = 0
        while (each.hasNext()) {
            val element = each.next()
            if (!elements.contains(element)) {
                each.remove()
                removedIndices.add(i)
            }
            ++i
        }

        if (removedIndices.isNotEmpty()) {
            forEachObserverAndAny(previousList) {
                it.onItemsRemoved(previousList, this, removedIndices)
            }
        }

        return removedIndices.isNotEmpty()
    }

    override operator fun set(index: Int, element: E): E {
        val previousList = snapshot()
        val previousElement = arrayList.set(index, element)
        if (previousElement != element) {
            forEachObserverAndAny(previousList) {
                it.onItemsReplaced(previousList, this, listOf(index))
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
        val previousList = snapshot()
        val replacedItems = mutableListOf<Int>()
        for (entry in map) {
            if (entry.key == entry.value)
                continue

            var lastPos = -1

            do {
                val pos = previousList.indexOf(entry.key, lastPos)
                if (pos < 0) break
                lastPos = pos + 1

                replacedItems += pos
                arrayList[pos] = entry.value

            } while (true)
        }

        if (replacedItems.isNotEmpty()) {
            forEachObserverAndAny(previousList) {
                it.onItemsReplaced(previousList, this, replacedItems)
            }
        }

    }

}