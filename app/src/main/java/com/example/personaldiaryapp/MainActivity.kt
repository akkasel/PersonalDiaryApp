package com.example.personaldiaryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.personaldiaryapp.data.database.entity.ImageToUploadDao
import com.example.personaldiaryapp.navigation.Screen
import com.example.personaldiaryapp.navigation.SetupNavGraph
import com.example.personaldiaryapp.ui.theme.PersonalDiaryAppTheme
import com.example.personaldiaryapp.util.Constants.APP_ID
import com.example.personaldiaryapp.util.retryUploadingImageToFirebase
import com.google.firebase.FirebaseApp
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var imageToUploadDao: ImageToUploadDao
    var keepSplashOpened = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // show splash screen
        installSplashScreen().setKeepOnScreenCondition(){
            keepSplashOpened
        }

        FirebaseApp.initializeApp(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            PersonalDiaryAppTheme(dynamicColor = false) {
                val navController = rememberNavController()
                SetupNavGraph(
                    startDestination = getStartDestination(),
                    navController = navController,
                    onDataLoaded = {
                        keepSplashOpened = false
                    }
                )
            }
        }

        cleanupCheck(scope = lifecycleScope, imageToUploadDao = imageToUploadDao)
    }
}

private fun cleanupCheck(
    scope: CoroutineScope,
    imageToUploadDao: ImageToUploadDao
){
    scope.launch(Dispatchers.IO) {
        val result = imageToUploadDao.getAllImages()
        result.forEach {imageToUpload ->
            retryUploadingImageToFirebase(
                imageToUpload = imageToUpload,
                onSuccess = {
                    scope.launch(Dispatchers.IO){
                        imageToUploadDao.cleanupImage(imageId = imageToUpload.id)
                    }
                }
            )
        }
    }
}

private fun getStartDestination(): String{
    val user = App.Companion.create(APP_ID).currentUser
    return if(user != null && user.loggedIn)
                Screen.Home.route
    else Screen.Authentication.route
}