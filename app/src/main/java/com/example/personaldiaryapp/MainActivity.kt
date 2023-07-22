package com.example.personaldiaryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.example.personaldiaryapp.data.repository.MongoDB
import com.example.personaldiaryapp.navigation.Screen
import com.example.personaldiaryapp.navigation.SetupNavGraph
import com.example.personaldiaryapp.ui.theme.PersonalDiaryAppTheme
import com.example.personaldiaryapp.util.Constants.APP_ID
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import io.realm.kotlin.mongodb.App

class MainActivity : ComponentActivity() {

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
    }
}

private fun getStartDestination(): String{
    val user = App.Companion.create(APP_ID).currentUser
    return if(user != null && user.loggedIn)
                Screen.Home.route
    else Screen.Authentication.route
}