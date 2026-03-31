package com.example.arendaproga.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.arendaproga.data.Booking
import com.example.arendaproga.ui.RentViewModel

@Composable
fun MyBookingsScreen(
    vm: RentViewModel,
    onBack: (() -> Unit)? = null
) {
    val list = vm.myBookings

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
            Text("Мои бронирования", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        if (list.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📋", fontSize = 56.sp)
                    Spacer(Modifier.height(16.dp))
                    Text("Броней пока нет", fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = Color(0xFF1A1A2E))
                    Spacer(Modifier.height(6.dp))
                    Text("Забронируй авто в каталоге", color = Color.Gray, fontSize = 14.sp)
                }
            }
            return
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(list, key = { it.id }) { booking ->
                BookingCard(booking = booking, onCancel = { vm.cancelBooking(booking.id) })
            }
        }
    }
}

@Composable
private fun BookingCard(booking: Booking, onCancel: () -> Unit) {
    val isCancelled = booking.status == "Cancelled"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCancelled) Color(0xFFFAFAFA) else Color.White
        ),
        elevation = CardDefaults.cardElevation(if (isCancelled) 0.dp else 2.dp)
    ) {
        Column(Modifier.padding(18.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isCancelled) Color(0xFFF0F0F0) else Color(0xFFEDE7FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (isCancelled) "❌" else "🚗", fontSize = 22.sp)
                }

                Spacer(Modifier.width(12.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        booking.carTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (isCancelled) Color.Gray else Color(0xFF1A1A2E)
                    )
                    Spacer(Modifier.height(3.dp))
                    StatusBadge(status = booking.status)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "${booking.totalPrice} ₸",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (isCancelled) Color.Gray else Color(0xFF6C63FF)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))
            Divider(color = Color(0xFFF0F0F0))
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DateBadge(label = "Начало", date = booking.startDate)
                Text("→", color = Color.Gray, fontSize = 18.sp)
                DateBadge(label = "Конец", date = booking.endDate)
            }

            if (!isCancelled) {
                Spacer(Modifier.height(14.dp))
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color(0xFFD32F2F),
                        disabledContainerColor = Color.Transparent,
                        disabledContentColor = Color.Gray
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCDD2))
                ) {
                    Text("Отменить бронь", fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    val (label, bg, textColor) = when (status) {
        "Pending" -> Triple("⏳ Ожидает", Color(0xFFFFF8E1), Color(0xFFFF8F00))
        "Confirmed" -> Triple("✅ Подтверждено", Color(0xFFE8F5E9), Color(0xFF2E7D32))
        "Cancelled" -> Triple("❌ Отменено", Color(0xFFFFEBEE), Color(0xFFD32F2F))
        else -> Triple(status, Color(0xFFF0F0F0), Color.Gray)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(label, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun DateBadge(label: String, date: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color.Gray, fontSize = 11.sp)
        Spacer(Modifier.height(4.dp))
        Text(date, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF1A1A2E))
    }
}