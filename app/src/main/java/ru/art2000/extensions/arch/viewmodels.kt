package ru.art2000.extensions.arch

import android.content.Context
import androidx.lifecycle.AndroidViewModel

val AndroidViewModel.context: Context get() = getApplication()