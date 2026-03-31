package com.example.arendaproga.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.arendaproga.data.Car
import com.example.arendaproga.ui.RentViewModel

private enum class GearFilter { ALL, AT, MT }
private enum class SortMode { PRICE_ASC, PRICE_DESC }

private const val MIN_PRICE = 0f
private const val MAX_PRICE = 55000f
private const val MIN_YEAR = 2019
private const val MAX_YEAR = 2022

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarsScreen(
    vm: RentViewModel,
    onOpenCar: (String) -> Unit,
    onOpenBookings: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var gear by remember { mutableStateOf(GearFilter.ALL) }
    var sort by remember { mutableStateOf(SortMode.PRICE_ASC) }
    var priceRange by remember { mutableStateOf(MIN_PRICE..MAX_PRICE) }
    var yearRange by remember { mutableStateOf(MIN_YEAR..MAX_YEAR) }

    val filtered: List<Car> = remember(vm.cars, query, gear, sort, priceRange, yearRange) {
        vm.cars.asSequence()
            .filter { car ->
                val q = query.lowercase().trim()
                q.isBlank() || "${car.brand} ${car.model}".lowercase().contains(q)
            }
            .filter { car ->
                when (gear) {
                    GearFilter.ALL -> true
                    GearFilter.AT -> car.transmission.uppercase() == "AT"
                    GearFilter.MT -> car.transmission.uppercase() == "MT"
                }
            }
            .filter { car -> car.pricePerDay in priceRange.start.toInt()..priceRange.endInclusive.toInt() }
            .filter { car -> car.year in yearRange }
            .sortedWith(
                when (sort) {
                    SortMode.PRICE_ASC -> compareBy { it.pricePerDay }
                    SortMode.PRICE_DESC -> compareByDescending { it.pricePerDay }
                }
            )
            .toList()
    }

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
                Text("Привет! 👋", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                Text("Выбери автомобиль", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {

            // Поиск
            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Поиск: Toyota, Kia...") },
                    leadingIcon = { Text("🔍", fontSize = 18.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF6C63FF),
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
                Spacer(Modifier.height(14.dp))
            }

            // КПП фильтр
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(GearFilter.ALL to "Все", GearFilter.AT to "Автомат", GearFilter.MT to "Механика")
                        .forEach { (g, label) ->
                            GearChip(label = label, selected = gear == g, onClick = { gear = g })
                        }
                }
                Spacer(Modifier.height(14.dp))
            }

            // Фильтр цены
            item {
                PriceBlock(priceRange = priceRange, onChange = { priceRange = it })
                Spacer(Modifier.height(14.dp))
            }

            // Фильтр года
            item {
                YearBlock(yearRange = yearRange, onChange = { yearRange = it })
                Spacer(Modifier.height(6.dp))
            }

            // Заголовок списка
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${filtered.size} авто",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Color(0xFF1A1A2E)
                    )
                    SortDropdown(sort = sort, onChange = { sort = it })
                }
            }

            if (filtered.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(top = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🚫", fontSize = 48.sp)
                            Spacer(Modifier.height(12.dp))
                            Text("Ничего не найдено", fontWeight = FontWeight.Medium, color = Color.Gray)
                        }
                    }
                }
            } else {
                items(filtered, key = { it.id }) { car ->
                    CarCard(
                        car = car,
                        isFav = vm.isFavorite(car.id),
                        onToggleFav = { vm.toggleFavorite(car.id) },
                        onClick = { onOpenCar(car.id) }
                    )
                    Spacer(Modifier.height(14.dp))
                }
            }
        }
    }
}

@Composable
private fun GearChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) Color(0xFF6C63FF) else Color.White)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            label,
            color = if (selected) Color.White else Color(0xFF555555),
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PriceBlock(
    priceRange: ClosedFloatingPointRange<Float>,
    onChange: (ClosedFloatingPointRange<Float>) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("💰 Цена в сутки", fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2E))
                Text(
                    "${priceRange.start.toInt()} — ${priceRange.endInclusive.toInt()} ₸",
                    color = Color(0xFF6C63FF),
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                )
            }
            RangeSlider(
                value = priceRange,
                onValueChange = onChange,
                valueRange = MIN_PRICE..MAX_PRICE,
                steps = 9,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF6C63FF),
                    activeTrackColor = Color(0xFF6C63FF)
                )
            )
        }
    }
}

@Composable
private fun YearBlock(yearRange: IntRange, onChange: (IntRange) -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("📅 Год выпуска", fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2E))
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                (MIN_YEAR..MAX_YEAR).forEach { year ->
                    val selected = year in yearRange
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selected) Color(0xFF6C63FF) else Color(0xFFF0F0F5))
                            .clickable {
                                val newRange = when {
                                    year < yearRange.first -> year..yearRange.last
                                    year > yearRange.last -> yearRange.first..year
                                    year == yearRange.first && yearRange.first != yearRange.last -> (year + 1)..yearRange.last
                                    year == yearRange.last && yearRange.first != yearRange.last -> yearRange.first..(year - 1)
                                    else -> year..year
                                }
                                onChange(newRange)
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "$year",
                            color = if (selected) Color.White else Color(0xFF555555),
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortDropdown(sort: SortMode, onChange: (SortMode) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = if (sort == SortMode.PRICE_ASC) "Цена ↑" else "Цена ↓",
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().widthIn(min = 130.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF6C63FF),
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text("Цена ↑") }, onClick = { onChange(SortMode.PRICE_ASC); expanded = false })
            DropdownMenuItem(text = { Text("Цена ↓") }, onClick = { onChange(SortMode.PRICE_DESC); expanded = false })
        }
    }
}

@Composable
private fun CarCard(
    car: Car,
    isFav: Boolean,
    onToggleFav: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = car.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(180.dp).clip(
                        RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    ),
                    contentScale = ContentScale.Crop
                )
                // Градиент снизу фото
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.45f))
                            )
                        )
                )
                // Цена поверх фото
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF6C63FF))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text("${car.pricePerDay} ₸/сут", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                // Кнопка избранного
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.9f))
                        .clickable { onToggleFav() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (isFav) "♥" else "♡", fontSize = 18.sp, color = if (isFav) Color(0xFFE040FB) else Color.Gray)
                }
            }

            Column(Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "${car.brand} ${car.model}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = Color(0xFF1A1A2E)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "${car.year} • ${car.transmission} • ${car.seats} мест",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                    // Рейтинг
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFFF8E1))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("⭐", fontSize = 12.sp)
                        Spacer(Modifier.width(3.dp))
                        Text(
                            "${car.rating}",
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFFF8F00),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}