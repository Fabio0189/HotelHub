package com.esrcitazione.hotelhub

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.esrcitazione.hotelhub.databinding.ItemServizioInCameraBinding

class ServizioInCameraAdapter(private val servizioInCameraList: MutableList<ServizioInCamera>) :
    RecyclerView.Adapter<ServizioInCameraAdapter.ServizioInCameraViewHolder>() {

    inner class ServizioInCameraViewHolder(private val binding: ItemServizioInCameraBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(servizioInCamera: ServizioInCamera) {
            binding.textViewNumeroCamera.text = servizioInCamera.numeroCamera.toString()
            binding.textViewServizio.text = servizioInCamera.servizio
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServizioInCameraViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemServizioInCameraBinding.inflate(inflater, parent, false)
        return ServizioInCameraViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServizioInCameraViewHolder, position: Int) {
        val servizioInCamera = servizioInCameraList[position]
        holder.bind(servizioInCamera)
    }

    override fun getItemCount(): Int {
        return servizioInCameraList.size
    }

    fun setData(data: List<ServizioInCamera>) {
        servizioInCameraList.clear()
        servizioInCameraList.addAll(data)
        notifyDataSetChanged()
    }
}
