package com.example.personaldiaryapp.data.database.entity

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ImageToUpload::class],
    version = 1,
    exportSchema = false
)
abstract class ImagesDatabase : RoomDatabase() {
    abstract fun imageToUploadDao(): ImageToUploadDao
}