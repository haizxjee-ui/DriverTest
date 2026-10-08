package com.example.drivertest.data.repository

import PracticeProgressEntity
import com.example.drivertest.data.local.AppDatabase
import com.example.drivertest.data.local.entity.QuestionTypeEntity
import UserQuestionProgressEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PracticeRepository @Inject constructor(
    private val database: AppDatabase
) {

    fun getAllQuestionTypes(): Flow<List<QuestionTypeEntity>> {
        return database
            .questionTypeDao()
            .getAllQuestionTypes()
    }

    fun getQuestionCount(
        questionTypeID: Int
    ): Flow<Int> {
        return database
            .questionDao()
            .getQuestionCountByType(questionTypeID)
    }

    fun getCriticalQuestionCount(
        questionTypeID: Int
    ): Flow<Int> {
        return database
            .questionDao()
            .getCriticalQuestionCountByType(questionTypeID)
    }

    fun getAnsweredQuestionCount(
        chapterNumber: Int,
        questionTypeID: Int
    ): Flow<Int> {
        return database
            .userQuestionProgressDao()
            .getAnsweredQuestionCountByType(
                chapterNumber,
                questionTypeID
            )
    }

    fun getAnsweredCriticalQuestionCount(): Flow<Int> {
        return database
            .userQuestionProgressDao()
            .getAnsweredCriticalQuestionCount()
    }

    suspend fun getPracticeProgress(
        chapterNumber: Int
    ): PracticeProgressEntity? {
        return database
            .practiceProgressDao()
            .getProgressByChapterNumber(chapterNumber)
    }

    suspend fun savePracticeProgress(
        progress: PracticeProgressEntity
    ) {
        database
            .practiceProgressDao()
            .insertProgress(progress)
    }

    suspend fun getQuestionProgress(
        chapterNumber: Int,
        questionID: Int
    ): UserQuestionProgressEntity? {
        return database
            .userQuestionProgressDao()
            .getProgressByQuestionID(
                chapterNumber,
                questionID
            )
    }

    suspend fun resetChapter(
        chapterNumber: Int
    ) {

        database
            .userQuestionProgressDao()
            .deleteProgressByChapter(
                chapterNumber
            )

        database
            .practiceProgressDao()
            .deleteProgressByChapter(
                chapterNumber
            )
    }

    suspend fun saveQuestionProgress(
        progress: UserQuestionProgressEntity
    ) {
        database
            .userQuestionProgressDao()
            .insertProgress(progress)
    }
}