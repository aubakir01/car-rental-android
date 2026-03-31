package com.example.arendaproga.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "cars")
data class Car(
    @PrimaryKey val id: String,
    val brand: String,
    val model: String,
    val year: Int,
    val pricePerDay: Int,
    val transmission: String,
    val seats: Int,
    val imageUrl: String? = null,
    val rating: Double
)

@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey val id: String,
    val carId: String,
    val carTitle: String,
    val startDate: String,
    val endDate: String,
    val totalPrice: Int,
    val status: String = "Pending"
)

@Entity(tableName = "favorites")
data class Favorite(
    @PrimaryKey val carId: String
)