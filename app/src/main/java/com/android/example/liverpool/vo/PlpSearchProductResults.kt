package com.android.example.liverpool.vo

import androidx.room.Entity
import androidx.room.TypeConverters
import com.android.example.liverpool.db.LiverpoolTypeConverters

@Entity(primaryKeys = ["query"])
@TypeConverters(LiverpoolTypeConverters::class)
data class PlpSearchProductResults(
        val query: String,
        val totalCount: Int,
        val next: Int?
)
