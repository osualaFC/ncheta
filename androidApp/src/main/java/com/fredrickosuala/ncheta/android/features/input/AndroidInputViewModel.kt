package com.fredrickosuala.ncheta.android.features.input

import androidx.lifecycle.ViewModel
import com.fredrickosuala.ncheta.features.input.InputViewModel

class AndroidInputViewModel(
    val sharedViewModel: InputViewModel = InputViewModel()
) : ViewModel() {

    override fun onCleared() {
        super.onCleared()
        sharedViewModel.clear()
    }
}