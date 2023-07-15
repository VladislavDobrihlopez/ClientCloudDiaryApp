package com.example.yourdiabetesdiary.util

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.example.yourdiabetesdiary.data.database.models.ImagesForDeletionDbModel
import com.example.yourdiabetesdiary.data.database.models.ImagesForUploadingDbModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storageMetadata
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

fun retrieveImagesFromFirebaseStorage(
    imagesUrls: List<String>,
    onCompletedDownloadingItem: (Uri) -> Unit,
    onFailure: (Exception) -> Unit = {},
    onWholeWorkCompleted: () -> Unit = {}
) {
    val storageReference = FirebaseStorage.getInstance().reference
    imagesUrls.map { it.trim() }.forEachIndexed { index, remoteFirebasePath ->
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

fun retryUploadingImage(
    image: ImagesForUploadingDbModel,
    whetherSuccessfullyCompleted: (Boolean) -> Unit
) {
    val reference = FirebaseStorage.getInstance().reference
    reference.child(image.remotePath)
        .putFile(Uri.parse(image.localUri), storageMetadata { }, image.sessionUri.toUri())
        .addOnSuccessListener {
            whetherSuccessfullyCompleted(true)
        }
        .addOnFailureListener {
            whetherSuccessfullyCompleted(false)
        }
}

fun retryDeletingImage(
    image: ImagesForDeletionDbModel,
    whetherSuccessfullyCompleted: (Boolean) -> Unit
) {
    val reference = FirebaseStorage.getInstance().reference
    reference.child(image.remotePath)
        .delete()
        .addOnSuccessListener {
            whetherSuccessfullyCompleted(true)
        }
        .addOnFailureListener {
            whetherSuccessfullyCompleted(false)
        }
}