package com.example.yourdiabetesdiary

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.realm_atlas.database.models.ImagesForDeletionDbModel
import com.example.realm_atlas.database.models.ImagesForUploadingDbModel
import com.example.ui.theme.YourDiabetesDiaryTheme
import com.example.util.Constants
import com.example.util.Screen
import com.example.yourdiabetesdiary.navigation.SetupNavHost
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storageMetadata
import dagger.hilt.android.AndroidEntryPoint
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var imagesUploadingDao: com.example.realm_atlas.database.dao.ImageInQueryForUploadingDao
    @Inject
    lateinit var imagesDeletionDao: com.example.realm_atlas.database.dao.ImageInQueryForDeletionDao

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var keepDisplayingSplash = true

        installSplashScreen().setKeepOnScreenCondition {
            keepDisplayingSplash
        }

        Firebase.initialize(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            YourDiabetesDiaryTheme(dynamicColor = false) {
                val navController = rememberNavController()

                SetupNavHost(
                    navHostController = navController,
                    startDestination = getStartScreenDestination(),
                    keepSplashScreen = { shouldKeep ->
                        keepDisplayingSplash = shouldKeep
                    },
                )
            }
        }
        managePendingImages(
            scope = lifecycleScope,
            pendingImageDao = imagesUploadingDao,
            pendingImageDeletionDao = imagesDeletionDao
        )
    }

    private fun managePendingImages(
        scope: CoroutineScope,
        pendingImageDao: com.example.realm_atlas.database.dao.ImageInQueryForUploadingDao,
        pendingImageDeletionDao: com.example.realm_atlas.database.dao.ImageInQueryForDeletionDao
    ) {
        scope.launch(Dispatchers.IO) {
            launch {
                val allImagesForUploading = pendingImageDao.getAllImages()
                allImagesForUploading.forEach { image ->
                    retryUploadingImage(
                        image = image,
                        whetherSuccessfullyCompleted = { isSuccessfully ->
                            if (isSuccessfully) {
                                scope.launch(Dispatchers.IO) {
                                    pendingImageDao.clearAll(imageId = image.id)
                                }
                            }
                        }
                    )
                }
            }

            launch {
                val allImagesForDeletion = pendingImageDeletionDao.getAllImages()
                allImagesForDeletion.forEach { image ->
                    retryDeletingImage(
                        image = image,
                        whetherSuccessfullyCompleted = { isSuccessfully ->
                            if (isSuccessfully) {
                                scope.launch(Dispatchers.IO) {
                                    pendingImageDeletionDao.clearAll(imageId = image.id)
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    private fun getStartScreenDestination(): String {
        val user = App.Companion.create(Constants.MONGO_DB_APP_ID).currentUser
        Log.d("TEST_USER", user.toString())
        return if (user == null || !user.loggedIn) Screen.Authentication.route else Screen.Home.route
    }

    private fun retryUploadingImage(
        image: ImagesForUploadingDbModel,
        whetherSuccessfullyCompleted: (Boolean) -> Unit
    ) {
        val reference = FirebaseStorage.getInstance().reference
        reference.child(image.remotePath)
            // firebase documentation
            .putFile(
                image.localUri.toUri(),
                storageMetadata { },
                image.sessionUri.toUri()
            )
            .addOnSuccessListener {
                whetherSuccessfullyCompleted(true)
            }
            .addOnFailureListener {
                whetherSuccessfullyCompleted(false)
            }
    }

    private fun retryDeletingImage(
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
}
