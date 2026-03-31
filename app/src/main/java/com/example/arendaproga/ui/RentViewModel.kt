package com.example.arendaproga.ui

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arendaproga.data.Booking
import com.example.arendaproga.data.Car
import com.example.arendaproga.data.RoomRentRepository
import kotlinx.coroutines.launch
import java.util.UUID

class RentViewModel(
    private val repo: RoomRentRepository
) : ViewModel() {

    var isLoggedIn by mutableStateOf(false)
        private set

    var cars by mutableStateOf(emptyList<Car>())
        private set

    var favorites by mutableStateOf(emptyList<Car>())
        private set

    var myBookings by mutableStateOf(emptyList<Booking>())
        private set

    var isLoading by mutableStateOf(false)
        private set

    // Кэш машин для быстрого getCar()
    private val carsCache = mutableMapOf<String, Car>()

    fun loadInitial() {
        viewModelScope.launch {
            isLoading = true
            cars = repo.getCarsAsync()
            cars.forEach { carsCache[it.id] = it }
            myBookings = repo.getBookingsAsync()
            favorites = repo.getFavoritesAsync()
            isLoading = false
        }
    }

    fun login(username: String, password: String): Boolean {
        val ok = username.isNotBlank() && password.isNotBlank()
        isLoggedIn = ok
        return ok
    }

    fun logout() {
        isLoggedIn = false
    }

    fun getCar(id: String): Car? = carsCache[id]

    fun bookCar(car: Car, startDate: String, endDate: String, days: Int) {
        viewModelScope.launch {
            val total = days.coerceAtLeast(1) * car.pricePerDay
            val booking = Booking(
                id = UUID.randomUUID().toString(),
                carId = car.id,
                carTitle = "${car.brand} ${car.model} (${car.year})",
                startDate = startDate,
                endDate = endDate,
                totalPrice = total,
                status = "Pending"
            )
            repo.addBookingAsync(booking)
            myBookings = repo.getBookingsAsync()
        }
    }

    fun cancelBooking(bookingId: String) {
        viewModelScope.launch {
            repo.cancelBookingAsync(bookingId)
            myBookings = repo.getBookingsAsync()
        }
    }

    fun isFavorite(carId: String): Boolean = favorites.any { it.id == carId }

    fun toggleFavorite(carId: String) {
        viewModelScope.launch {
            repo.toggleFavoriteAsync(carId)
            favorites = repo.getFavoritesAsync()
        }
    }

    var profileName by mutableStateOf("Пользователь")
    var profilePhone by mutableStateOf("+7 777 000 00 00")
}