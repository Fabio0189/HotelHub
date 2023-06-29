package com.esrcitazione.hotelhub

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esrcitazione.hotelhub.databinding.FragmentRecensioniBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class RecensioniFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var db: DatabaseHelper
    private lateinit var binding: FragmentRecensioniBinding
    private lateinit var adapter: RecensioniAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecensioniBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonLeaveReview = view.findViewById<Button>(R.id.button_lascia_recensione)
        buttonLeaveReview.setOnClickListener {
            showLeaveReviewPopup()
        }
// Inizializza la RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recensioni_db)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

// Crea un'istanza dell'adapter e assegnalo alla RecyclerView
        adapter = RecensioniAdapter(emptyList()) // Passa una lista vuota per ora
        recyclerView.adapter = adapter


// Recupera le recensioni dal database e aggiornale nell'adapter
        getRecensioniFromDatabase(adapter)

        calcolaMediaRecensioni()
        calcolaPercentualiRecensioni()
    }

    private fun showLeaveReviewPopup() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.lascia_recensione, null)
        db = DatabaseHelper(requireContext())

        val alertDialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Lascia una recensione")
            .setPositiveButton("Invia") { dialog, which ->
                val ratingBar = dialogView.findViewById<RatingBar>(R.id.dialog_rating_bar)
                val commentEditText = dialogView.findViewById<EditText>(R.id.dialog_comment)

                val id_u = db.getId()
                val rating = ratingBar.rating.toDouble()
                val comment = commentEditText.text.toString()

                inserisciRecensione(id_u, rating, comment)

                dialog.dismiss()
            }
            .setNegativeButton("Torna indietro") { dialog, which ->
                dialog.dismiss()
            }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun inserisciRecensione(id: Int, rating: Double, commento: String) {
        val insertQuery = "INSERT INTO recensioni (id_utente_recensione, punteggio, commento) VALUES ('$id','$rating','$commento')"

        ClientNetwork.retrofit.insert(insertQuery).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    showToast("Recensione salvata con successo!")

                    getRecensioniFromDatabase(adapter)
                    calcolaMediaRecensioni()
                    calcolaPercentualiRecensioni()
                } else {
                    showToast("Recensione non salvata. Riprova.")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                showToast("Errore di connessione. Riprova.")
            }
        })
    }


    private fun calcolaMediaRecensioni() {
        val ratingBarAverage = view?.findViewById<RatingBar>(R.id.rating_bar_average)
        val mediaTextView = view?.findViewById<TextView>(R.id.text_average_rating_value)
        val query = "SELECT AVG(punteggio) AS average_rating FROM recensioni"

        ClientNetwork.retrofit.select(query).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val result = response.body()?.getAsJsonArray("queryset")
                    if (result != null && result.size() > 0) {
                        val averageRating = result[0].asJsonObject["average_rating"].asFloat
                        ratingBarAverage?.rating = averageRating
                        mediaTextView?.text = averageRating.toString()
                    }
                } else {
                    showToast("Errore nel calcolo della media delle recensioni")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                showToast("Errore di connessione. Riprova.")
            }
        })
    }


    private fun calcolaPercentualiRecensioni() {
        val query = "SELECT " +
                "(SUM(IF(punteggio = 5, 1, 0)) * 100 / COUNT(*)) AS percentuale_cinque_stelle, " +
                "(SUM(IF(punteggio = 4, 1, 0)) * 100 / COUNT(*)) AS percentuale_quattro_stelle, " +
                "(SUM(IF(punteggio = 3, 1, 0)) * 100 / COUNT(*)) AS percentuale_tre_stelle, " +
                "(SUM(IF(punteggio = 2, 1, 0)) * 100 / COUNT(*)) AS percentuale_due_stelle, " +
                "(SUM(IF(punteggio = 1, 1, 0)) * 100 / COUNT(*)) AS percentuale_una_stella " +
                "FROM recensioni"

        ClientNetwork.retrofit.select(query).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val result = response.body()?.getAsJsonArray("queryset")
                    if (result != null && result.size() > 0) {
                        val percentualeCinqueStelle = result[0].asJsonObject["percentuale_cinque_stelle"].asDouble
                        val percentualeQuattroStelle = result[0].asJsonObject["percentuale_quattro_stelle"].asDouble
                        val percentualeTreStelle = result[0].asJsonObject["percentuale_tre_stelle"].asDouble
                        val percentualeDueStelle = result[0].asJsonObject["percentuale_due_stelle"].asDouble
                        val percentualeUnaStella = result[0].asJsonObject["percentuale_una_stella"].asDouble

                        binding.progressBar5.progress = percentualeCinqueStelle.toInt()
                        binding.progressBar4.progress = percentualeQuattroStelle.toInt()
                        binding.progressBar3.progress = percentualeTreStelle.toInt()
                        binding.progressBar2.progress = percentualeDueStelle.toInt()
                        binding.progressBar1.progress = percentualeUnaStella.toInt()
                    }
                } else {
                    showToast("Errore nel calcolo delle percentuali.")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                showToast("Errore di connessione. Riprova.")
            }
        })
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecensioniFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    private fun getRecensioniFromDatabase(adapter: RecensioniAdapter) {
        val query = "SELECT recensioni.*, utenti.nome, utenti.cognome " +
                "FROM recensioni, utenti " +
                "WHERE recensioni.id_utente_recensione = utenti.id_u"

        ClientNetwork.retrofit.select(query).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val result = response.body()?.getAsJsonArray("queryset")
                    if (result != null && result.size() > 0) {
                        val recensioniList = mutableListOf<Recensioni>()

                        for (i in 0 until result.size()) {
                            val recensioneObject = result[i].asJsonObject
                            val idUtenteRecensione = recensioneObject["id_utente_recensione"].asInt
                            val punteggio = recensioneObject["punteggio"].asDouble
                            val commento = recensioneObject["commento"].asString
                            val nome = recensioneObject["nome"].asString
                            val cognome = recensioneObject["cognome"].asString

                            val recensione = Recensioni(idUtenteRecensione, punteggio, commento, nome, cognome)
                            recensioniList.add(recensione)
                        }

                        // Aggiorna la lista delle recensioni nell'adapter
                        adapter.updateRecensioniList(recensioniList)
                    }
                } else {
                    showToast("Errore nel recupero delle recensioni")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                showToast("Errore di connessione. Riprova.")
            }
        })
    }



}
