package com.example.yourdiabetesdiary.util

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.ui.text.toLowerCase
import com.google.firebase.storage.FirebaseStorage
import io.realm.kotlin.types.RealmInstant
import java.lang.IllegalStateException
import java.time.Instant
import java.util.Locale

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

fun retrieveImagesFromFirebaseStorage(
    imagesUrls: List<String>,
    onCompletedDownloadingItem: (Uri) -> Unit,
    onFailure: (Exception) -> Unit = {},
    onWholeWorkCompleted: () -> Unit = {}
) {
    val storageReference = FirebaseStorage.getInstance().reference
    imagesUrls.map { it.trim()}.forEachIndexed { index, remoteFirebasePath ->
        if (remoteFirebasePath.isEmpty()) {
            onFailure(IllegalStateException("Url is somehow empty"))
        }
        storageReference.child(remoteFirebasePath).downloadUrl
            .addOnSuccessListener { firebaseUrl ->
                onCompletedDownloadingItem(firebaseUrl)
                if (index == imagesUrls.lastIndex) {
                    onWholeWorkCompleted()
                }
            }
            .addOnFailureListener(onFailure)
    }
}