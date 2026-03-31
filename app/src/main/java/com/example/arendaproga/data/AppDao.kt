package com.example.arendaproga.data

import androidx.room.*

@Dao
interface AppDao {

    // ─── CARS ───────────────────────────
    @Query("SELECT * FROM cars")
    suspend fun getAllCars(): List<Car>

    @Query("SELECT * FROM cars WHERE id = :id")
    suspend fun getCarById(id: String): Car?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCars(cars: List<Car>)

    // ─── BOOKINGS ────────────────────────
    @Query("SELECT * FROM bookings ORDER BY rowid DESC")
    suspend fun getAllBookings(): List<Booking>

    @Insert
    suspend fun insertBooking(booking: Booking)

    @Query("UPDATE bookings SET status = 'Cancelled' WHERE id = :bookingId")
    suspend fun cancelBooking(bookingId: String)

    // ─── FAVORITES ───────────────────────
    @Query("SELECT * FROM cars WHERE id IN (SELECT carId FROM favorites)")
    suspend fun getFavoriteCars(): List<Car>

    @Query("SELECT COUNT(*) FROM favorites WHERE carId = :carId")
    suspend fun isFavorite(carId: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavorite(favorite: Favorite)

    @Query("DELETE FROM favorites WHERE carId = :carId")
    suspend fun removeFavorite(carId: String)
}