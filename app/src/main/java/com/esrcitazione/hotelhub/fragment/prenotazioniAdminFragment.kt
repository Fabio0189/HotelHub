package com.esrcitazione.hotelhub.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.esrcitazione.hotelhub.utils.Booking
import com.esrcitazione.hotelhub.adapter.BookingAdapter
import com.esrcitazione.hotelhub.db.ClientNetwork
import com.esrcitazione.hotelhub.databinding.FragmentPrenotazioniAdminBinding
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class prenotazioniAdminFragment : Fragment() {

    private lateinit var binding: FragmentPrenotazioniAdminBinding
    private lateinit var bookingAdapter: BookingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPrenotazioniAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        getBookingsFromDatabase()
        binding.buttonCheckIn.setOnClickListener {
            handleCheckIn()
        }

        binding.buttonElimina.setOnClickListener {
            handleElimina()
        }

    }

    private fun setupRecyclerView() {
        bookingAdapter = BookingAdapter(mutableListOf())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = bookingAdapter
    }

    private fun getBookingsFromDatabase() {
        val query = "SELECT p.id_p, u.nome, u.cognome, s.numero_stanza, p.data_check_in, p.data_check_out " +
                "FROM utenti AS u, prenotazioni AS p, stanze AS s " +
                "WHERE u.id_u = p.id_utente " +
                "AND p.id_stanza = s.id_s AND p.checkin=false"

        ClientNetwork.retrofit.select(query).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val resultSet = response.body()?.getAsJsonArray("queryset")
                    if (resultSet != null && resultSet.size() > 0) {
                        val bookings = parseBookingsFromJson(resultSet)
                        showBookings(bookings)
                    } else {
                        showNoBookings()
                    }
                } else {
                    showToast("Qualcosa è andato storto")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                showToast("Qualcosa è andato storto")
            }
        })
    }

    private fun parseBookingsFromJson(resultSet: JsonArray): List<Booking> {
        val bookings = mutableListOf<Booking>()

        for (bookingElement in resultSet) {
            val bookingObject = bookingElement.asJsonObject

            val id = bookingObject.get("id_p").asInt
            val nome = bookingObject.get("nome").asString
            val cognome = bookingObject.get("cognome").asString
            val numeroStanza = bookingObject.get("numero_stanza").asInt
            val dataCheckIn = bookingObject.get("data_check_in").asString
            val dataCheckOut = bookingObject.get("data_check_out").asString
            val booking = Booking(id, nome, cognome, numeroStanza, dataCheckIn, dataCheckOut)
            bookings.add(booking)
        }

        return bookings
    }

    private fun showBookings(bookings: List<Booking>) {
        if (bookings.isNotEmpty()) {
            binding.textNoBookings.visibility = View.GONE
            bookingAdapter.bookings = bookings.toMutableList()
            bookingAdapter.notifyDataSetChanged()
        } else {
            showNoBookings()
        }
    }

    private fun showNoBookings() {
        binding.textNoBookings.visibility = View.VISIBLE
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun handleCheckIn() {
        val selectedBookings = ArrayList(bookingAdapter.selectedBookings)

        for (booking in selectedBookings) {
            val query = "UPDATE prenotazioni SET checkin=true WHERE prenotazioni.id_p=${booking.id}"

            ClientNetwork.retrofit.update(query).enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        showToast("Check-in confermato")

                        // Rimuovie l'elemento dalla lista dopo il check-in, ma non dal database
                        bookingAdapter.bookings.remove(booking)
                        bookingAdapter.selectedBookings.remove(booking)
                        bookingAdapter.notifyDataSetChanged()
                    } else {
                        showToast("Gestisci la risposta non riuscita")
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    showToast("Gestisci l'errore di comunicazione")
                }
            })
        }

        bookingAdapter.selectedBookings.clear()

        bookingAdapter.notifyDataSetChanged()
    }

    private fun handleElimina() {
        val selectedBookings = ArrayList(bookingAdapter.selectedBookings)

        for (booking in selectedBookings) {
            val query = "DELETE FROM prenotazioni WHERE prenotazioni.id_p=${booking.id}"

            ClientNetwork.retrofit.remove(query).enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        showToast("Prenotazioni selezionate eliminate")

                        // Rimuove l'elemento
                        bookingAdapter.bookings.remove(booking)
                        bookingAdapter.selectedBookings.remove(booking)
                        bookingAdapter.notifyDataSetChanged()
                    } else {
                        showToast("Qualcosa è andato storto")
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    showToast("Qualcosa è andato storto")
                }
            })
        }

        bookingAdapter.selectedBookings.clear()

        bookingAdapter.notifyDataSetChanged()
    }
}
