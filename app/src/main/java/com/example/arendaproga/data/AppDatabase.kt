package com.example.arendaproga.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Car::class, Booking::class, Favorite::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "arendaproga.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Заполняем начальными данными при первом запуске
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.dao()?.insertCars(initialCars)
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// Начальные данные
val initialCars = listOf(
    Car("1","Toyota","Camry",2020,18000,"AT",5,
        "https://cdn.imagin.studio/getimage?customer=img&make=toyota&modelFamily=camry&modelYear=2020&paintId=color-white&angle=01",4.8),
    Car("2","Toyota","RAV4",2021,22000,"AT",5,
        "https://cdn.imagin.studio/getimage?customer=img&make=toyota&modelFamily=rav4&modelYear=2021&paintId=color-white&angle=01",4.9),
    Car("3","Toyota","Corolla",2022,14000,"AT",5,
        "https://cdn.imagin.studio/getimage?customer=img&make=toyota&modelFamily=corolla&modelYear=2022&paintId=color-red&angle=01",4.6),
    Car("4","Hyundai","Elantra",2021,15000,"AT",5,
        "https://cdn.imagin.studio/getimage?customer=img&make=hyundai&modelFamily=elantra&modelYear=2021&paintId=color-blue&angle=01",4.6),
    Car("5","Hyundai","Tucson",2022,24000,"AT",5,
        "https://cdn.imagin.studio/getimage?customer=img&make=hyundai&modelFamily=tucson&modelYear=2022&paintId=color-white&angle=01",4.7),
    Car("6","Hyundai","Sonata",2020,17000,"AT",5,
        "https://cdn.imagin.studio/getimage?customer=img&make=hyundai&modelFamily=sonata&modelYear=2020&paintId=color-black&angle=01",4.5),
    Car("7","Kia","Sportage",2019,20000,"AT",5,
        "https://cdn.imagin.studio/getimage?customer=img&make=kia&modelFamily=sportage&modelYear=2019&paintId=color-white&angle=01",4.7),
    Car("8","Kia","K5",2021,19000,"AT",5,
        "https://cdn.imagin.studio/getimage?customer=img&make=kia&modelFamily=k5&modelYear=2021&paintId=color-red&angle=01",4.8),
    Car("9","Kia","Seltos",2022,21000,"AT",5,
        "https://cdn.imagin.studio/getimage?customer=img&make=kia&modelFamily=seltos&modelYear=2022&paintId=color-blue&angle=01",4.6),
    Car("10","BMW","3 Series",2021,35000,"AT",5,
        "https://cdn.imagin.studio/getimage?customer=img&make=bmw&modelFamily=3-series&modelYear=2021&paintId=color-black&angle=01",4.9),
    Car("11","BMW","X5",2020,45000,"AT",5,
        "https://cdn.imagin.studio/getimage?customer=img&make=bmw&modelFamily=x5&modelYear=2020&paintId=color-white&angle=01",4.9),
    Car("12","Mercedes-Benz","C-Class",2021,40000,"AT",5,
        "https://cdn.imagin.studio/getimage?customer=img&make=mercedes-benz&modelFamily=c-class&modelYear=2021&paintId=color-silver&angle=01",4.8),
    Car("13","Mercedes-Benz","GLE",2022,55000,"AT",5,
        "https://cdn.imagin.studio/getimage?customer=img&make=mercedes-benz&modelFamily=gle&modelYear=2022&paintId=color-black&angle=01",5.0),
    Car("14","Lada","Vesta",2022,12000,"MT",5,
        "https://cdn.imagin.studio/getimage?customer=img&make=lada&modelFamily=vesta&modelYear=2022&paintId=color-white&angle=01",4.3),
    Car("15","Volkswagen","Polo",2020,13000,"AT",5,
        "https://cdn.imagin.studio/getimage?customer=img&make=volkswagen&modelFamily=polo&modelYear=2020&paintId=color-red&angle=01",4.4),
    Car("16","Volkswagen","Tiguan",2021,26000,"AT",5,
        "https://cdn.imagin.studio/getimage?customer=img&make=volkswagen&modelFamily=tiguan&modelYear=2021&paintId=color-blue&angle=01",4.7),
    Car("17","Nissan","Qashqai",2021,23000,"AT",5,
        "https://cdn.imagin.studio/getimage?customer=img&make=nissan&modelFamily=qashqai&modelYear=2021&paintId=color-white&angle=01",4.6),
    Car("18","Nissan","X-Trail",2020,25000,"AT",7,
        "https://cdn.imagin.studio/getimage?customer=img&make=nissan&modelFamily=x-trail&modelYear=2020&paintId=color-black&angle=01",4.5),
    Car("19","Chevrolet","Malibu",2019,16000,"AT",5,
        "https://cdn.imagin.studio/getimage?customer=img&make=chevrolet&modelFamily=malibu&modelYear=2019&paintId=color-white&angle=01",4.4),
    Car("20","Audi","A4",2021,38000,"AT",5,
        "https://cdn.imagin.studio/getimage?customer=img&make=audi&modelFamily=a4&modelYear=2021&paintId=color-grey&angle=01",4.8)
)