package com.example.arendaproga.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.arendaproga.data.RoomRentRepository

class RentViewModelFactory(
    private val repo: RoomRentRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RentViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}