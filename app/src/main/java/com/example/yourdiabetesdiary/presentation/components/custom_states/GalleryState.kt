package com.example.yourdiabetesdiary.presentation.components.custom_states

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.yourdiabetesdiary.models.GalleryItem
import kotlin.random.Random

class GalleryState {
    private val _images = mutableListOf<GalleryItem>()
    private val _imagesForDeletion = mutableListOf<GalleryItem>()

    val images: List<GalleryItem>
        get() = _images.toList()

    val imagesForDeletion: List<GalleryItem>
        get() = _imagesForDeletion.toList()

    fun addImage(item: GalleryItem) {
        _images.add(item)
    }

    fun removeInTwoStorages(item: GalleryItem) {
        _images.remove(item)
        _imagesForDeletion.add(item)
    }

    fun clearAllSelectedImagesForDeletion() {
        _imagesForDeletion.clear()
    }



    override fun toString(): String {
        return "GalleryState(_images=$_images, _imagesForDeletion=$_imagesForDeletion, images=$images, imagesForDeletion=$imagesForDeletion)"
    }

    companion object {
        fun setupImagesBasedOnPrevious(prevGalleryState: GalleryState): GalleryState {
            return GalleryState().apply {
                _images.addAll(prevGalleryState.images)
                _imagesForDeletion.addAll(prevGalleryState.imagesForDeletion)
            }
        }
    }
}

@Composable
fun rememberGalleryState() = remember {
    Log.d("TEST_IMAGE_SELECTION", "remember")
    mutableStateOf(GalleryState())
}