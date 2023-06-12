package com.esrcitazione.hotelhub

data class Booking(
    val id: Int,
    val userId: Int,
    val roomId: Int,
    val checkInDate: String,
    val checkOutDate: String,
    var isSelected: Boolean = false
)
