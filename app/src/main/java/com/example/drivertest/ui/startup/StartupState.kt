package com.example.drivertest.ui.startup

sealed interface StartupState {

    data object Loading : StartupState

    data object Success : StartupState

    data class Error(val message: String) : StartupState
}