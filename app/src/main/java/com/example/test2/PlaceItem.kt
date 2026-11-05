package com.example.test2

import java.util.UUID

data class PlaceItem(
    val id: String = UUID.randomUUID().toString(),
    var name: String,
    val latitude: Double,
    val longitude: Double,
    var isDone: Boolean = false,
    val isFoodStop: Boolean = false,
    val isRestStop: Boolean = false
)
