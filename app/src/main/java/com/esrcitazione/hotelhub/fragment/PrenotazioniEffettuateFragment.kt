package com.esrcitazione.hotelhub.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.esrcitazione.hotelhub.db.ClientNetwork
import com.esrcitazione.hotelhub.db.DatabaseHelper
import com.esrcitazione.hotelhub.utils.PrenotazioniEffettuate
import com.esrcitazione.hotelhub.adapter.PrenotazioniEffettuateAdapter
import com.esrcitazione.hotelhub.databinding.FragmentPrenotazioniEffettuateBinding
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PrenotazioniEffettuateFragment : Fragment() {

    private lateinit var binding: FragmentPrenotazioniEffettuateBinding
    private lateinit var adapter: PrenotazioniEffettuateAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var emptyTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPrenotazioniEffettuateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = PrenotazioniEffettuateAdapter()
        binding.recyclerPrenotazioniEffettuate.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerPrenotazioniEffettuate.adapter = adapter
        emptyTextView = binding.textNoPrenotazioni
        databaseHelper = DatabaseHelper(requireContext())


        val prenotazioniList = getPrenotazioniEffettuateFromDatabase()
        if (prenotazioniList.isNotEmpty()) {
            adapter.setPrenotazioni(prenotazioniList)
        } else {
            showEmptyView()
        }
    }

    private fun getPrenotazioniEffettuateFromDatabase(): List<PrenotazioniEffettuate> {
        val id = databaseHelper.getId()

        val query = "SELECT p.id_p, u.nome, u.cognome, s.numero_stanza, p.data_check_in, p.data_check_out " +
                "FROM prenotazioni AS p, utenti AS u, stanze AS s " +
                "WHERE p.id_utente = u.id_u " +
                "AND p.id_stanza = s.id_s " +
                "AND u.id_u = $id"

        val prenotazioniList = mutableListOf<PrenotazioniEffettuate>()

        ClientNetwork.retrofit.select(query).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val resultSet = response.body()?.get("queryset") as JsonArray
                    val prenotazioniListFromJson = parsePrenotazioniFromJson(resultSet)
                    prenotazioniList.addAll(prenotazioniListFromJson)
                    if (prenotazioniList.isNotEmpty()) {
                        adapter.setPrenotazioni(prenotazioniList)
                    } else {
                        showEmptyView()
                    }
                } else {
                    showToast("Qualcosa è andato storto durante la richiesta delle prenotazioni.")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                showToast("Errore di comunicazione durante la richiesta delle prenotazioni.")
                showEmptyView()
            }
        })

        return prenotazioniList
    }

    private fun parsePrenotazioniFromJson(resultSet: JsonArray): List<PrenotazioniEffettuate> {
        val prenotazioniList = mutableListOf<PrenotazioniEffettuate>()

        for (bookingElement in resultSet) {
            val bookingObject = bookingElement.asJsonObject

            val nome = bookingObject.get("nome").asString
            val cognome = bookingObject.get("cognome").asString
            val numeroStanza = bookingObject.get("numero_stanza").asInt
            val dataCheckIn = bookingObject.get("data_check_in").asString
            val dataCheckOut = bookingObject.get("data_check_out").asString

            val prenotazioneEffettuata =
                PrenotazioniEffettuate(nome, cognome, numeroStanza, dataCheckIn, dataCheckOut)
            prenotazioniList.add(prenotazioneEffettuata)
        }

        return prenotazioniList
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showEmptyView() {
        emptyTextView.visibility = View.VISIBLE
    }
}
