package com.example.drivertest.ui.startup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivertest.data.DataStoreManager
import com.example.drivertest.data.repository.QuestionRepository
import com.example.drivertest.data.seed.DatabaseSeeder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class StartupViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val connectivityChecker: ConnectivityChecker,
    private val questionRepository: QuestionRepository,
    private val databaseSeeder: DatabaseSeeder
) : ViewModel() {

    private val _state = MutableStateFlow<StartupState>(StartupState.Loading)
    val state: StateFlow<StartupState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            initializeApp()
        }
    }

    private suspend fun initializeApp() {

        try {
            // 1. Seed dữ liệu cấu hình cố định
            databaseSeeder.seed()

            // 2. Kiểm tra dữ liệu API đã được khởi tạo chưa
            val isInitialized =
                dataStoreManager.isDataInitialized.first()

            val selectedLicenseID =
                dataStoreManager.selectedLicenseTypeID.first()

            // 3. Dữ liệu API đã có
            if (isInitialized) {

                if (selectedLicenseID != null) {
                    // Đã có dữ liệu + đã chọn hạng
                    _state.value = StartupState.Success
                } else {
                    // Đã có dữ liệu nhưng chưa chọn hạng
                    _state.value = StartupState.NeedLicenseSelection
                }

                return
            }

            // 4. Chưa có dữ liệu API → kiểm tra mạng
            if (!connectivityChecker.isNetworkAvailable()) {
                _state.value = StartupState.Error(
                    "Không có kết nối Internet."
                )
                return
            }

            // 5. Có mạng → tải dữ liệu từ API
            questionRepository.initializeRemoteData()

            // 6. Dữ liệu đã tải xong → kiểm tra lại hạng bằng
            val newSelectedLicenseID =
                dataStoreManager.selectedLicenseTypeID.first()

            if (newSelectedLicenseID != null) {
                _state.value = StartupState.Success
            } else {
                _state.value = StartupState.NeedLicenseSelection
            }

        } catch (e: IOException) {

            _state.value = StartupState.Error(
                "Không thể kết nối đến máy chủ."
            )

        } catch (e: Exception) {

            _state.value = StartupState.Error(
                e.message ?: "Đã xảy ra lỗi khi khởi tạo ứng dụng."
            )
        }
    }

    fun retry() {
        viewModelScope.launch {
            _state.value = StartupState.Loading
            delay(2000)
            initializeApp()
        }
    }
}