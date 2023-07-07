package com.example.yourdiabetesdiary.data.repository

import com.example.yourdiabetesdiary.domain.RequestState
import com.example.yourdiabetesdiary.models.DiaryEntry
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId
import java.time.LocalDate

typealias DiariesType = Map<LocalDate, List<DiaryEntry>>

interface MongoDbRepository {
    fun configureRealmDb()
    fun retrieveDiaries(): Flow<RequestState<DiariesType>>
    suspend fun pullDiary(diaryId: ObjectId): RequestState<DiaryEntry>
    suspend fun addNewDiary(diaryEntry: DiaryEntry): RequestState<DiaryEntry>
}