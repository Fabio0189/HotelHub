package com.esrcitazione.hotelhub

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecensioniAdapter(private var recensioniList: List<Recensioni>) : RecyclerView.Adapter<RecensioniAdapter.RecensioniViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecensioniViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_recensioni, parent, false)
        return RecensioniViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecensioniViewHolder, position: Int) {
        val recensione = recensioniList[position]
        holder.txtNome.text = recensione.nome
        holder.txtCognome.text = recensione.cognome
        holder.ratingBarPunteggio.rating = recensione.punteggio.toFloat()
        holder.txtCommento.text = recensione.commento
    }

    override fun getItemCount(): Int {
        return recensioniList.size
    }

    inner class RecensioniViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNome: TextView = itemView.findViewById(R.id.txtNome)
        val txtCognome: TextView = itemView.findViewById(R.id.txtCognome)
        val ratingBarPunteggio: RatingBar = itemView.findViewById(R.id.punteggio)
        val txtCommento: TextView = itemView.findViewById(R.id.txtCommento)
    }

    fun updateRecensioniList(recensioniList: List<Recensioni>) {
        this.recensioniList = recensioniList
        notifyDataSetChanged()
    }
}
