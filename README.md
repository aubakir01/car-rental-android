# Car Rental Android App

Мобильное приложение для аренды автомобилей, написанное на Kotlin с использованием Jetpack Compose.

## Что умеет приложение

- **Каталог автомобилей** — просмотр доступных машин с фильтрацией по параметрам
- **Бронирование** — оформление аренды с выбором дат и условий
- **Мои брони** — история и текущие бронирования
- **Избранное** — сохранение понравившихся автомобилей
- **AI ассистент** — встроенный чат для помощи с выбором автомобиля
- **OCR сканер** — распознавание текста с документов через камеру
- **Личный профиль** — управление аккаунтом и данными пользователя
- **Онбординг** — знакомство с приложением при первом запуске


## Стек технологий

| Область | Технология |
|---|---|
| Язык | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Навигация | Navigation Compose |
| База данных | Room |
| Сеть | Retrofit + OkHttp |
| AI | Groq API (LLaMA 3.1) |
| OCR | Google ML Kit |
| Изображения | Coil |
| Архитектура | MVVM + Repository pattern |



## Запуск проекта

1. Клонируйте репозиторий
   git clone https://github.com/aubakir01/car-rental-android.git

2. Создайте файл `local.properties` в корне проекта и добавьте ваш Groq API ключ:
   GROQ_API_KEY=your_api_key_here

3. Получить бесплатный ключ можно на [console.groq.com](https://console.groq.com)

4. Откройте проект в Android Studio и запустите на эмуляторе или устройстве

## Минимальные требования

- Android 7.0 (API 24) и выше
- Android Studio Hedgehog или новее

## Скриншоты

### Онбординг
![Онбординг 1](screenshots/01_onboarding.png_1.jpg)
![Онбординг 2](screenshots/01_onboarding_2.png.jpg)
![Онбординг 3](screenshots/01_onboarding_3.png.jpg)
![Онбординг 4](screenshots/01_onboarding_4.png.jpg)

### Авторизация
![Вход](screenshots/02_login.png.jpg)

### Каталог автомобилей
![Каталог 1](screenshots/03_cars_catalog_1.jpg)
![Каталог 2](screenshots/03_cars_catalog_2.jpg)

### Детали автомобиля
![Детали](screenshots/04_car_details.jpg)

### Бронирование
![Форма бронирования](screenshots/05_booking_form.jpg)

### Мои брони
![Мои брони](screenshots/06_my_bookings.jpg)

### Избранное
![Избранное](screenshots/07_favorites.jpg)

### AI ассистент
![AI ассистент](screenshots/08_ai_assistant.jpg)

### OCR сканер
![OCR сканер](screenshots/09_ocr_scanner.jpg)

### Профиль
![Профиль](screenshots/10_profile.jpg)