package com.example.arendaproga.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.arendaproga.data.network.CarApiResponse
import com.example.arendaproga.data.network.NetworkResult
import com.example.arendaproga.data.network.RetrofitClient
import com.example.arendaproga.ui.RentViewModel
import kotlinx.coroutines.launch
import java.io.IOException

private const val API_KEY = "knJ8RcZpvvS6OqTW8NKQccqiOn3Uh4shXpG9NyQe"

@Composable
fun CarNewsScreen(vm: RentViewModel) {
    val scope = rememberCoroutineScope()
    var result by remember { mutableStateOf<NetworkResult<List<CarApiResponse>>>(NetworkResult.Loading) }
    var selectedMake by remember { mutableStateOf("toyota") }

    val makes = listOf(
        "toyota" to "Toyota",
        "bmw" to "BMW",
        "mercedes-benz" to "Mercedes",
        "kia" to "Kia",
        "hyundai" to "Hyundai",
        "audi" to "Audi"
    )

    fun load(make: String) {
        scope.launch {
            result = NetworkResult.Loading
            result = try {
                val data = RetrofitClient.carApiService.getCars(
                    apiKey = API_KEY,
                    make = make,
                    year = 2020

                )
                // Фильтруем — только машины от 2015 года
                val filtered = data.filter { it.year >= 2015 }
                if (filtered.isEmpty()) NetworkResult.Error("Данные не найдены")
                else NetworkResult.Success(filtered)
            } catch (e: IOException) {
                NetworkResult.Error("❌ Нет подключения к интернету")
            } catch (e: Exception) {
                NetworkResult.Error("❌ Ошибка сервера: ${e.message}")
            }
        }
    }

    LaunchedEffect(selectedMake) { load(selectedMake) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F7FB))
    ) {
        // Header
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
                Text(
                    "🌐 Характеристики авто",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Данные из внешнего API",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Фильтр марок
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(makes) { (apiName, displayName) ->
                        val selected = apiName == selectedMake
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (selected) Color(0xFF6C63FF) else Color.White
                                )
                                .clickable { selectedMake = apiName }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text(
                                displayName,
                                color = if (selected) Color.White else Color(0xFF555555),
                                fontSize = 13.sp,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            // Состояния загрузки
            when (val r = result) {
                is NetworkResult.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = Color(0xFF6C63FF))
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    "Загружаем данные из API...",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                is NetworkResult.Error -> {
                    item {
                        ErrorCard(
                            message = r.message,
                            onRetry = { load(selectedMake) }
                        )
                    }
                }

                is NetworkResult.Success -> {
                    item {
                        Text(
                            "Найдено: ${r.data.size} авто",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                    items(r.data) { car ->
                        ApiCarCard(car = car)
                    }
                }
            }
        }
    }
}

@Composable
private fun ApiCarCard(car: CarApiResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            // Заголовок карточки
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "${car.make.replaceFirstChar { it.uppercase() }} ${car.model.replaceFirstChar { it.uppercase() }}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1A1A2E)
                    )
                    Text(
                        car.`class`?.replaceFirstChar { it.uppercase() } ?: "",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
                // Год
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEDE7FF))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        "${car.year} г.",
                        color = Color(0xFF6C63FF),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(Modifier.height(12.dp))

            // Характеристики — строка 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ApiSpecChip(
                    emoji = "⚙️",
                    label = "КПП",
                    value = when (car.transmission) {
                        "a" -> "Автомат"
                        "m" -> "Механика"
                        "am" -> "Робот"
                        else -> car.transmission?.uppercase() ?: "—"
                    },
                    modifier = Modifier.weight(1f)
                )
                ApiSpecChip(
                    emoji = "⛽",
                    label = "Топливо",
                    value = when (car.fuel_type) {
                        "gas" -> "Бензин"
                        "diesel" -> "Дизель"
                        "electricity" -> "Электро"
                        "hybrid" -> "Гибрид"
                        else -> car.fuel_type?.replaceFirstChar { it.uppercase() } ?: "—"
                    },
                    modifier = Modifier.weight(1f)
                )
                ApiSpecChip(
                    emoji = "🛞",
                    label = "Привод",
                    value = when (car.drive) {
                        "fwd" -> "Передний"
                        "rwd" -> "Задний"
                        "awd" -> "Полный"
                        "4wd" -> "4WD"
                        else -> car.drive?.uppercase() ?: "—"
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(8.dp))

            // Характеристики — строка 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ApiSpecChip(
                    emoji = "🔧",
                    label = "Цилиндры",
                    value = if (car.cylinders != null) "${car.cylinders} цил." else "—",
                    modifier = Modifier.weight(1f)
                )
                ApiSpecChip(
                    emoji = "📐",
                    label = "Объём",
                    value = if (car.displacement != null) "${car.displacement} л." else "—",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ApiSpecChip(
    emoji: String,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF6F7FB))
            .padding(horizontal = 8.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 18.sp)
            Spacer(Modifier.height(4.dp))
            Text(
                value,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A2E),
                textAlign = TextAlign.Center
            )
            Text(
                label,
                fontSize = 10.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ErrorCard(message: String, onRetry: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("⚠️", fontSize = 48.sp)
            Spacer(Modifier.height(12.dp))
            Text(
                message,
                color = Color(0xFFD32F2F),
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("🔄 Повторить", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}