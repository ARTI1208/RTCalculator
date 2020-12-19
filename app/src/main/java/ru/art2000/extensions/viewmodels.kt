package ru.art2000.extensions

import android.content.Context
import androidx.lifecycle.AndroidViewModel

val AndroidViewModel.context: Context get() = getApplication()