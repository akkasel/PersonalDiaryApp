package com.akka.util

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import io.realm.kotlin.types.RealmInstant
import java.time.Instant

// Download images from Firebase asynchronously.
// This function returns imageUri after each successful download.
fun fetchImagesFromFirebase(
    remoteImagePath: List<String>,
    onImageDownload: (Uri) -> Unit,
    onImageDownloadFailed: (Exception) -> Unit = {},
    onReadyToDisplay: () -> Unit = {}
) {
    if (remoteImagePath.isNotEmpty()) {
        remoteImagePath.forEachIndexed { index, remoteImagePath ->
            if (remoteImagePath.trim().isNotEmpty()) {
                FirebaseStorage.getInstance().reference.child(remoteImagePath.trim()).downloadUrl
                    .addOnSuccessListener {
                        onImageDownload(it)
                        if (remoteImagePath.lastIndexOf(remoteImagePath.last()) == index) {
                            onReadyToDisplay()
                        }
                    }.addOnFailureListener {
                        onImageDownloadFailed(it)
                    }
            }
        }
    }
}

// convert Realm Instant to regular Instant type
fun RealmInstant.toInstant() : Instant {
    val sec: Long = this.epochSeconds
    val nano: Int = this.nanosecondsOfSecond
    return if(sec >= 0){
        Instant.ofEpochSecond(sec, nano.toLong())
    } else{
        Instant.ofEpochSecond(sec - 1, 1_000_000 + nano.toLong() )
    }
}



fun Instant.toRealmInstant(): RealmInstant{
    val sec: Long = this.epochSecond

    val nano: Int = this.nano

    return if(sec >= 0){
        RealmInstant.from(sec, nano)
    } else{
        RealmInstant.from(sec + 1, -1_000_000 + nano)
    }
}