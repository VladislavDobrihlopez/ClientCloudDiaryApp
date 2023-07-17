package com.example.realm_atlas

import com.example.util.RequestState
import com.example.util.models.DiaryEntry
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId
import java.time.LocalDate

typealias DiariesType = Map<LocalDate, List<DiaryEntry>>

interface MongoDbRepository {
    fun configureRealmDb()
    fun retrieveDiaries(): Flow<RequestState<DiariesType>>
    fun retrieveFilteredDiaries(localDate: LocalDate): Flow<RequestState<DiariesType>>
    suspend fun pullDiary(diaryId: ObjectId): Flow<RequestState<DiaryEntry>>
    suspend fun upsertEntry(diaryEntry: DiaryEntry): Flow<RequestState<DiaryEntry>>
    suspend fun deleteDiary(diaryId: ObjectId): RequestState<Boolean>
    suspend fun deleteAllDiaries(): RequestState<Boolean>
}