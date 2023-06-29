package com.esrcitazione.hotelhub

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.esrcitazione.hotelhub.databinding.FragmentServizioAdminBinding
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ServizioAdminFragment : Fragment() {
    private lateinit var binding: FragmentServizioAdminBinding
    private lateinit var servizioAdapter: ServizioInCameraAdapter
    private val servizioInCameraList: MutableList<ServizioInCamera> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentServizioAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        fetchServizioPrenotazioni()
    }

    private fun setupRecyclerView() {
        servizioAdapter = ServizioInCameraAdapter(servizioInCameraList, requireContext())
        binding.recyclerViewServizioAdmin.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewServizioAdmin.adapter = servizioAdapter
    }


    private fun fetchServizioPrenotazioni() {
        val query = "SELECT * FROM servizio_in_camera"
        ClientNetwork.retrofit.select(query).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null) {
                        val resultSet = result.getAsJsonArray("queryset")
                        if (resultSet.size() > 0) {
                            val servizioInCameraList = parseServizioInCameraFromJson(resultSet)
                            showServizioInCamera(servizioInCameraList)
                        } else {
                            showNoServizioInCamera()
                        }
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


    private fun parseServizioInCameraFromJson(resultSet: JsonArray): List<ServizioInCamera> {
        val servizioInCameraList = mutableListOf<ServizioInCamera>()
        for (item in resultSet) {
            val servizioInCameraObject = item.asJsonObject
            val id = servizioInCameraObject.get("id_sc").asInt
            val idPrenotazione = servizioInCameraObject.get("id_p_sc").asInt
            val idUtente = servizioInCameraObject.get("id_u_sc").asInt
            val numeroCamera = servizioInCameraObject.get("numero_camera").asInt
            val servizio = servizioInCameraObject.get("servizio").asString

            val servizioInCamera = ServizioInCamera(id, idPrenotazione, idUtente, numeroCamera, servizio)
            servizioInCameraList.add(servizioInCamera)
        }
        return servizioInCameraList
    }

    private fun showServizioInCamera(servizioInCameraList: List<ServizioInCamera>) {
        binding.recyclerViewServizioAdmin.visibility = View.VISIBLE
        binding.textViewNoPrenotazioni.visibility = View.GONE
        servizioAdapter.setData(servizioInCameraList)
    }

    private fun showNoServizioInCamera() {
        binding.recyclerViewServizioAdmin.visibility = View.GONE
        binding.textViewNoPrenotazioni.visibility = View.VISIBLE
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
