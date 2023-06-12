package com.esrcitazione.hotelhub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.esrcitazione.hotelhub.databinding.ActivityHomeAdminBinding
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeAdminBinding
    private lateinit var bookingAdapter: BookingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        getBookingsFromDatabase()

        binding.buttonDeleteSelectedBookings.setOnClickListener {
            for (booking in bookingAdapter.selectedBookings) {
                // Esegui l'azione desiderata per ogni prenotazione selezionata
                // Ad esempio, rimuovi la prenotazione dal database
            }

            // Dopo aver eliminato le prenotazioni, puoi pulire la lista delle prenotazioni selezionate
            bookingAdapter.selectedBookings.clear()

            // Aggiorna l'aspetto visivo degli elementi selezionati nel RecyclerView
            bookingAdapter.notifyDataSetChanged()
        }


    }

    private fun setupRecyclerView() {
        bookingAdapter = BookingAdapter(emptyList())
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = bookingAdapter
    }

    private fun getBookingsFromDatabase() {
        val query = "SELECT * FROM prenotazioni"

        ClientNetwork.retrofit.select(query).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val jsonObject = response.body()
                    val bookings = parseBookingsFromJson(jsonObject)
                    showBookings(bookings)
                } else {
                    // Gestisci la risposta non riuscita
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                // Gestisci l'errore di comunicazione
            }
        })
    }

    private fun parseBookingsFromJson(jsonObject: JsonObject?): List<Booking> {
        val bookings = mutableListOf<Booking>()

        if (jsonObject != null) {
            val resultSet = jsonObject.getAsJsonArray("queryset")
            for (bookingElement in resultSet) {
                val bookingObject = bookingElement.asJsonObject

                val id = bookingObject.get("id").asInt
                val userId = bookingObject.get("id_utente").asInt
                val roomId = bookingObject.get("id_stanza").asInt
                val checkInDate = bookingObject.get("data_check_in").asString
                val checkOutDate = bookingObject.get("data_check_out").asString
                // Aggiungi altre proprietà della prenotazione in base al tuo database

                val booking = Booking(id, userId, roomId, checkInDate, checkOutDate)
                bookings.add(booking)
            }
        }

        return bookings
    }


    private fun showBookings(bookings: List<Booking>) {
        if (bookings.isNotEmpty()) {
            binding.textNoBookings.visibility = View.GONE
            bookingAdapter.bookings = bookings
            bookingAdapter.notifyDataSetChanged()
        } else {
            binding.textNoBookings.visibility = View.VISIBLE
        }
    }

}
