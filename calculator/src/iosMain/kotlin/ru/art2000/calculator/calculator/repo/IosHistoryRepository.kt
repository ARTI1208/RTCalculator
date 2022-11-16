package ru.art2000.calculator.calculator.repo

import kotlinx.coroutines.flow.flow
import ru.art2000.calculator.calculator.model.HistoryContentItem
import ru.art2000.calculator.calculator.model.HistoryListItem
import ru.art2000.calculator.calculator.model.HistoryValueItem

internal class IosHistoryRepository : HistoryRepository {

    override fun getAll() = flow<List<HistoryListItem>> { emit(emptyList()) }

    override suspend fun add(item: HistoryContentItem) {

    }

    override suspend fun update(item: HistoryValueItem) {

    }

    override suspend fun remove(item: HistoryValueItem) {

    }

    override suspend fun clear() {

    }
}