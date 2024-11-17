package com.erstuu.app.story.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stories")
data class Stories (
    @PrimaryKey
    val id: String,
    val photoUrl: String? = null,
    val createdAt: String? = null,
    val name: String? = null,
    val description: String? = null,
    val lon: Double? = null,
    val lat: Double? = null
)