package com.example.arendaproga.data

interface RentRepository {
    fun getCars(): List<Car>
    fun getCarById(id: String): Car?
    fun getMyBookings(): List<Booking>
    fun addBooking(booking: Booking)
    fun cancelBooking(bookingId: String)
    fun getFavorites(): List<Car>
    fun isFavorite(carId: String): Boolean
    fun toggleFavorite(carId: String)
}

class RoomRentRepository(private val dao: AppDao) : RentRepository {

    override fun getCars(): List<Car> = emptyList()
    override fun getCarById(id: String): Car? = null
    override fun getMyBookings(): List<Booking> = emptyList()
    override fun addBooking(booking: Booking) {}
    override fun cancelBooking(bookingId: String) {}
    override fun getFavorites(): List<Car> = emptyList()
    override fun isFavorite(carId: String): Boolean = false
    override fun toggleFavorite(carId: String) {}

    // ─── Suspend функции для ViewModel ───

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