package com.example.yourdiabetesdiary.data.repository

import android.util.Log
import com.example.yourdiabetesdiary.domain.RequestState
import com.example.yourdiabetesdiary.domain.exceptions.CustomException
import com.example.yourdiabetesdiary.models.DiaryEntry
import com.example.yourdiabetesdiary.util.Constants
import com.example.yourdiabetesdiary.util.toInstant
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.mongodb.kbson.ObjectId
import java.time.ZoneId

object MongoDbDbRepositoryImpl : MongoDbRepository {
    private val app = App.Companion.create(Constants.MONGO_DB_APP_ID)
    private val currentUser = app.currentUser
    private lateinit var realm: Realm

    init {
        configureRealmDb()
    }

    override fun configureRealmDb() {
        if (isSessionValid()) {
            val config =
                SyncConfiguration.Builder(user = currentUser!!, schema = setOf(DiaryEntry::class))
                    .initialSubscriptions { subscription ->
                        add(
                            query = subscription.query<DiaryEntry>("ownerId == $0", currentUser.id),
                            name = "Retrieving user's diaries"
                        )
                    }
                    .log(LogLevel.ALL)
                    .build()
            realm = Realm.open(config)
        }
    }

    override fun retrieveDiaries(): Flow<RequestState<DiariesType>> {
        return if (isSessionValid()) {
            try {
                realm.query<DiaryEntry>(query = "ownerId == $0", currentUser!!.id)
                    .sort(property = "date", sortOrder = Sort.DESCENDING)
                    .asFlow()
                    .map { result ->
                        Log.d("TEST_DIARY", "${result.list}")
                        Log.d("TEST_DIARY", "${currentUser!!.id}")
                        RequestState.Success(data = result.list.groupBy { diaryEntry ->
                            diaryEntry.date.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        })
                    }
            } catch (ex: Exception) {
                flow {
                    emit(RequestState.Error(ex))
                }
            }
        } else {
            flow {
                emit(RequestState.Error(CustomException.UserNotAuthenticatedException()))
            }
        }
    }

    override suspend fun pullDiary(diaryId: ObjectId): Flow<RequestState<DiaryEntry>> {
        return if (isSessionValid()) {
            withContext(Dispatchers.IO) {
                try {
                    realm.query<DiaryEntry>("_id == $0", diaryId).find().asFlow().map {
                        RequestState.Success<DiaryEntry>(data = it.list.first())
                    }
                } catch (e: Exception) {
                    flow {
                        RequestState.Error(e)
                    }
                }
            }
        } else {
            flow {
                RequestState.Error(CustomException.UserNotAuthenticatedException())
            }
        }
    }

    override suspend fun addNewDiary(diaryEntry: DiaryEntry): RequestState<DiaryEntry> {
        return if (isSessionValid()) {
            withContext(Dispatchers.IO) {
                try {
                    val addedDiary = realm.write {
                        copyToRealm(diaryEntry.apply {
                            ownerId = currentUser!!.id
                        })
                    }
                    RequestState.Success<DiaryEntry>(data = addedDiary)
                } catch (e: Exception) {
                    RequestState.Error(e)
                }
            }
        } else {
            RequestState.Error(CustomException.UserNotAuthenticatedException())
        }
    }

    private fun isSessionValid() = currentUser != null
}