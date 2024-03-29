package ru.art2000.extensions.arch

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

val AndroidViewModel.context: Context get() = getApplication()

// TODO refactor once Hilt support @AssistedInject constructor for @HiltViewModel
// https://github.com/google/dagger/issues/2287
inline fun <reified VM : ViewModel> Fragment.assistedViewModel(
    crossinline viewModelProducer: (SavedStateHandle) -> VM,
) = viewModels<VM> {
    object : AbstractSavedStateViewModelFactory(this@assistedViewModel, arguments) {
        @Suppress("UNCHECKED_CAST")
        override fun <VM : ViewModel> create(key: String, modelClass: Class<VM>, handle: SavedStateHandle) =
            viewModelProducer(handle) as VM
    }
}

inline fun <reified T : ViewModel> ComponentActivity.assistedViewModel(
    crossinline viewModelProducer: (SavedStateHandle) -> T,
) = viewModels<T> {
    object : AbstractSavedStateViewModelFactory(this@assistedViewModel, intent.extras) {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle) =
            viewModelProducer(handle) as T
    }
}