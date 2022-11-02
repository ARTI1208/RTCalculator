package ru.art2000.calculator.model.calculator.history

import ru.art2000.extensions.collections.DiffComparable

sealed interface HistoryListItem : DiffComparable<HistoryListItem>