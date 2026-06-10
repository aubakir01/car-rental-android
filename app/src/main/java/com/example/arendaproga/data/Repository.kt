package com.example.arendaproga.data

class RoomRentRepository(private val dao: AppDao) {

    suspend fun getCarsAsync(): List<Car> = dao.getAllCars()

    suspend fun getCarByIdAsync(id: String): Car? = dao.getCarById(id)

    suspend fun getBookingsAsync(): List<Booking> = dao.getAllBookings()

    suspend fun addBookingAsync(booking: Booking) = dao.insertBooking(booking)

    suspend fun cancelBookingAsync(id: String) = dao.cancelBooking(id)

    suspend fun getFavoritesAsync(): List<Car> = dao.getFavoriteCars()

    suspend fun isFavoriteAsync(carId: String): Boolean = dao.isFavorite(carId) > 0

    suspend fun toggleFavoriteAsync(carId: String) {
        if (dao.isFavorite(carId) > 0) dao.removeFavorite(carId)
        else dao.addFavorite(Favorite(carId))
    }
}