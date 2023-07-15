package com.example.yourdiabetesdiary.domain

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    fun observe(): Flow<Status>

    enum class Status {
        AVAILABLE, UNAVAILABLE, LOST, LOSING
    }
}