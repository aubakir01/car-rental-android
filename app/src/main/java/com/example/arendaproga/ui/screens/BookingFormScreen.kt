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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.arendaproga.ui.RentViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.max

private val dateFmt: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingFormScreen(
    vm: RentViewModel,
    carId: String,
    onBack: () -> Unit,
    onBooked: () -> Unit
) {
    val car = vm.getCar(carId)

    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var pickStart by remember { mutableStateOf(false) }
    var pickEnd by remember { mutableStateOf(false) }

    val days: Int = remember(startDate, endDate) {
        if (startDate == null || endDate == null) 0
        else max(1, java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1)
    }

    if (car == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Авто не найдено") }
        return
    }

    val total = car.pricePerDay * days

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F7FB))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
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
                    .padding(20.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .clickable { onBack() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("←", color = Color.White, fontSize = 18.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Бронирование", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text("${car.brand} ${car.model}", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                        }
                    }
                }
            }

            Column(Modifier.padding(20.dp)) {

                // Карточка авто
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFEDE7FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🚗", fontSize = 28.sp)
                        }
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text("${car.brand} ${car.model}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A1A2E))
                            Text("${car.year} • ${car.transmission}", color = Color.Gray, fontSize = 13.sp)
                            Text("${car.pricePerDay} ₸ / сутки", color = Color(0xFF6C63FF), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))
                Text("Выбери даты", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1A1A2E))
                Spacer(Modifier.height(12.dp))

                // Даты
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DatePickerCard(
                        label = "Начало",
                        emoji = "📅",
                        value = startDate?.format(dateFmt) ?: "Не выбрано",
                        onClick = { pickStart = true; error = null },
                        modifier = Modifier.weight(1f)
                    )
                    DatePickerCard(
                        label = "Конец",
                        emoji = "🏁",
                        value = endDate?.format(dateFmt) ?: "Не выбрано",
                        onClick = { pickEnd = true; error = null },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(20.dp))

                // Итог
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E))
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text("Итого", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    if (days == 0) "— ₸" else "$total ₸",
                                    color = Color.White,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    if (days == 0) "Выбери даты" else "$days ${dayWord(days)} × ${car.pricePerDay} ₸",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                if (error != null) {
                    Spacer(Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Text(
                            "⚠️  ${error!!}",
                            modifier = Modifier.padding(14.dp),
                            color = Color(0xFFD32F2F),
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        when {
                            startDate == null || endDate == null -> error = "Выбери обе даты"
                            endDate!!.isBefore(startDate) -> error = "Дата окончания раньше начала"
                            else -> {
                                vm.bookCar(
                                    car = car,
                                    startDate = startDate!!.format(dateFmt),
                                    endDate = endDate!!.format(dateFmt),
                                    days = days
                                )
                                onBooked()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    enabled = days > 0
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                if (days > 0)
                                    Brush.horizontalGradient(listOf(Color(0xFF6C63FF), Color(0xFFE040FB)))
                                else
                                    Brush.horizontalGradient(listOf(Color.Gray, Color.Gray)),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✅  Подтвердить бронь", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                Spacer(Modifier.height(20.dp))
            }
        }
    }

    if (pickStart) {
        DatePickerDialogM3(
            title = "Дата начала",
            initial = startDate,
            onDismiss = { pickStart = false },
            onConfirm = { picked ->
                startDate = picked
                if (endDate != null && picked != null && endDate!!.isBefore(picked)) endDate = null
                pickStart = false
            }
        )
    }

    if (pickEnd) {
        DatePickerDialogM3(
            title = "Дата окончания",
            initial = endDate ?: startDate,
            onDismiss = { pickEnd = false },
            onConfirm = { picked -> endDate = picked; pickEnd = false }
        )
    }
}

private fun dayWord(n: Int): String = when {
    n % 100 in 11..19 -> "дней"
    n % 10 == 1 -> "день"
    n % 10 in 2..4 -> "дня"
    else -> "дней"
}

@Composable
private fun DatePickerCard(
    label: String,
    emoji: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 24.sp)
            Spacer(Modifier.height(8.dp))
            Text(label, color = Color.Gray, fontSize = 12.sp)
            Spacer(Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color(0xFF1A1A2E))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialogM3(
    title: String,
    initial: LocalDate?,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate?) -> Unit
) {
    val initialMillis = initial?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    val state = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(state.selectedDateMillis?.toLocalDate()) }) { Text("ОК") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    ) {
        Column(Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            DatePicker(state = state)
        }
    }
}

private fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()