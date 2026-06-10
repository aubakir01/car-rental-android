package com.example.arendaproga

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.arendaproga.data.AppDatabase
import com.example.arendaproga.data.RoomRentRepository
import com.example.arendaproga.ui.RentViewModel
import com.example.arendaproga.ui.RentViewModelFactory
import com.example.arendaproga.ui.screens.*
import com.example.arendaproga.ui.theme.ArendaProgaTheme

sealed class Route(val value: String) {
    object Onboarding : Route("onboarding")
    object Login : Route("login")
    object Main : Route("main")
    object Favorites : Route("favorites")
    object Cars : Route("cars")
    object Bookings : Route("bookings")
    object Profile : Route("profile")
    object Api : Route("api_cars")
    object Ocr : Route("ocr")
    object CarDetails : Route("car/{id}") {
        fun create(id: String) = "car/$id"
    }
    object BookingForm : Route("booking/{id}") {
        fun create(id: String) = "booking/$id"
    }
}

data class BottomItem(val route: String, val label: String, val emoji: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализируем базу и репо один раз
        val db = AppDatabase.getInstance(applicationContext)
        val repo = RoomRentRepository(db.dao())
        val factory = RentViewModelFactory(repo)

        setContent {
            ArendaProgaTheme {
                val vm: RentViewModel = viewModel(factory = factory)

                // Правильная загрузка данных при старте
                LaunchedEffect(Unit) {
                    vm.loadInitial()
                }

                val rootNav = rememberNavController()

                NavHost(navController = rootNav, startDestination = Route.Onboarding.value) {
                    composable(Route.Onboarding.value) {
                        OnboardingScreen(onFinish = {
                            rootNav.navigate(Route.Login.value) {
                                popUpTo(Route.Onboarding.value) { inclusive = true }
                            }
                        })
                    }
                    composable(Route.Login.value) {
                        LoginScreen(vm = vm, onLoginSuccess = {
                            rootNav.navigate(Route.Main.value) {
                                popUpTo(Route.Login.value) { inclusive = true }
                            }
                        })
                    }
                    composable(Route.Api.value) { CarNewsScreen(vm) }
                    composable(Route.Main.value) {
                        MainScaffold(vm = vm, onLogout = {
                            rootNav.navigate(Route.Login.value) {
                                popUpTo(Route.Main.value) { inclusive = true }
                            }
                        })
                    }
                }
            }
        }
    }
}

@Composable
private fun MainScaffold(vm: RentViewModel, onLogout: () -> Unit) {
    val nav = rememberNavController()
    val items = listOf(
        BottomItem(Route.Cars.value, "Каталог", "🚗"),
        BottomItem(Route.Favorites.value, "Избранное", "♡"),
        BottomItem(Route.Bookings.value, "Брони", "📋"),

        BottomItem(Route.Profile.value, "Профиль", "👤"),
        BottomItem(Route.Ocr.value, "OCR", "📷"),
    )

    Scaffold(
        bottomBar = {
            val navBackStackEntry by nav.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            Surface(shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    items.forEach { item ->
                        NavBarItem(
                            item = item,
                            selected = currentRoute == item.route,
                            onClick = {
                                nav.navigate(item.route) {
                                    popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(navController = nav, startDestination = Route.Cars.value, modifier = Modifier.padding(padding)) {
            composable(Route.Cars.value) { CarsScreen(vm, { nav.navigate(Route.CarDetails.create(it)) }, { nav.navigate(Route.Bookings.value) }) }
            composable(Route.Bookings.value) { MyBookingsScreen(vm) }
            composable(Route.Ocr.value) { OcrScreen() }
            composable(Route.Profile.value) { ProfileScreen(vm, onLogout) }
            composable(Route.Favorites.value) { FavoritesScreen(vm, { nav.navigate(Route.CarDetails.create(it)) }) }
            composable(Route.Api.value) { CarNewsScreen(vm) }  // ← добавь эту строку
            composable(Route.CarDetails.value, arguments = listOf(navArgument("id") { type = NavType.StringType })) {
                val id = it.arguments?.getString("id") ?: ""
                CarDetailsScreen(vm, id, { nav.popBackStack() }, { nav.navigate(Route.BookingForm.create(id)) })
            }
            composable(Route.BookingForm.value, arguments = listOf(navArgument("id") { type = NavType.StringType })) {
                val id = it.arguments?.getString("id") ?: ""
                BookingFormScreen(vm, id, { nav.popBackStack() }, { nav.navigate(Route.Bookings.value) { popUpTo(Route.Cars.value) } })
            }
        }
    }
}

@Composable
private fun NavBarItem(item: BottomItem, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) Color(0xFF6C63FF).copy(0.1f) else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(item.emoji, fontSize = 20.sp)
        Text(item.label, fontSize = 10.sp, color = if (selected) Color(0xFF6C63FF) else Color.Gray)
    }
}