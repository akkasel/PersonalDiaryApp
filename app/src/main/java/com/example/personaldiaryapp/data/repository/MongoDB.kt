package com.example.personaldiaryapp.data.repository

import com.example.personaldiaryapp.model.Diary
import com.example.personaldiaryapp.util.Constants.APP_ID
import com.example.personaldiaryapp.model.RequestState
import com.example.personaldiaryapp.util.toInstant
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.query.Sort
import io.realm.kotlin.types.ObjectId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.ZoneId
import kotlin.Exception

object MongoDB : MongoRepository {

    private val app = App.create(APP_ID)
    private val user = app.currentUser
    private lateinit var realm: Realm

    init {
        configureTheRealm()
    }

    override fun configureTheRealm() {
        if(user != null){
            val config = SyncConfiguration.Builder(user, setOf(Diary::class))
                .initialSubscriptions (rerunOnOpen = true){ sub ->
                    add(
                        query = sub.query<Diary>("ownerId == $0", user.identity),
                        // query = sub.query<Diary>("ownerId == $0 AND title == $1", user.identity),
                        name = "User's Diaries"
                    )
                }
                .log(LogLevel.ALL)
                .build()
            realm = Realm.open(config)
        }
    }

    // to read all Diaries from MongoDB
    override fun getAllDiaries(): Flow<Diaries> {
        return if (user != null) {
            try {
                realm.query<Diary>(query = "ownerId == $0", user.identity)
                    .sort(property = "date", sortOrder = Sort.DESCENDING)
                    .asFlow()
                    .map { result ->
                        RequestState.Success(
                            data = result.list.groupBy {
                                it.date.toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                            }
                        )
                    }
            } catch (e: Exception) {
                flow { emit(RequestState.Error(e)) }
            }
        } else {
            flow { emit(RequestState.Error(UserNotAuthenticatedException())) }
        }
    }

    override fun getSelectedDiary(diaryId: ObjectId): Flow<RequestState<Diary>> {
        return if(user != null){
            try{
                realm.query<Diary>(query="_id == $0", diaryId).asFlow().map {
                    RequestState.Success(data = it.list.first())
                }
            } catch(e: Exception){
                flow { emit(RequestState.Error(e))}
            }
        } else{
            flow { emit(RequestState.Error(UserNotAuthenticatedException())) }
        }
    }

    override suspend fun insertDiary(diary: Diary): RequestState<Diary> {
        return if(user != null){
            realm.write {
                try{
                    val addedDiary = copyToRealm(diary.apply { ownerId = user.id})
                    RequestState.Success(data = addedDiary)
                } catch(e: Exception){
                    RequestState.Error(e)
                }
            }

        } else{
            RequestState.Error(UserNotAuthenticatedException())
        }
    }

    override suspend fun updateDiary(diary: Diary): RequestState<Diary> {
        return if(user != null){
            realm.write {
                val queriedDiary = query<Diary>(query = "_id == $0", diary._id).first().find()
                if(queriedDiary != null){
                    queriedDiary.title = diary.title
                    queriedDiary.description = diary.description
                    queriedDiary.mood = diary.mood
                    queriedDiary.images = diary.images
                    queriedDiary.date = diary.date
                    RequestState.Success(data = queriedDiary)
                } else{
                    RequestState.Error(error = Exception("Queried Diary does not exist."))
                }
            }

        } else{
            RequestState.Error(UserNotAuthenticatedException())
        }
    }

    override suspend fun deleteDiary(id: ObjectId): RequestState<Diary> {
        return if(user != null){
            realm.write {
                val diary = query<Diary>(query = "_id == $0 AND ownerId == $1", id, user.identity)
                    .first().find()
                if(diary != null){
                    try {

                        delete(diary)
                        RequestState.Success(data = diary)
                    }catch (e: Exception){
                        RequestState.Error(e)
                    }
                } else {
                    RequestState.Error(Exception("Diary does not exist."))
                }

            }
        } else{
            RequestState.Error(UserNotAuthenticatedException())
        }
    }
}

private class UserNotAuthenticatedException : Exception("User is not Logged in")