package com.example.composition.navigation

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.composition.CompositionViewModel
import com.example.util.Screen
import com.example.util.getImageType
import com.example.util.models.Mood
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import io.realm.kotlin.ext.toRealmList
import org.mongodb.kbson.ObjectId

@OptIn(ExperimentalPagerApi::class)
fun NavGraphBuilder.diaryRoute(navigateBack: () -> Unit) {
    composable(
        route = Screen.DiaryEntry.route, arguments = listOf(navArgument(
            name = Screen.DiaryEntry.DIARY_ID_ARGUMENT_KEY,
        ) {
            type = NavType.StringType
            nullable = true
            defaultValue = null
        })
    ) {
        //val diaryEntryId = it.arguments?.getString(Screen.DiaryEntry.DIARY_ID_ARGUMENT_KEY)

        val context = LocalContext.current
        val viewModel: CompositionViewModel = hiltViewModel()
        val pagerState = rememberPagerState()
        val entry = viewModel.uiState.value

        Log.d("NEW_STATE", "${entry.date}, ${entry.mood}")

        LaunchedEffect(key1 = entry.mood) {
            pagerState.animateScrollToPage(Mood.valueOf(entry.mood.name).ordinal)
        }

        val currentPage = remember {
            derivedStateOf { pagerState.currentPage }
        }

        val galleryState = viewModel.galleryState

        Log.d("TEST_IMAGE_SELECTION", "setupNavHost: ${galleryState.value}")

        com.example.composition.CompositionScreen(
            date = entry.date,
            mood = { Mood.values()[currentPage.value].name },
            pagerState = pagerState,
            screenState = entry,
            navigateBack = navigateBack,
            galleryState = galleryState,
            onImageSelected = { uri ->
                Log.d("TEST_IMAGE_SELECTION", "$uri")
                val type = context.getImageType(uri)
                viewModel.addImage(uri, type)
            },
            onDescriptionChanged = { desc ->
                viewModel.setNewDescription(desc)
            },
            onTitleChanged = { title ->
                viewModel.setNewTitle(title)
            },
            onDeleteConfirmed = {
                viewModel.deleteDiary(onSuccess = {
                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                    navigateBack()
                }, onFailure = { error ->
                    Toast.makeText(context, "Error occurred: $error", Toast.LENGTH_SHORT).show()
                })
            },
            onSaveDiaryButtonClicked = { diary ->
                viewModel.storeDiary(
                    diary = diary.apply {
                        this.mood = Mood.values()[currentPage.value].name
                        this._id =
                            if (entry.selectedDiaryEntryId != null) {
                                ObjectId(entry.selectedDiaryEntryId!!)
                            } else {
                                ObjectId.invoke()
                            }
                        this.images =
                            viewModel.galleryState.value.images.map { it.remotePath }.toRealmList()
                    },
                    onSuccess = {
                        Log.d("TEST_STORING", "navigationing back")
                        navigateBack()
                    },
                    onFailure = { error ->
                        Toast.makeText(context, "Error occurred: $error", Toast.LENGTH_SHORT)
                            .show()
                        navigateBack()
                    }
                )
            },
            onDateUpdated = { zonedDateTime ->
                Log.d("ON_DATE_UPDATED", zonedDateTime.toString())
                viewModel.setNewDateAndTime(zonedDateTime = zonedDateTime)
            },
            onDeleteGalleryImage = { galleryItem ->
                viewModel.queueImageForDeletion(galleryItem)
            }
        )
    }
}