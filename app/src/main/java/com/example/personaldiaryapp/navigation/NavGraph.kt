package com.example.personaldiaryapp.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.akka.auth.navigation.authenticationRoute
import com.akka.home.navigation.homeRoute
import com.akka.util.Screen
import com.akka.write.navigation.writeRoute

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetupNavGraph(
    startDestination: String,
    navController: NavHostController,
    onDataLoaded: () -> Unit
){
    NavHost(
        startDestination = startDestination,
        navController = navController
    ) {
        authenticationRoute(
            navigateToHome = {
                navController.popBackStack()
                navController.navigate(Screen.Home.route)
            },
            onDataLoaded = onDataLoaded
        )
        homeRoute(
            navigateToWrite = {
                navController.navigate(Screen.Write.route)
            },
            navigateToWriteWithArgs = {
                                      navController.navigate(Screen.Write.passDiaryId(diaryId = it))
            },
            navigateToAuth = {
                navController.popBackStack()
                navController.navigate(Screen.Authentication.route)
            },
            onDataLoaded = onDataLoaded
        )
        writeRoute(
            onBackPressed = {
            navController.popBackStack()
            }
        )
    }

}







