package com.esrcitazione.hotelhub

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.esrcitazione.hotelhub.Booking
import com.esrcitazione.hotelhub.R

class BookingAdapter(var bookings: List<Booking>) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]
        holder.bind(booking)
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

        fun bind(booking: Booking) {
            textBookingId.text = booking.id.toString()
            textUserId.text = booking.userId.toString()
            textRoomId.text = booking.roomId.toString()
            textCheckInDate.text = booking.checkInDate
            textCheckOutDate.text = booking.checkOutDate
        }
    }
}