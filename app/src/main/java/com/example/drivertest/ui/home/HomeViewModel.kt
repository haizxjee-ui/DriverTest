package com.example.drivertest.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivertest.data.DataStoreManager
import com.example.drivertest.data.local.dao.ExamDao
import com.example.drivertest.data.local.dao.LicenseTypeDao
import com.example.drivertest.data.local.entity.ExamEntity
import com.example.drivertest.data.local.entity.LicenseTypeEntity
import com.example.drivertest.data.repository.MockExamRepository
import com.example.drivertest.data.repository.WrongQuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    dataStoreManager: DataStoreManager,
    licenseTypeDao: LicenseTypeDao,
    private val wrongQuestionRepository: WrongQuestionRepository,
    private val mockExamRepository: MockExamRepository,
    examDao: ExamDao
) : ViewModel() {

    val currentLicense: StateFlow<LicenseTypeEntity?> =
        combine(
            dataStoreManager.selectedLicenseTypeID,
            licenseTypeDao.getAllLicenseTypes()
        ) { selectedID, licenseTypes ->
            licenseTypes.find {
                it.licenseTypeID == selectedID
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val wrongQuestionCount: StateFlow<Int> =
        wrongQuestionRepository.getAllWrongQuestions()
            .map { questions -> questions.size }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0
            )

    val examHistory: StateFlow<List<ExamEntity>> =
        examDao.getAllExams()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun clearExamHistory() {
        viewModelScope.launch {
            mockExamRepository.clearAllExamHistory()
        }
    }
}
