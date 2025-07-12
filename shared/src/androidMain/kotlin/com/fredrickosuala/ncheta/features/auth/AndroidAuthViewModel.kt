package com.fredrickosuala.ncheta.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredrickosuala.ncheta.repository.AuthRepository

class AndroidAuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    val authViewModel = AuthViewModel(
        authRepository = authRepository,
        coroutineScope = viewModelScope
    )

    override fun onCleared() {
        super.onCleared()
        authViewModel.clear()
    }
}