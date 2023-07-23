package com.example.personaldiaryapp.util

import android.net.Uri
import androidx.core.net.toUri
import com.example.personaldiaryapp.data.database.entity.ImageToDelete
import com.example.personaldiaryapp.data.database.entity.ImageToUpload
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storageMetadata
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

fun retryUploadingImageToFirebase(
    imageToUpload: ImageToUpload,
    onSuccess: () -> Unit
) {
    val storage = FirebaseStorage.getInstance().reference
    storage.child(imageToUpload.remoteImagePath).putFile(
        imageToUpload.imageUri.toUri(),
        storageMetadata { },
        imageToUpload.sessionUri.toUri()
    ).addOnSuccessListener { onSuccess() }
}

fun retryDeletingImageToFirebase(
    imageToDelete: ImageToDelete,
    onSuccess: () -> Unit
) {
    val storage = FirebaseStorage.getInstance().reference
    storage.child(imageToDelete.remoteImagePath).delete()
        .addOnSuccessListener { onSuccess() }
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