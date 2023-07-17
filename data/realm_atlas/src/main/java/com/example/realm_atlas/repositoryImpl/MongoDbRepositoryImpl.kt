package com.example.realm_atlas.repositoryImpl

import android.annotation.SuppressLint
import android.util.Log
import com.example.realm_atlas.DiariesType
import com.example.realm_atlas.MongoDbRepository
import com.example.util.Constants
import com.example.util.RequestState
import com.example.util.exceptions.CustomException
import com.example.util.models.DiaryEntry
import com.example.util.toInstant
import com.example.util.toRealmInstant
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
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

internal object MongoDbRepositoryImpl : MongoDbRepository {
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

    @SuppressLint("NewApi")
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

    @SuppressLint("NewApi")
    override fun retrieveFilteredDiaries(localDate: LocalDate): Flow<RequestState<DiariesType>> {
        return if (isSessionValid()) {
            try {
                val instant = ZonedDateTime.of(
                    localDate,
                    LocalTime.MIDNIGHT,
                    ZoneId.systemDefault()
                )

                realm.query<DiaryEntry>(
                    query = "ownerId == $0 AND date <= $1 AND date >= $2 ",
                    currentUser!!.id,
                    instant.plusDays(1).toInstant().toRealmInstant(),
                    instant.toInstant().toRealmInstant()
                )
                    .sort(property = "date", sortOrder = Sort.DESCENDING)
                    .asFlow()
                    .map { result ->
                        Log.d("TEST_DIARY", "2: ${result.list}")
                        Log.d("TEST_DIARY", "2: ${currentUser!!.id}")
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
        Log.d("TEST_STORING", "pulled: $diaryId")

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

    override suspend fun upsertEntry(diaryEntry: DiaryEntry): Flow<RequestState<DiaryEntry>> {
        Log.d("TEST_STORING", "navigated in viewmodel upsertEntry: ${diaryEntry._id}")

        return if (isSessionValid()) {
            withContext(Dispatchers.IO) {
                try {
                    try {
                        val queriedDiary = realm.write {
                            query<DiaryEntry>(query = "_id == $0", diaryEntry._id).first().find()
                        }
                        Log.d(
                            "TEST_STORING",
                            "navigated in viewmodel upsertEntry: ${queriedDiary == null}"
                        )
                        if (queriedDiary == null) {
                            realm.write {
                                copyToRealm(diaryEntry.apply {
                                    ownerId = currentUser!!.id
                                })
                            }
                            flow {
                                emit(RequestState.Success<DiaryEntry>(data = diaryEntry))
                            }
                        } else {
                            realm.write {
                                val queriedEntry =
                                    query<DiaryEntry>(query = "_id == $0", diaryEntry._id).first()
                                        .find()!!
                                queriedEntry.apply {
                                    this.title = diaryEntry.title
                                    this.description = diaryEntry.description
                                    this.date = diaryEntry.date
                                    this.images = diaryEntry.images
                                    this.mood = diaryEntry.mood
                                }
                            }
                            flow {
                                emit(RequestState.Success<DiaryEntry>(data = diaryEntry))
                            }
                        }
                    } catch (ex: NoSuchElementException) {
                        flow {
                            emit(RequestState.Error(ex))
                        }
                    }
                } catch (e: Exception) {
                    flow {
                        emit(RequestState.Error(e))
                    }
                }
            }
        } else {
            flow {
                emit(RequestState.Error(CustomException.UserNotAuthenticatedException()))
            }
        }
    }

    override suspend fun deleteDiary(diaryId: ObjectId): RequestState<Boolean> {
        return if (isSessionValid()) {
            withContext(Dispatchers.IO) {
                realm.write {
                    try {
                        val diary = query<DiaryEntry>(
                            "_id == $0 AND ownerId == $1",
                            diaryId,
                            currentUser!!.id
                        ).first().find()

                        Log.d("TEST_DELETING", "$diary")

                        if (diary != null) {
                            delete(diary)
                            RequestState.Success(true)
                        } else {
                            RequestState.Error(IllegalStateException("Diary doesn't exist"))
                        }
                    } catch (ex: Exception) {
                        RequestState.Error(ex = ex)
                    }
                }
            }
        } else {
            RequestState.Error(ex = CustomException.UserNotAuthenticatedException())
        }
    }

    override suspend fun deleteAllDiaries(): RequestState<Boolean> {
        return if (isSessionValid()) {
            withContext(Dispatchers.IO) {
                realm.write {
                    try {
                        val diary = query<DiaryEntry>(
                            "ownerId == $0",
                            currentUser!!.id
                        ).find()

                        delete(diary)

                        Log.d("TEST_DELETING", "$diary")
                        RequestState.Success(true)
                    } catch (ex: Exception) {
                        RequestState.Error(ex = ex)
                    }
                }
            }
        } else {
            RequestState.Error(ex = CustomException.UserNotAuthenticatedException())
        }
    }

    private fun isSessionValid() = currentUser != null
}