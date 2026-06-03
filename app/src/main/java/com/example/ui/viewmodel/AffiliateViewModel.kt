package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AffiliateClickLog
import com.example.data.AffiliateRepository
import com.example.data.AppDatabase
import com.example.data.Car
import com.example.data.GarageItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AffiliateViewModel(
    private val repository: AffiliateRepository
) : ViewModel() {

    // Car catalog state (static list)
    val catalog: List<Car> = repository.getCatalog()

    // Expose garage items reactively
    val garageItems: StateFlow<List<GarageItem>> = repository.garageItems
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Expose click redirection records reactively
    val clicksLog: StateFlow<List<AffiliateClickLog>> = repository.clicksLog
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Derive aggregate stats: Total MSRP in user garage
    val totalMSRP: StateFlow<Double> = garageItems
        .map { list -> list.sumOf { it.numericPrice } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    // Derive aggregate stats: Total commissions generated (simulated)
    val totalCommissions: StateFlow<Double> = clicksLog
        .map { list -> list.sumOf { it.commissionEarned } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    // Check if a vehicle is already present in their garage
    fun isCarInGarage(carId: Int): Flow<Boolean> {
        return repository.garageItems.map { list ->
            list.any { it.id == carId }
        }
    }

    // Interactive Action callbacks
    fun addToGarage(car: Car) {
        viewModelScope.launch {
            repository.addToGarage(car)
        }
    }

    fun removeFromGarage(carId: Int) {
        viewModelScope.launch {
            repository.removeFromGarage(carId)
        }
    }

    fun clearGarage() {
        viewModelScope.launch {
            repository.clearGarage()
        }
    }

    fun logSingleAffiliateRedirect(car: Car) {
        viewModelScope.launch {
            repository.logAffiliateClick(car)
        }
    }

    fun logBatchAffiliateCheckout(items: List<GarageItem>) {
        if (items.isEmpty()) return
        viewModelScope.launch {
            repository.logBatchAllCheckout(items)
        }
    }

    private suspend fun AffiliateRepository.logBatchAllCheckout(items: List<GarageItem>) {
        this.logBatchAffiliateCheckout(items)
    }

    fun clearAnalyticsLog() {
        viewModelScope.launch {
            repository.clearClicksLog()
        }
    }

    // Simple Factory Provider
    class Factory(private val repository: AffiliateRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AffiliateViewModel::class.java)) {
                return AffiliateViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
