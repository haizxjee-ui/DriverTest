package com.example.drivertest.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.drivertest.data.local.AppDatabase
import com.example.drivertest.data.local.dao.AnswerDao
import com.example.drivertest.data.local.dao.ExamDao
import com.example.drivertest.data.local.dao.ExamDetailDao
import com.example.drivertest.data.local.dao.ImageDao
import com.example.drivertest.data.local.dao.LicenseQuestionRuleDao
import com.example.drivertest.data.local.dao.LicenseTypeDao
import com.example.drivertest.data.local.dao.QuestionDao
import com.example.drivertest.data.local.dao.QuestionTypeDao
import com.example.drivertest.data.local.dao.WrongQuestionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `wrong_questions` (
                `questionID` INTEGER NOT NULL,
                PRIMARY KEY(`questionID`),
                FOREIGN KEY(`questionID`)
                    REFERENCES `questions`(`questionID`)
                    ON UPDATE NO ACTION
                    ON DELETE CASCADE
            )
            """.trimIndent()
        )
    }
}

private val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `mock_exams` (
                `mockExamID` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `licenseTypeID` INTEGER NOT NULL,
                `examNumber` INTEGER NOT NULL,
                FOREIGN KEY(`licenseTypeID`)
                    REFERENCES `license_types`(`licenseTypeID`)
                    ON UPDATE NO ACTION
                    ON DELETE RESTRICT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE UNIQUE INDEX IF NOT EXISTS
            `index_mock_exams_licenseTypeID_examNumber`
            ON `mock_exams` (`licenseTypeID`, `examNumber`)
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `mock_exam_questions` (
                `mockExamID` INTEGER NOT NULL,
                `questionID` INTEGER NOT NULL,
                `questionOrder` INTEGER NOT NULL,
                PRIMARY KEY(`mockExamID`, `questionOrder`),
                FOREIGN KEY(`mockExamID`)
                    REFERENCES `mock_exams`(`mockExamID`)
                    ON UPDATE NO ACTION
                    ON DELETE CASCADE,
                FOREIGN KEY(`questionID`)
                    REFERENCES `questions`(`questionID`)
                    ON UPDATE NO ACTION
                    ON DELETE RESTRICT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS
            `index_mock_exam_questions_questionID`
            ON `mock_exam_questions` (`questionID`)
            """.trimIndent()
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        )
            .addMigrations(
                MIGRATION_1_2,
                MIGRATION_2_3
            )
            .build()
    }

    @Provides
    fun provideQuestionDao(database: AppDatabase): QuestionDao {
        return database.questionDao()
    }

    @Provides
    fun provideAnswerDao(database: AppDatabase): AnswerDao {
        return database.answerDao()
    }

    @Provides
    fun provideExamDao(database: AppDatabase): ExamDao {
        return database.examDao()
    }

    @Provides
    fun provideExamDetailDao(database: AppDatabase): ExamDetailDao {
        return database.examDetailDao()
    }

    @Provides
    fun provideLicenseQuestionRuleDao(
        database: AppDatabase
    ): LicenseQuestionRuleDao {
        return database.licenseQuestionRuleDao()
    }

    @Provides
    fun provideLicenseTypeDao(database: AppDatabase): LicenseTypeDao {
        return database.licenseTypeDao()
    }

    @Provides
    fun provideQuestionTypeDao(database: AppDatabase): QuestionTypeDao {
        return database.questionTypeDao()
    }

    @Provides
    fun provideImageDao(database: AppDatabase): ImageDao {
        return database.imageDao()
    }

    @Provides
    fun provideWrongQuestionDao(
        database: AppDatabase
    ): WrongQuestionDao {
        return database.wrongQuestionDao()
    }
}