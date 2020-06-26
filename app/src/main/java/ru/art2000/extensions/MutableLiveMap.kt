package ru.art2000.extensions

interface MutableLiveMap<K, V> : LiveMap<K, V>, MutableMap<K, V> {
}