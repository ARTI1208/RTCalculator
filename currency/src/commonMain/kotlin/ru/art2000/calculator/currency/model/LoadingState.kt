package ru.art2000.calculator.currency.model

internal enum class LoadingState(val finishesLoading: Boolean) {

    UNINITIALISED(false),
    LOADING_STARTED(false),
    LOADING_ENDED(true),
    UNKNOWN_ERROR(true),
    NETWORK_ERROR(true)

}