package com.esrcitazione.hotelhub.utils

data class Booking(
    val id: Int,
    val nome: String,
    val cognome: String,
    val numeroStanza: Int,
    val checkInDate: String,
    val checkOutDate: String,
    var isSelected: Boolean = false
)
