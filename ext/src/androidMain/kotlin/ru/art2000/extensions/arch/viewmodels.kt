package ru.art2000.extensions.arch

import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application

val AndroidViewModel.context: Context get() = application