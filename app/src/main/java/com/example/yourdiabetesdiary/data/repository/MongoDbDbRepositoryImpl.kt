package com.example.yourdiabetesdiary.data.repository

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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.ZoneId

object MongoDbDbRepositoryImpl : MongoDbRepository {
    private val app = App.Companion.create(Constants.MONGO_DB_APP_ID)
    private val currentUser = app.currentUser
    private lateinit var realm: Realm

    init {
        configureRealmDb()
    }

    override fun configureRealmDb() {
        currentUser?.let { user ->
            val config = SyncConfiguration.Builder(user = user, schema = setOf(DiaryEntry::class))
                .initialSubscriptions { subscription ->
                    add(
                        query = subscription.query(DiaryEntry::class, "ownerId == $0", user.id),
                        name = "Retrieving user's diaries"
                    )
                }
                .log(LogLevel.ALL)
                .build()
            realm = Realm.open(config)
        }
    }

    override fun retrieveDiaries(): Flow<RequestState<DiariesType>> {
        return if (currentUser != null) {
            try {
                realm.query<DiaryEntry>(query = "ownerId == $0", currentUser.id)
                    .asFlow()
                    .map { result ->
                        RequestState.Success<DiariesType>(data = result.list.groupBy { diaryEntry ->
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
}