package com.example.drivertest.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.drivertest.data.local.dao.AnswerDao
import com.example.drivertest.data.local.dao.ExamDao
import com.example.drivertest.data.local.dao.ExamDetailDao
import com.example.drivertest.data.local.dao.ImageDao
import com.example.drivertest.data.local.dao.LicenseQuestionRuleDao
import com.example.drivertest.data.local.dao.LicenseTypeDao
import com.example.drivertest.data.local.dao.PracticeProgressDao
import com.example.drivertest.data.local.dao.QuestionDao
import com.example.drivertest.data.local.dao.QuestionTypeDao
import com.example.drivertest.data.local.dao.UserQuestionProgressDao
import com.example.drivertest.data.local.dao.WrongQuestionDao
import com.example.drivertest.data.local.entity.AnswerEntity
import com.example.drivertest.data.local.entity.ExamDetailEntity
import com.example.drivertest.data.local.entity.ExamEntity
import com.example.drivertest.data.local.entity.ImageEntity
import com.example.drivertest.data.local.entity.LicenseQuestionRuleEntity
import com.example.drivertest.data.local.entity.LicenseTypeEntity
import com.example.drivertest.data.local.entity.QuestionEntity
import com.example.drivertest.data.local.entity.QuestionTypeEntity
import com.example.drivertest.data.local.entity.WrongQuestionEntity
import PracticeProgressEntity
import UserQuestionProgressEntity
import com.example.drivertest.data.local.dao.MockExamDao
import com.example.drivertest.data.local.entity.MockExamEntity
import com.example.drivertest.data.local.entity.MockExamQuestionEntity

@Database(
    entities = [
        QuestionEntity::class,
        AnswerEntity::class,
        ImageEntity::class,
        QuestionTypeEntity::class,
        LicenseTypeEntity::class,
        LicenseQuestionRuleEntity::class,
        ExamEntity::class,
        ExamDetailEntity::class,
        UserQuestionProgressEntity::class,
        PracticeProgressEntity::class,
        WrongQuestionEntity::class,
        MockExamEntity::class,
        MockExamQuestionEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun questionDao(): QuestionDao
    abstract fun answerDao(): AnswerDao
    abstract fun imageDao(): ImageDao
    abstract fun questionTypeDao(): QuestionTypeDao
    abstract fun licenseTypeDao(): LicenseTypeDao
    abstract fun licenseQuestionRuleDao(): LicenseQuestionRuleDao
    abstract fun examDao(): ExamDao
    abstract fun examDetailDao(): ExamDetailDao
    abstract fun userQuestionProgressDao(): UserQuestionProgressDao
    abstract fun practiceProgressDao(): PracticeProgressDao
    abstract fun wrongQuestionDao(): WrongQuestionDao
    abstract fun mockExamDao(): MockExamDao
}