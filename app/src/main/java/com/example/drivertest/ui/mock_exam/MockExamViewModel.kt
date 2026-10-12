package com.example.drivertest.ui.mock_exam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivertest.data.DataStoreManager
import com.example.drivertest.data.local.entity.MockExamEntity
import com.example.drivertest.data.repository.MockExamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed class MockExamListUiState {
    data object Loading : MockExamListUiState()

    data class Success(
        val exams: List<MockExamEntity>
    ) : MockExamListUiState()

    data class Error(
        val message: String
    ) : MockExamListUiState()
}

@HiltViewModel
class MockExamViewModel @Inject constructor(
    private val mockExamRepository: MockExamRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<MockExamListUiState>(MockExamListUiState.Loading)

    val uiState: StateFlow<MockExamListUiState> =
        _uiState.asStateFlow()

    fun loadExams() {
        viewModelScope.launch {
            _uiState.value = MockExamListUiState.Loading

            try {
                val licenseTypeID =
                    dataStoreManager.selectedLicenseTypeID.first()

                if (licenseTypeID == null) {
                    _uiState.value = MockExamListUiState.Error(
                        "Bạn chưa chọn hạng bằng lái"
                    )
                    return@launch
                }

                val exams =
                    mockExamRepository.getOrCreateMockExams(licenseTypeID)

                _uiState.value = MockExamListUiState.Success(exams)

            } catch (e: Exception) {
                _uiState.value = MockExamListUiState.Error(
                    e.message ?: "Không thể tải danh sách đề thi"
                )
            }
        }
    }
}