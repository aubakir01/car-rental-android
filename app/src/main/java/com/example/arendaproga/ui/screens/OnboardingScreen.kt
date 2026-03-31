package com.example.arendaproga.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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

data class OnboardingPage(
    val emoji: String,
    val title: String,
    val subtitle: String,
    val gradient: List<Color>
)

val onboardingPages = listOf(
    OnboardingPage(
        emoji = "🚗",
        title = "Добро пожаловать\nв ArendaProga",
        subtitle = "Аренда автомобилей\nбыстро и удобно",
        gradient = listOf(Color(0xFF1A1A2E), Color(0xFF0F3460))
    ),
    OnboardingPage(
        emoji = "🔍",
        title = "Найди\nсвой автомобиль",
        subtitle = "Фильтруй по цене, году\nи типу коробки передач",
        gradient = listOf(Color(0xFF0F3460), Color(0xFF6C63FF))
    ),
    OnboardingPage(
        emoji = "📅",
        title = "Бронируй\nв пару касаний",
        subtitle = "Выбери даты и подтверди\nбронь за несколько секунд",
        gradient = listOf(Color(0xFF6C63FF), Color(0xFFE040FB))
    ),
    OnboardingPage(
        emoji = "♥",
        title = "Сохраняй\nизбранное",
        subtitle = "Добавляй авто в избранное\nи возвращайся к ним позже",
        gradient = listOf(Color(0xFFE040FB), Color(0xFF1A1A2E))
    )
)

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    var currentPage by remember { mutableStateOf(0) }
    val page = onboardingPages[currentPage]
    val isLast = currentPage == onboardingPages.lastIndex

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(page.gradient))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // Кнопка пропустить
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (!isLast) {
                    TextButton(onClick = onFinish) {
                        Text(
                            "Пропустить",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Центральный блок
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Эмодзи в круге
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(page.emoji, fontSize = 56.sp)
                    }
                }

                // Текст
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = page.title,
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 36.sp
                    )
                    Text(
                        text = page.subtitle,
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                }
            }

            // Нижний блок — точки + кнопка
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                // Индикатор страниц
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    onboardingPages.forEachIndexed { index, _ ->
                        val isSelected = index == currentPage
                        val width by animateDpAsState(
                            targetValue = if (isSelected) 28.dp else 8.dp,
                            animationSpec = tween(300),
                            label = "dot_width"
                        )
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(width)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) Color.White
                                    else Color.White.copy(alpha = 0.35f)
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { currentPage = index }
                        )
                    }
                }

                // Кнопка
                Button(
                    onClick = {
                        if (isLast) onFinish()
                        else currentPage++
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text(
                        text = if (isLast) "Начать" else "Далее",
                        color = page.gradient.first(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}