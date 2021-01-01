package ru.art2000.extensions.collections

import androidx.lifecycle.LifecycleOwner

class LiveHashMap<K, V> : MutableLiveMap<K, V> {

    private val hashMap = HashMap<K, V>()


    private val map = HashMap<LiveMap.LiveMapObserver<K, V>,
            LiveObserverWrapper<LiveMap.LiveMapObserver<K, V>>>()

    override fun observe(lifecycleOwner: LifecycleOwner, observer: LiveMap.LiveMapObserver<K, V>) {
        val observerWrapper = LifecycleObserverWrapper(lifecycleOwner, observer)
        observerWrapper.subscribe {
            removeObserver(observer)
        }

        map[observer] = observerWrapper
    }

    override fun observeForever(observer: LiveMap.LiveMapObserver<K, V>) {
        val observerWrapper = PermanentObserverWrapper(observer)
        observerWrapper.subscribe {
            removeObserver(observer)
        }

        map[observer] = observerWrapper
    }

    override fun removeObserver(observer: LiveMap.LiveMapObserver<K, V>) {
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

    private fun forEachObserver(foo: (observer: LiveMap.LiveMapObserver<K, V>) -> Unit) {
        map.forEach { entry ->
            foo(entry.key)
        }
    }

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = hashMap.entries

    override val keys: MutableSet<K>
        get() = hashMap.keys

    override val size: Int
        get() = hashMap.size

    override val values: MutableCollection<V>
        get() = hashMap.values

    override fun containsKey(key: K): Boolean {
        return hashMap.containsKey(key)
    }

    override fun containsValue(value: V): Boolean {
        return hashMap.containsValue(value)
    }

    override operator fun get(key: K): V? {
        return hashMap[key]
    }

    override fun isEmpty(): Boolean {
        return hashMap.isEmpty()
    }

    override fun clear() {
        val items = entries.toList()
        val map = HashMap<K, V>(hashMap)

        hashMap.clear()

        forEachObserver {
            it.onItemsRemoved(map, items)
        }
    }

    override fun put(key: K, value: V): V? {
        val map = HashMap<K, V>(hashMap)

        val res = hashMap.put(key, value)

        forEachObserver {
            it.onItemsPut(map, listOf(Entry(key, value)))
        }

        return res
    }

    override fun putAll(from: Map<out K, V>) {
        val map = HashMap<K, V>(hashMap)

        hashMap.putAll(from)

        forEachObserver {
            it.onItemsPut(map, from.entries.toList())
        }
    }

    override fun remove(key: K): V? {
        val map = HashMap<K, V>(hashMap)

        val res = hashMap.remove(key)


        if (res != null) {
            forEachObserver {
                it.onItemsRemoved(map, listOf(Entry(key, res)))
            }
        }

        return res
    }


    private inner class Entry(override val key: K, override val value: V) : Map.Entry<K, V>
}