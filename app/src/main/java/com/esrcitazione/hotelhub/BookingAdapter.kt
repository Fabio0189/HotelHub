package com.esrcitazione.hotelhub

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.esrcitazione.hotelhub.Booking
import com.esrcitazione.hotelhub.R
import android.graphics.Color


class BookingAdapter(var bookings:  MutableList<Booking>) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    val selectedBookings = mutableListOf<Booking>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_booking, parent, false)
        val viewHolder = BookingViewHolder(view)

        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            val booking = bookings[position]

            // Inverti lo stato di selezione
            booking.isSelected = !booking.isSelected

            // Aggiorna l'aspetto dell'elemento
            viewHolder.bind(booking, booking.isSelected)

            // Aggiungi o rimuovi la prenotazione selezionata dalla lista delle prenotazioni selezionate
            if (booking.isSelected) {
                selectedBookings.add(booking)
            } else {
                selectedBookings.remove(booking)
            }
        }

        return viewHolder
    }


    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]
        holder.bind(booking, booking.isSelected)
    }

    override fun getItemCount(): Int {
        return bookings.size
    }

    inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textBookingId: TextView = itemView.findViewById(R.id.textBookingId)
        private val textUserId: TextView = itemView.findViewById(R.id.textUserId)
        private val textRoomId: TextView = itemView.findViewById(R.id.textRoomId)
        private val textCheckInDate: TextView = itemView.findViewById(R.id.textCheckInDate)
        private val textCheckOutDate: TextView = itemView.findViewById(R.id.textCheckOutDate)

        fun bind(booking: Booking, isSelected: Boolean) {
            textBookingId.text = booking.nome.toString()
            textUserId.text = booking.cognome.toString()
            textRoomId.text = booking.numeroStanza.toString()
            textCheckInDate.text = booking.checkInDate
            textCheckOutDate.text = booking.checkOutDate

            if (isSelected) {
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.selezionatoWhite))
            } else {
                itemView.setBackgroundColor(Color.TRANSPARENT)
            }
        }

    }
}