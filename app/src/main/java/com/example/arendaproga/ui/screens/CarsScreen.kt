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
import com.example.arendaproga.ui.components.AiAssistantButton

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

    Box(Modifier.fillMaxSize()) {
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
                    Text("Привет! 👋", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                    Text("Выбери автомобиль", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
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
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(GearFilter.ALL to "Все", GearFilter.AT to "Автомат", GearFilter.MT to "Механика")
                            .forEach { (g, label) ->
                                GearChip(label = label, selected = gear == g, onClick = { gear = g })
                            }
                    }
                    Spacer(Modifier.height(14.dp))
                }
                item {
                    PriceBlock(priceRange = priceRange, onChange = { priceRange = it })
                    Spacer(Modifier.height(14.dp))
                }
                item {
                    YearBlock(yearRange = yearRange, onChange = { yearRange = it })
                    Spacer(Modifier.height(6.dp))
                }
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 60.dp),
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

        AiAssistantButton(
            screenContext = "Каталог автомобилей",
            cars = vm.cars,
            onOpenCar = onOpenCar
        )
    }
} // ← конец CarsScreen

// ─────────────────────────────────────────────
// Вспомогательные компоненты — ВНЕ CarsScreen
// ─────────────────────────────────────────────

@Composable
private fun GearChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (selected) Color(0xFF6C63FF) else Color.White,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else Color(0xFF1A1A2E),
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
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
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Цена / день", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(
                    "${priceRange.start.toInt()} – ${priceRange.endInclusive.toInt()} ₸",
                    fontSize = 13.sp,
                    color = Color(0xFF6C63FF)
                )
            }
            Spacer(Modifier.height(8.dp))
            RangeSlider(
                value = priceRange,
                onValueChange = onChange,
                valueRange = MIN_PRICE..MAX_PRICE,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF6C63FF),
                    activeTrackColor = Color(0xFF6C63FF)
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun YearBlock(yearRange: IntRange, onChange: (IntRange) -> Unit) {
    var start by remember(yearRange) { mutableStateOf(yearRange.first.toFloat()) }
    var end by remember(yearRange) { mutableStateOf(yearRange.last.toFloat()) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Год выпуска", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(
                    "${start.toInt()} – ${end.toInt()}",
                    fontSize = 13.sp,
                    color = Color(0xFF6C63FF)
                )
            }
            Spacer(Modifier.height(8.dp))
            RangeSlider(
                value = start..end,
                onValueChange = { start = it.start; end = it.endInclusive },
                onValueChangeFinished = { onChange(start.toInt()..end.toInt()) },
                valueRange = MIN_YEAR.toFloat()..MAX_YEAR.toFloat(),
                steps = (MAX_YEAR - MIN_YEAR - 1),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF6C63FF),
                    activeTrackColor = Color(0xFF6C63FF)
                )
            )
        }
    }
}

@Composable
private fun SortDropdown(sort: SortMode, onChange: (SortMode) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val label = if (sort == SortMode.PRICE_ASC) "Цена ↑" else "Цена ↓"

    Box {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            modifier = Modifier.clickable { expanded = true }
        ) {
            Text(
                label,
                fontSize = 13.sp,
                color = Color(0xFF6C63FF),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("Цена ↑") },
                onClick = { onChange(SortMode.PRICE_ASC); expanded = false }
            )
            DropdownMenuItem(
                text = { Text("Цена ↓") },
                onClick = { onChange(SortMode.PRICE_DESC); expanded = false }
            )
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column {
            Box {
                AsyncImage(
                    model = car.imageUrl,
                    contentDescription = "${car.brand} ${car.model}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                )
                IconButton(
                    onClick = onToggleFav,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Text(if (isFav) "❤️" else "🤍", fontSize = 20.sp)
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF6C63FF),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                ) {
                    Text(
                        "${car.pricePerDay} ₸/день",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
            Column(Modifier.padding(16.dp)) {
                Text(
                    "${car.brand} ${car.model}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color(0xFF1A1A2E)
                )
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("📅 ${car.year}", fontSize = 13.sp, color = Color.Gray)
                    Text("⚙️ ${car.transmission}", fontSize = 13.sp, color = Color.Gray)
                }
            }
        }
    }
}