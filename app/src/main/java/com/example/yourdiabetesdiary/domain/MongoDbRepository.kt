package com.example.yourdiabetesdiary.domain

import com.example.util.models.DiaryEntry
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId
import java.time.LocalDate

typealias DiariesType = Map<LocalDate, List<com.example.util.models.DiaryEntry>>

interface MongoDbRepository {
    fun configureRealmDb()
    fun retrieveDiaries(): Flow<RequestState<DiariesType>>
    fun retrieveFilteredDiaries(localDate: LocalDate): Flow<RequestState<DiariesType>>
    suspend fun pullDiary(diaryId: ObjectId): Flow<RequestState<com.example.util.models.DiaryEntry>>
    suspend fun upsertEntry(diaryEntry: com.example.util.models.DiaryEntry): Flow<RequestState<com.example.util.models.DiaryEntry>>
    suspend fun deleteDiary(diaryId: ObjectId): RequestState<Boolean>
    suspend fun deleteAllDiaries(): RequestState<Boolean>
}