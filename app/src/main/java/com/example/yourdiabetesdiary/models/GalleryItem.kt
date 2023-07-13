package com.example.yourdiabetesdiary.models

import android.net.Uri

data class GalleryItem(
    val remotePath: String = NO_PATH,
    val localUri: Uri
) {
    companion object {
        const val NO_PATH = ""
    }
}