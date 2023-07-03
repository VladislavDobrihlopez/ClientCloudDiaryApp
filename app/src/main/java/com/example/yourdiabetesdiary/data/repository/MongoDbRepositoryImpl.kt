package com.example.yourdiabetesdiary.data.repository

import com.example.yourdiabetesdiary.models.DiaryEntry
import com.example.yourdiabetesdiary.util.Constants
import io.realm.kotlin.Realm
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.sync.SyncConfiguration

object MongoDbRepositoryImpl : MongoRepository {
    private val app = App.Companion.create(Constants.MONGO_DB_APP_ID)
    private val currentUser = app.currentUser
    private lateinit var realm: Realm
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
}