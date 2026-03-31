package com.example.arendaproga.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.arendaproga.ui.RentViewModel

@Composable
fun CarDetailsScreen(
    vm: RentViewModel,
    carId: String,
    onBack: () -> Unit,
    onBook: () -> Unit
) {
    val car = vm.getCar(carId)
    val isFav = vm.favorites.any { it.id == carId }

    if (car == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Авто не найдено")
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF6F7FB))) {

        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

            // Фото + оверлей
            Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                AsyncImage(
                    model = car.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Тёмный градиент сверху и снизу
                Box(
                    modifier = Modifier.fillMaxSize().background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.5f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.3f)
                            )
                        )
                    )
                )
                // Кнопка назад
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .clickable { onBack() }
                        .align(Alignment.TopStart),
                    contentAlignment = Alignment.Center
                ) {
                    Text("←", color = Color.White, fontSize = 20.sp)
                }
                // Кнопка избранного
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .clickable { vm.toggleFavorite(car.id) }
                        .align(Alignment.TopEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (isFav) "♥" else "♡",
                        color = if (isFav) Color(0xFFE040FB) else Color.White,
                        fontSize = 20.sp
                    )
                }
                // Название поверх фото (снизу)
                Column(
                    modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)
                ) {
                    Text(
                        "${car.brand} ${car.model}",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFFF8E1))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("⭐ ${car.rating}", color = Color(0xFFFF8F00), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                        Spacer(Modifier.width(8.dp))
                        Text("${car.year}", color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp)
                    }
                }
            }

            // Контент
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color(0xFFF6F7FB))
                    .padding(20.dp)
                    .offset(y = (-12).dp)
            ) {
                // Характеристики
                Text("Характеристики", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1A1A2E))
                Spacer(Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SpecCard(emoji = "⚙️", label = "Коробка", value = car.transmission, modifier = Modifier.weight(1f))
                    SpecCard(emoji = "💺", label = "Мест", value = "${car.seats}", modifier = Modifier.weight(1f))
                    SpecCard(emoji = "📅", label = "Год", value = "${car.year}", modifier = Modifier.weight(1f))
                }

                Spacer(Modifier.height(24.dp))

                // Цена
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1A1A2E)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Стоимость аренды", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "${car.pricePerDay} ₸",
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text("в сутки", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                        }
                        Text("🚗", fontSize = 48.sp)
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Кнопка бронирования
                Button(
                    onClick = onBook,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF6C63FF), Color(0xFFE040FB))
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🚀  Забронировать", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun SpecCard(emoji: String, label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 22.sp)
            Spacer(Modifier.height(6.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A1A2E))
            Text(label, color = Color.Gray, fontSize = 11.sp)
        }
    }
}