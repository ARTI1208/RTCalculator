package ru.art2000.calculator.calculator.model

import ru.art2000.extensions.collections.DiffComparable

sealed interface HistoryListItem : DiffComparable<HistoryListItem>