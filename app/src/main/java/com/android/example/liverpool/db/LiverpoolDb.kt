package com.android.example.liverpool.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.android.example.liverpool.vo.PlpSearchProductResults
import com.android.example.liverpool.vo.Product

@Database(
        entities = [
            Product::class,
            PlpSearchProductResults::class],
        version = 5,
        exportSchema = false
)
abstract class LiverpoolDb : RoomDatabase() {
    abstract fun productDao(): ProductDao
}