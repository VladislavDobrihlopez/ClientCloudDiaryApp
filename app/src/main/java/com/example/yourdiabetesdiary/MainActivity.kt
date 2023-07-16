package com.example.yourdiabetesdiary

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.yourdiabetesdiary.data.database.dao.ImageInQueryForDeletionDao
import com.example.yourdiabetesdiary.data.database.dao.ImageInQueryForUploadingDao
import com.example.yourdiabetesdiary.navigation.Screen
import com.example.yourdiabetesdiary.navigation.SetupNavHost
import com.example.ui.theme.YourDiabetesDiaryTheme
import com.example.yourdiabetesdiary.util.Constants
import com.example.yourdiabetesdiary.util.retryDeletingImage
import com.example.yourdiabetesdiary.util.retryUploadingImage
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import dagger.hilt.android.AndroidEntryPoint
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var imagesUploadingDao: ImageInQueryForUploadingDao
    @Inject
    lateinit var imagesDeletionDao: ImageInQueryForDeletionDao

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
        pendingImageDao: ImageInQueryForUploadingDao,
        pendingImageDeletionDao: ImageInQueryForDeletionDao
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
}
