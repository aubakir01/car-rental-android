package com.example.arendaproga.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.arendaproga.data.Car
import com.example.arendaproga.ui.RentViewModel

@Composable
fun FavoritesScreen(
    vm: RentViewModel,
    onOpenCar: (String) -> Unit
) {
    val list = vm.favorites

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F7FB))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFF1A1A2E), Color(0xFF0F3460))
                    )
                )
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Column {
                Text("Избранное", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("${list.size} авто", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
            }
        }

        if (list.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("♡", fontSize = 64.sp, color = Color(0xFFE040FB))
                    Spacer(Modifier.height(16.dp))
                    Text("Избранных нет", fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = Color(0xFF1A1A2E))
                    Spacer(Modifier.height(6.dp))
                    Text("Нажми ♡ на карточке авто", color = Color.Gray, fontSize = 14.sp)
                }
            }
            return
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(list, key = { it.id }) { car ->
                FavCard(car = car, onClick = { onOpenCar(car.id) }, onRemove = { vm.toggleFavorite(car.id) })
            }
        }
    }
}

@Composable
private fun FavCard(car: Car, onClick: () -> Unit, onRemove: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Фото
            AsyncImage(
                model = car.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .width(120.dp)
                    .height(100.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp)),
                contentScale = ContentScale.Crop
            )

            // Инфо
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp)
            ) {
                Text("${car.brand} ${car.model}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1A1A2E))
                Spacer(Modifier.height(4.dp))
                Text("${car.year} • ${car.transmission} • ${car.seats} мест", color = Color.Gray, fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
                Text("${car.pricePerDay} ₸/сут", color = Color(0xFF6C63FF), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            // Кнопка удалить
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFFFEBEE))
                    .clickable { onRemove() },
                contentAlignment = Alignment.Center
            ) {
                Text("♥", fontSize = 16.sp, color = Color(0xFFE040FB))
            }
        }
    }
}