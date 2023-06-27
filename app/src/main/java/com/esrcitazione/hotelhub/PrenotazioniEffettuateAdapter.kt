package com.esrcitazione.hotelhub

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PrenotazioniEffettuateAdapter : RecyclerView.Adapter<PrenotazioniEffettuateAdapter.PrenotazioneViewHolder>() {

    private var prenotazioniList: List<PrenotazioniEffettuate> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrenotazioneViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_prenotazioni_effettuate, parent, false)
        return PrenotazioneViewHolder(view)
    }

    override fun onBindViewHolder(holder: PrenotazioneViewHolder, position: Int) {
        val prenotazione = prenotazioniList[position]
        holder.bind(prenotazione)
    }

    override fun getItemCount(): Int {
        return prenotazioniList.size
    }

    fun setPrenotazioni(prenotazioni: List<PrenotazioniEffettuate>) {
        prenotazioniList = prenotazioni
        notifyDataSetChanged()
    }

    inner class PrenotazioneViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textNome: TextView = itemView.findViewById(R.id.textNome)
        private val textCognome: TextView = itemView.findViewById(R.id.textCognome)
        private val textNumeroStanza: TextView = itemView.findViewById(R.id.textNumeroStanza)
        private val textCheckInDate: TextView = itemView.findViewById(R.id.textCheckInDate)
        private val textCheckOutDate: TextView = itemView.findViewById(R.id.textCheckOutDate)

        fun bind(prenotazione: PrenotazioniEffettuate) {
            textNome.text = prenotazione.nome
            textCognome.text = prenotazione.cognome
            textNumeroStanza.text = prenotazione.numeroStanza.toString()
            textCheckInDate.text = prenotazione.checkInDate
            textCheckOutDate.text = prenotazione.checkOutDate
        }
    }
}
