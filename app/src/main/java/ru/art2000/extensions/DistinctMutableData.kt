package ru.art2000.extensions

import androidx.lifecycle.MutableLiveData

class DistinctMutableData<T>(initialValue: T) : MutableLiveData<T>(initialValue) {

    override fun setValue(value: T) {
        if (value != this.value) {
            super.setValue(value)
        }
    }
}