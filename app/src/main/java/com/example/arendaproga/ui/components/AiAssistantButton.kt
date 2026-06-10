package com.example.arendaproga.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.arendaproga.data.Car
import com.example.arendaproga.data.network.AiClient
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val carLinks: List<CarLink> = emptyList() // машины упомянутые в ответе
)

data class CarLink(
    val carId: String,
    val label: String // "Toyota Camry 2021"
)

@Composable
fun AiAssistantButton(
    screenContext: String = "",
    cars: List<Car> = emptyList(),         // ← передаём список машин
    onOpenCar: ((String) -> Unit)? = null  // ← навигация на машину
) {
    var showChat by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .size(56.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF6C63FF), Color(0xFFE040FB))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = { showChat = true }) {
                Text("🤖", fontSize = 24.sp)
            }
        }
    }

    if (showChat) {
        AiChatDialog(
            screenContext = screenContext,
            cars = cars,
            onOpenCar = onOpenCar,
            onDismiss = { showChat = false }
        )
    }
}

@Composable
private fun AiChatDialog(
    screenContext: String,
    cars: List<Car>,
    onOpenCar: ((String) -> Unit)?,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage(
                    text = "Привет! Я AI помощник приложения ArendaProga 🚗\nЗадай мне любой вопрос об аренде авто — я знаю все доступные машины!",
                    isUser = false
                )
            )
        )
    }
    var inputText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Формируем строку со списком машин для промпта
    val carsCatalog = remember(cars) {
        if (cars.isEmpty()) "Каталог пуст"
        else cars.joinToString("\n") { car ->
            "- ID:${car.id} | ${car.brand} ${car.model} ${car.year}г | ${car.pricePerDay}₸/день | КПП:${car.transmission} | мест:${car.seats} | рейтинг:${car.rating}"
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF6F7FB))
            ) {
                Column(Modifier.fillMaxSize()) {

                    // Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF6C63FF), Color(0xFFE040FB))
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text("🤖", fontSize = 28.sp)
                                Column {
                                    Text(
                                        "AI Помощник",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                    Text(
                                        "Знает ${cars.size} авто • Онлайн",
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            IconButton(onClick = onDismiss) {
                                Text("✕", color = Color.White, fontSize = 18.sp)
                            }
                        }
                    }

                    // Сообщения
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        state = listState,
                        contentPadding = PaddingValues(vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(messages) { message ->
                            ChatBubble(
                                message = message,
                                onOpenCar = onOpenCar,
                                onDismiss = onDismiss
                            )
                        }
                        if (isLoading) {
                            item {
                                Row(
                                    horizontalArrangement = Arrangement.Start,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp))
                                            .background(Color.White)
                                            .padding(14.dp)
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(16.dp),
                                                color = Color(0xFF6C63FF),
                                                strokeWidth = 2.dp
                                            )
                                            Text("AI думает...", color = Color.Gray, fontSize = 13.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Быстрые вопросы
                    if (messages.size == 1) {
                        val quickQuestions = listOf(
                            "Какое авто лучше для семьи?",
                            "Самое дешёвое авто?",
                            "Что такое AT и MT?"
                        )
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            quickQuestions.forEach { q ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Color(0xFFEDE7FF))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    TextButton(
                                        onClick = { inputText = q },
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text(
                                            q,
                                            fontSize = 11.sp,
                                            color = Color(0xFF6C63FF),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Поле ввода
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text("Задай вопрос...", fontSize = 14.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(20.dp),
                            maxLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF6C63FF),
                                unfocusedBorderColor = Color(0xFFE0E0E0)
                            )
                        )
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    if (inputText.isNotBlank() && !isLoading)
                                        Brush.linearGradient(listOf(Color(0xFF6C63FF), Color(0xFFE040FB)))
                                    else
                                        Brush.linearGradient(listOf(Color.Gray, Color.Gray))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(
                                onClick = {
                                    if (inputText.isNotBlank() && !isLoading) {
                                        val userMsg = inputText.trim()
                                        inputText = ""
                                        messages = messages + ChatMessage(userMsg, isUser = true)
                                        isLoading = true

                                        scope.launch {
                                            try {
                                                val prompt = buildString {
                                                    append("Ты помощник приложения для аренды автомобилей ArendaProga. ")
                                                    append("Отвечай кратко и по делу на русском языке. ")
                                                    append("Когда советуешь конкретные машины — обязательно упоминай их точно в формате: [Бренд Модель Год] (например [Toyota Camry 2021]). ")
                                                    append("Это важно — именно в квадратных скобках, чтобы пользователь мог перейти к машине. ")
                                                    if (screenContext.isNotBlank()) {
                                                        append("Контекст: пользователь на экране '$screenContext'. ")
                                                    }
                                                    append("\n\nДоступные автомобили в каталоге:\n$carsCatalog\n\n")
                                                    append("Вопрос пользователя: $userMsg")
                                                }

                                                val aiText = AiClient.ask(prompt)

                                                // Ищем упомянутые машины в ответе по формату [Бренд Модель Год]
                                                val mentionedCars = mutableListOf<CarLink>()
                                                val regex = Regex("""\[([^\]]+)\]""")
                                                regex.findAll(aiText).forEach { match ->
                                                    val label = match.groupValues[1]
                                                    // Ищем машину по бренду и модели
                                                    val found = cars.find { car ->
                                                        label.contains(car.brand, ignoreCase = true) &&
                                                                label.contains(car.model, ignoreCase = true)
                                                    }
                                                    if (found != null) {
                                                        mentionedCars.add(CarLink(found.id, "${found.brand} ${found.model} ${found.year}"))
                                                    }
                                                }

                                                messages = messages + ChatMessage(
                                                    text = aiText,
                                                    isUser = false,
                                                    carLinks = mentionedCars.distinctBy { it.carId }
                                                )
                                            } catch (e: Exception) {
                                                messages = messages + ChatMessage(
                                                    "⚠️ Ошибка: ${e.message}",
                                                    isUser = false
                                                )
                                            }
                                            isLoading = false
                                        }
                                    }
                                },
                                enabled = inputText.isNotBlank() && !isLoading
                            ) {
                                Text("➤", color = Color.White, fontSize = 18.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(
    message: ChatMessage,
    onOpenCar: ((String) -> Unit)?,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(Color(0xFF6C63FF), Color(0xFFE040FB)))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("🤖", fontSize = 16.sp)
            }
            Spacer(Modifier.width(8.dp))
        }

        Column(horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start) {
            Box(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .clip(
                        if (message.isUser)
                            RoundedCornerShape(16.dp, 4.dp, 16.dp, 16.dp)
                        else
                            RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp)
                    )
                    .background(
                        if (message.isUser)
                            Brush.linearGradient(listOf(Color(0xFF6C63FF), Color(0xFFE040FB)))
                        else
                            Brush.linearGradient(listOf(Color.White, Color.White))
                    )
                    .padding(12.dp)
            ) {
                // Убираем квадратные скобки из текста для красивого отображения
                val cleanText = message.text.replace(Regex("""\[([^\]]+)\]"""), "$1")
                Text(
                    cleanText,
                    color = if (message.isUser) Color.White else Color(0xFF1A1A2E),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }

            // Кнопки-ссылки на машины
            if (message.carLinks.isNotEmpty() && onOpenCar != null) {
                Spacer(Modifier.height(6.dp))
                message.carLinks.forEach { link ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFEDE7FF))
                            .clickable {
                                onDismiss()
                                onOpenCar(link.carId)
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        val annotated = buildAnnotatedString {
                            append("🚗 ")
                            withStyle(SpanStyle(
                                color = Color(0xFF6C63FF),
                                fontWeight = FontWeight.SemiBold,
                                textDecoration = TextDecoration.Underline
                            )) {
                                append(link.label)
                            }
                            append(" →")
                        }
                        Text(annotated, fontSize = 13.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                }
            }
        }
    }
}