package com.esrcitazione.hotelhub.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.esrcitazione.hotelhub.db.ClientNetwork
import com.esrcitazione.hotelhub.R
import com.esrcitazione.hotelhub.utils.ServizioInCamera
import com.esrcitazione.hotelhub.databinding.ItemServizioInCameraBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ServizioInCameraAdapter(
    private val servizioInCameraList: MutableList<ServizioInCamera>,
    private val context: Context
) : RecyclerView.Adapter<ServizioInCameraAdapter.ServizioInCameraViewHolder>() {

    inner class ServizioInCameraViewHolder(private val binding: ItemServizioInCameraBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(servizioInCamera: ServizioInCamera) {
            binding.textViewNumeroCamera.text = servizioInCamera.numeroCamera.toString()
            binding.textViewServizio.text = servizioInCamera.servizio
            binding.buttonServito.setOnClickListener {
                removeServizio(servizioInCamera)
            }
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

    private fun removeServizio(servizioInCamera: ServizioInCamera) {
        val servizioId = servizioInCamera.id

        // Crea la query per rimuovere il servizio dal database
        val query = "DELETE FROM servizio_in_camera WHERE id_sc = $servizioId"

        // si effettua la chiaamata Retrofit per rimuovere l'ondinazione dal database
        ClientNetwork.retrofit.remove(query).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    // Rimuovi il servizio dall'elenco e aggiorna l'adapter
                    servizioInCameraList.remove(servizioInCamera)
                    notifyDataSetChanged()
                } else {
                    showToast(context.getString(R.string.error_remove_service))
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                showToast(context.getString(R.string.error_remove_service))
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
