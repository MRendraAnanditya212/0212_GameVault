package com.example.gamevault.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamevault.core.local.TokenManager
import com.example.gamevault.core.local.UserSession
import com.example.gamevault.core.network.RetrofitClient
import com.example.gamevault.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isRegisterSuccess: Boolean = false
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val tokenManager = TokenManager(application)
    private val repository = AuthRepository(
        authApiService = RetrofitClient.getAuthApiService(application),
        tokenManager = tokenManager
    )

    val sessionState: StateFlow<UserSession> = repository.sessionFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserSession()
    )

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email dan kata sandi wajib diisi.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.login(email, password)
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Selamat datang, ${user.gamerTag} (${user.role})"
                        )
                    }
                }
                .onFailure { err ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = err.message) }
                }
        }
    }

    fun register(namaLengkap: String, email: String, gamerTag: String, password: String) {
        if (namaLengkap.isBlank() || email.isBlank() || gamerTag.isBlank() || password.length < 6) {
            _uiState.update {
                it.copy(errorMessage = "Lengkapi semua kolom dan pastikan kata sandi minimal 6 karakter.")
            }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, isRegisterSuccess = false) }
            repository.register(namaLengkap, email, gamerTag, password)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRegisterSuccess = true,
                            successMessage = "Registrasi berhasil! Silakan masuk."
                        )
                    }
                }
                .onFailure { err ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = err.message) }
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _uiState.value = AuthUiState()
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null, isRegisterSuccess = false) }
    }
}