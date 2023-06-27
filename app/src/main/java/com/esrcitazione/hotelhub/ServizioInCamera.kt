package com.esrcitazione.hotelhub

data class ServizioInCamera(
    val id: Int,
    val idPrenotazione: Int,
    val idUtente: Int,
    val numeroCamera: Int,
    val servizio: String
)

