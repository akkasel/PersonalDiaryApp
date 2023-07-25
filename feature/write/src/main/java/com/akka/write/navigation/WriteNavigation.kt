package com.akka.write.navigation

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.akka.util.Constants
import com.akka.util.Screen
import com.akka.util.model.Mood
import com.akka.write.WriteScreen
import com.akka.write.WriteViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalPagerApi::class)
fun NavGraphBuilder.writeRoute(onBackPressed: () -> Unit){

    composable(
        route = Screen.Write.route,
        arguments = listOf(navArgument(name = Constants.WRITE_SCREEN_ARGUMENT_KEY){
            type = NavType.StringType
            nullable = true
            defaultValue = null
        })
    ){
        val viewModel: WriteViewModel = hiltViewModel()
        val uiState = viewModel.uiState
        val context = LocalContext.current
        val galleryState = viewModel.galleryState
        val pagerState = rememberPagerState()
        val pageNumber by remember{ derivedStateOf { pagerState.currentPage } }

        WriteScreen(
            uiState = uiState,
            moodName = { Mood.values()[pageNumber].name },
            pagerState = pagerState,
            galleryState = galleryState,
            onTitleChanged = {viewModel.setTitle(title = it)},
            onDescriptionChanged = {viewModel.setDescription(description = it)},
            onDeleteConfirmed = {
                viewModel.deleteDiary(
                    onSuccess = {
                        Toast.makeText(
                            context,
                            "Deleted",
                            Toast.LENGTH_SHORT
                        ).show()
                        onBackPressed()
                    },
                    onError = { message ->
                        Toast.makeText(
                            context,
                            message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )},
            onDateTimeUpdated = { viewModel.updateDateTime(zonedDateTime = it)},
            onBackPressed = onBackPressed,
            onSaveClicked = {
                viewModel.upsertDiary(
                    diary = it.apply { mood = Mood.values()[pageNumber].name },
                    onSuccess = {onBackPressed()},
                    onError = { message ->
                        Toast.makeText(
                            context,
                            message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            },
            onImageSelect = {
                val type = context.contentResolver.getType(it)?.split("/")?.last() ?: "jpg"
                viewModel.addImage(image = it, imageType = type)
            },
            onImageDeleteClicked = { galleryState.removeImage(it)}
        )
    }
}