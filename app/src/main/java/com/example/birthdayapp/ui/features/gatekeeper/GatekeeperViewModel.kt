package com.example.birthdayapp.ui.features.gatekeeper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class GatekeeperViewModel : ViewModel() {

    // Target Birthday: Set this to the actual date
    private val targetDate = LocalDate.of(2025, 12, 25) // Example date

    private val _uiState = MutableStateFlow<GatekeeperUiState>(GatekeeperUiState.Loading)
    val uiState: StateFlow<GatekeeperUiState> = _uiState.asStateFlow()

    init {
        startCountdown()
    }

    private fun startCountdown() {
        viewModelScope.launch {
            while (true) {
                val now = LocalDateTime.now()
                val targetDateTime = LocalDateTime.of(targetDate, LocalTime.MIDNIGHT)

                if (now.isAfter(targetDateTime) || now.isEqual(targetDateTime)) {
                    _uiState.value = GatekeeperUiState.Unlocked
                    break
                } else {
                    val duration = Duration.between(now, targetDateTime)
                    _uiState.value = GatekeeperUiState.Locked(
                        days = duration.toDays(),
                        hours = duration.toHours() % 24,
                        minutes = duration.toMinutes() % 60,
                        seconds = duration.getSeconds() % 60
                    )
                }
                delay(1000)
            }
        }
    }
}

sealed class GatekeeperUiState {
    object Loading : GatekeeperUiState()
    data class Locked(val days: Long, val hours: Long, val minutes: Long, val seconds: Long) : GatekeeperUiState()
    object Unlocked : GatekeeperUiState()
}