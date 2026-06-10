package com.example.arendaproga.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.arendaproga.ui.components.AiAssistantButton

@Composable
fun ProfileScreen(
    vm: RentViewModel,
    onLogout: () -> Unit
) {
    var name by remember { mutableStateOf("Amir") }
    var phone by remember { mutableStateOf("+7 777 000 00 00") }
    var saved by remember { mutableStateOf(false) }

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
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF6C63FF), Color(0xFFE040FB))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👤", fontSize = 36.sp)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(name, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(phone, color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                }
            }

            Column(Modifier.padding(20.dp)) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Редактировать профиль", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color(0xFF1A1A2E))
                        Spacer(Modifier.height(14.dp))
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it; saved = false },
                            label = { Text("Имя") },
                            leadingIcon = { Text("👤", fontSize = 18.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF6C63FF))
                        )
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it; saved = false },
                            label = { Text("Телефон") },
                            leadingIcon = { Text("📞", fontSize = 18.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF6C63FF))
                        )
                        Spacer(Modifier.height(14.dp))
                        Button(
                            onClick = { saved = true },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF))
                        ) {
                            Text(if (saved) "✅ Сохранено" else "Сохранить", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        StatItem(emoji = "🚗", value = "${vm.myBookings.size}", label = "Броней")
                        StatItem(emoji = "♥", value = "${vm.favorites.size}", label = "Избранных")
                        StatItem(emoji = "✅", value = "${vm.myBookings.count { it.status == "Confirmed" }}", label = "Подтверждено")
                    }
                }

                Spacer(Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { vm.logout(); onLogout() },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color(0xFFD32F2F),
                        disabledContainerColor = Color.Transparent,
                        disabledContentColor = Color.Gray
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCDD2))
                ) {
                    Text("🚪  Выйти из аккаунта", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                }
            }
        }
        AiAssistantButton(screenContext = "Профиль пользователя")
    }
}

@Composable
private fun StatItem(emoji: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 24.sp)
        Spacer(Modifier.height(4.dp))
        Text(value, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF1A1A2E))
        Text(label, color = Color.Gray, fontSize = 12.sp)
    }
}