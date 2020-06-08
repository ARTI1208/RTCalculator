package ru.art2000.calculator.currency_converter.model

enum class LoadingState {

    UNINITIALISED,
    LOADING_STARTED,
    LOADING_ENDED,
    UNKNOWN_ERROR,
    NETWORK_ERROR

}