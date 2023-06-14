package com.esrcitazione.hotelhub

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
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

        binding.logout2.setOnClickListener {
            val dbHelper = DatabaseHelper(this)
            dbHelper.deleteTabella()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.buttonCheckIn.setOnClickListener{

            val selectedBookings = ArrayList(bookingAdapter.selectedBookings)
            for (booking in selectedBookings) {
                val query = "UPDATE prenotazioni SET checkin=true WHERE prenotazioni.id=" + booking.id

                ClientNetwork.retrofit.update(query).enqueue(object : Callback<JsonObject> {
                    override fun onResponse(
                        call: Call<JsonObject>,
                        response: Response<JsonObject>
                    ) {
                        if (response.isSuccessful) {
                            showToast("Chek In confermato")

                            // Rimuovi l'elemento dalla lista dopo aver completato la richiesta di eliminazione
                            bookingAdapter.bookings.remove(booking)
                            bookingAdapter.selectedBookings.remove(booking)
                            bookingAdapter.notifyDataSetChanged()
                        } else {
                            // Gestisci la risposta non riuscita
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        // Gestisci l'errore di comunicazione
                    }
                })
            }


            // Dopo aver eliminato le prenotazioni, puoi pulire la lista delle prenotazioni selezionate
            bookingAdapter.selectedBookings.clear()

            // Aggiorna l'aspetto visivo degli elementi selezionati nel RecyclerView
            bookingAdapter.notifyDataSetChanged()

        }

        binding.buttonElimina.setOnClickListener {
            val selectedBookings = ArrayList(bookingAdapter.selectedBookings)

            for (booking in selectedBookings) {
                val query = "DELETE from prenotazioni where prenotazioni.id=" + booking.id

                ClientNetwork.retrofit.remove(query).enqueue(object : Callback<JsonObject> {
                    override fun onResponse(
                        call: Call<JsonObject>,
                        response: Response<JsonObject>
                    ) {
                        if (response.isSuccessful) {
                            showToast("Prenotazioni Selezionate Eliminate")

                            // Rimuovi l'elemento dalla lista dopo aver completato la richiesta di eliminazione
                            bookingAdapter.bookings.remove(booking)
                            bookingAdapter.selectedBookings.remove(booking)
                            bookingAdapter.notifyDataSetChanged()
                        } else {
                            showToast("Qualcosa è andato Storto")
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        showToast("Qualcosa è andato Storto")
                    }
                })
            }



            bookingAdapter.selectedBookings.clear()


            bookingAdapter.notifyDataSetChanged()


        }
    }
    private fun setupRecyclerView() {
        bookingAdapter = BookingAdapter(mutableListOf())
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = bookingAdapter
    }

    private fun getBookingsFromDatabase() {
        val query ="SELECT p.id,u.nome, u.cognome, s.numero_stanza, p.data_check_in, p.data_check_out " +
                "FROM utenti AS u, prenotazioni AS p, stanze AS s " +
                "WHERE u.id = p.id_utente " +
                "AND p.id_stanza = s.id AND p.checkin=false"


        ClientNetwork.retrofit.select(query).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val resultSet = response.body()?.get("queryset") as JsonArray
                    if (resultSet.asJsonArray.size() > 0) {
                        val jsonObject = response.body()
                        val bookings = parseBookingsFromJson(jsonObject)
                        showBookings(bookings)
                    }// Dobbiamo aggiungere un else forse?
                }else {
                    showToast("Qualcosa è andato Storto")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                showToast("Qualcosa è andato Storto")
            }
        })
    }

    private fun parseBookingsFromJson(jsonObject: JsonObject?): List<Booking> {
        val bookings = mutableListOf<Booking>()

        if (jsonObject != null) {
            val resultSet = jsonObject.getAsJsonArray("queryset")
            for (bookingElement in resultSet) {
                val bookingObject = bookingElement.asJsonObject

                val id= bookingObject.get("id").asInt
                val nome = bookingObject.get("nome").asString
                val cognome = bookingObject.get("cognome").asString
                val numeroStanza = bookingObject.get("numero_stanza").asInt
                val dataCheckIn = bookingObject.get("data_check_in").asString
                val dataCheckOut = bookingObject.get("data_check_out").asString
                val booking = Booking(id, nome, cognome, numeroStanza, dataCheckIn , dataCheckOut)
                bookings.add(booking)
            }
        }

        return bookings
    }


    private fun showBookings(bookings: List<Booking>) {
        if (bookings.isNotEmpty()) {
            binding.textNoBookings.visibility = View.GONE
            bookingAdapter.bookings = bookings.toMutableList()
            bookingAdapter.notifyDataSetChanged()
        } else {
            binding.textNoBookings.visibility = View.VISIBLE
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
