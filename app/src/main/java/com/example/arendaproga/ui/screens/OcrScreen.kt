package com.example.arendaproga.ui.screens

import android.graphics.BitmapFactory
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.arendaproga.data.network.RetrofitClient
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition

import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale

@Composable
fun OcrScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var recognizedText by remember { mutableStateOf("") }
    var translatedText by remember { mutableStateOf("") }
    var isRecognizing by remember { mutableStateOf(false) }
    var isTranslating by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var ttsReady by remember { mutableStateOf(false) }

    // Инициализация TTS
    DisposableEffect(Unit) {
        val t = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsReady = true
            }
        }
        tts = t
        onDispose { t.shutdown() }
    }

    // Выбор изображения из галереи
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        isRecognizing = true
        error = null
        recognizedText = ""
        translatedText = ""

        try {
            val stream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(stream)
            val image = InputImage.fromBitmap(bitmap, 0)
            val recognizer = TextRecognition.getClient(com.google.mlkit.vision.text.latin.TextRecognizerOptions.DEFAULT_OPTIONS)
            recognizer.process(image)
                .addOnSuccessListener { result ->
                    recognizedText = result.text
                    isRecognizing = false
                    if (recognizedText.isBlank()) {
                        error = "Текст не найден на изображении"
                    }
                }
                .addOnFailureListener { e ->
                    error = "Ошибка распознавания: ${e.message}"
                    isRecognizing = false
                }
        } catch (e: Exception) {
            error = "Не удалось открыть изображение"
            isRecognizing = false
        }
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
                Text(
                    "📷 Распознавание текста",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "ML Kit + Перевод + Озвучивание",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Кнопка выбора фото
            Button(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
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
                    Text(
                        "📁  Выбрать изображение",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            // Индикатор загрузки
            if (isRecognizing) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF6C63FF),
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 3.dp
                        )
                        Text("Распознаём текст...", color = Color.Gray)
                    }
                }
            }

            // Ошибка
            if (error != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Text(
                        "⚠️ $error",
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFFD32F2F),
                        fontSize = 14.sp
                    )
                }
            }

            // Блок распознанного текста
            if (recognizedText.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "📝 Распознанный текст",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF1A1A2E)
                            )
                            // Кнопка озвучить
                            IconButton(
                                onClick = {
                                    if (ttsReady) {
                                        tts?.language = Locale.ENGLISH
                                        tts?.speak(
                                            recognizedText,
                                            TextToSpeech.QUEUE_FLUSH,
                                            null,
                                            null
                                        )
                                    }
                                }
                            ) {
                                Text("🔊", fontSize = 22.sp)
                            }
                        }

                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider(color = Color(0xFFF0F0F0))
                        Spacer(Modifier.height(8.dp))

                        Text(
                            recognizedText,
                            fontSize = 14.sp,
                            color = Color(0xFF333333),
                            lineHeight = 22.sp
                        )

                        Spacer(Modifier.height(16.dp))

                        // Кнопка перевести
                        Button(
                            onClick = {
                                scope.launch {
                                    isTranslating = true
                                    translatedText = ""
                                    error = null
                                    try {
                                        val response = RetrofitClient.translateApiService.translate(
                                            text = recognizedText,
                                            langPair = "en|ru"
                                        )
                                        translatedText = response.responseData?.translatedText
                                            ?: "Перевод недоступен"
                                    } catch (e: IOException) {
                                        error = "Нет интернета для перевода"
                                    } catch (e: Exception) {
                                        error = "Ошибка перевода: ${e.message}"
                                    }
                                    isTranslating = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6C63FF)
                            )
                        ) {
                            if (isTranslating) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    "🌍  Перевести на русский",
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // Блок перевода
            if (translatedText.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7FF)),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "🌍 Перевод (RU)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF1A1A2E)
                            )
                            // Кнопка озвучить перевод
                            IconButton(
                                onClick = {
                                    if (ttsReady) {
                                        tts?.language = Locale("ru")
                                        tts?.speak(
                                            translatedText,
                                            TextToSpeech.QUEUE_FLUSH,
                                            null,
                                            null
                                        )
                                    }
                                }
                            ) {
                                Text("🔊", fontSize = 22.sp)
                            }
                        }

                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider(color = Color(0xFFD1C4E9))
                        Spacer(Modifier.height(8.dp))

                        Text(
                            translatedText,
                            fontSize = 14.sp,
                            color = Color(0xFF333333),
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            // Пустое состояние
            if (!isRecognizing && recognizedText.isBlank() && error == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("📷", fontSize = 64.sp)
                        Text(
                            "Выбери изображение с текстом",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = Color(0xFF1A1A2E),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "Приложение распознает текст,\nпереведёт и озвучит его",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }
    }
}