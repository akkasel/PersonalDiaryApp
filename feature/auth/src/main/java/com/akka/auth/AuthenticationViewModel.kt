package com.akka.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akka.util.Constants.APP_ID
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

internal class AuthenticationViewModel: ViewModel() {

    var authenticated = mutableStateOf(false)
        private set

    var loadingState = mutableStateOf(false)
        private set

    fun setLoading(loading: Boolean){
        loadingState.value = loading
    }

    fun signInWithMongoAtlas(
        tokenId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ){
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO){
                    // create user account on the mongoDB
                    App.create(APP_ID).login(
                        Credentials.jwt(tokenId)
                        //Credentials.google(tokenId, GoogleAuthType.ID_TOKEN)
                    ).loggedIn
                }
                withContext(Dispatchers.Main){
                    if(result){
                        onSuccess()
                        delay(600)
                        authenticated.value = true
                    }
                    else{
                        onError(Exception("User is not logged in."))
                    }
                }
            }catch (e: Exception){
                withContext(Dispatchers.Main){
                    onError(e)
                }
            }
        }
    }

}