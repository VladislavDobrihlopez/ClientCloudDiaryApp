package com.example.util.connectivity

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    fun observe(): Flow<Status>

    enum class Status {
        AVAILABLE, UNAVAILABLE, LOST, LOSING
    }
}