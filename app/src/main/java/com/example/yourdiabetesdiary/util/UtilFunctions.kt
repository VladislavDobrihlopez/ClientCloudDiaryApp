package com.example.yourdiabetesdiary.util

import android.content.Context
import android.net.Uri
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

//official documentation function
fun Instant.toRealmInstant(): RealmInstant {
    val sec: Long = this.epochSecond
    val nano: Int = this.nano
    return if (sec >= 0) {
        RealmInstant.from(sec, nano)
    } else {
        RealmInstant.from(sec + 1, -1_000_000 + nano)
    }
}

fun Context.getImageType(uri: Uri): String {
    return contentResolver.getType(uri)?.split("/")?.last() ?: "jpg"
}