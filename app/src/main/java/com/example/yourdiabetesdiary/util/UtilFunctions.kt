package com.example.yourdiabetesdiary.util

import io.realm.kotlin.types.RealmInstant
import java.time.Instant

fun RealmInstant.toInstant(): Instant {
    val nano = nanosecondsOfSecond
    val sec = epochSeconds
    return if (sec >= 0) {
        Instant.ofEpochSecond(sec, nano.toLong())
    } else {
        Instant.ofEpochSecond(sec - 1, 1_000_000 + nano.toLong())
    }
}