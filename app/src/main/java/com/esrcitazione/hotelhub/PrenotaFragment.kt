package com.esrcitazione.hotelhub

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esrcitazione.hotelhub.databinding.FragmentPrenotaBinding
import com.esrcitazione.hotelhub.databinding.ItemCameraBinding
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import androidx.fragment.app.Fragment

private lateinit var db: DatabaseHelper

class PrenotaFragment : Fragment() {
    private lateinit var binding: FragmentPrenotaBinding
    private lateinit var cameraSelected: String
    private var fatturazionePremuta = false
    private var dataCheckIn: Calendar = Calendar.getInstance()
    private var dataCheckOut: Calendar = Calendar.getInstance()
    private var isCheckInSelected = false
    private var isCheckOutSelected = false
    private var idStanza: Int = 0
    data class Camera(val tipo: String, val immagini: List<Int>, val prezzo: Double)

    private lateinit var camere: List<Camera>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPrenotaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = DatabaseHelper(requireContext())

        camere = listOf(
            Camera(requireContext().getString(R.string.camera_type_1), listOf(R.drawable.camerasingola1, R.drawable.camerasingola2, R.drawable.camerasingola3, R.drawable.camerasingola4), 50.0),
            Camera(requireContext().getString(R.string.camera_type_2), listOf(R.drawable.cameradoppia1, R.drawable.cameradoppia2, R.drawable.cameradoppia3, R.drawable.cameradoppia4), 100.0),
            Camera(requireContext().getString(R.string.camera_type_3), listOf(R.drawable.camerafamiliare1, R.drawable.camerafamiliare2, R.drawable.camerafamiliare3, R.drawable.camerafamiliare4), 200.0)
        )

        camere.forEach { camera ->
            val recyclerView = RecyclerView(requireContext()).apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = CameraAdapter(camera)
            }
            binding.layoutRecyclerViews.addView(recyclerView)
        }

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.camera_types,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTipoCamera.adapter = adapter
        binding.spinnerTipoCamera.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                binding.buttonFatturazione.visibility = View.GONE
                binding.textViewPrezzo.visibility = View.GONE
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                cameraSelected = adapter.getItem(position).toString()
                binding.buttonFatturazione.visibility = if (!fatturazionePremuta) View.VISIBLE else View.GONE

                val prezzoCamera = camere[position].prezzo
                val priceLabel = getString(R.string.priceLabel)
                binding.textViewPrezzo.text = "$priceLabel $prezzoCamera €"
                binding.textViewPrezzo.visibility = View.VISIBLE
                binding.buttonFatturazione.isEnabled = isCheckInSelected && isCheckOutSelected
                if (fatturazionePremuta) {
                    binding.buttonFatturazione.isEnabled = false
                    binding.buttonFatturazione.alpha = 0.5f
                }
            }
        }

        binding.editTextDataCheckIn.setOnClickListener {
            showDatePickerDialog(false) { calendar ->
                dataCheckIn = calendar
                binding.editTextDataCheckIn.setText(formatDate(calendar))
                isCheckInSelected = true
                if (isCheckOutSelected) {
                    if (dataCheckIn.timeInMillis >= dataCheckOut.timeInMillis) {
                        isCheckOutSelected = false
                        binding.editTextDataCheckOut.text.clear()
                        showToast(getString(R.string.toast_checkin_cannot_be_after_checkout))
                    }
                }
                binding.buttonFatturazione.isEnabled = isCheckInSelected && isCheckOutSelected
            }
        }

        binding.editTextDataCheckOut.setOnClickListener {
            if (!isCheckInSelected) {
                showToast(getString(R.string.toast_select_checkin_first))
            } else {
                showDatePickerDialog(true) { calendar ->
                    dataCheckOut = calendar
                    binding.editTextDataCheckOut.setText(formatDate(calendar))
                    isCheckOutSelected = true
                    binding.buttonFatturazione.isEnabled = isCheckInSelected && isCheckOutSelected
                }
            }
        }

        binding.buttonFatturazione.setOnClickListener {
            val tipoCamera = binding.spinnerTipoCamera.selectedItemPosition + 1
            ClientNetwork.retrofit.select("""
                SELECT COUNT(*) AS count, id_s 
                FROM stanze
                WHERE capacita = $tipoCamera
                  AND NOT EXISTS (
                    SELECT *
                    FROM prenotazioni
                    WHERE id_stanza = stanze.id_s
                      AND (
                        (data_check_in <= '${formatDate(dataCheckOut)}' AND data_check_out >= '${formatDate(dataCheckOut)}')
                        OR (data_check_in <= '${formatDate(dataCheckIn)}' AND data_check_out >= '${formatDate(dataCheckIn)}')
                        OR (data_check_in >= '${formatDate(dataCheckIn)}' AND data_check_out <= '${formatDate(dataCheckOut)}')
                      )
                  )
                GROUP BY id_s
            """).enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful && response.body() != null) {
                        val result = response.body()?.get("queryset") as JsonArray
                        if (result.asJsonArray.size() > 0) {
                            binding.textViewNome.visibility = View.VISIBLE
                            binding.editTextNome.visibility = View.VISIBLE
                            binding.textViewCognome.visibility = View.VISIBLE
                            binding.editTextCognome.visibility = View.VISIBLE
                            binding.textViewNumeroCarta.visibility = View.VISIBLE
                            binding.editTextNumeroCarta.visibility = View.VISIBLE
                            binding.textViewCvv.visibility = View.VISIBLE
                            binding.editTextCvv.visibility = View.VISIBLE
                            binding.buttonConferma.visibility = View.VISIBLE
                            binding.buttonFatturazione.visibility = View.GONE
                            binding.buttonFatturazione.isEnabled = false
                            fatturazionePremuta = true
                            idStanza = result.asJsonArray[0].asJsonObject.get("id_s").asInt
                        } else {
                            showToast(getString(R.string.toast_room_unavailable))
                        }
                    } else {
                        showToast(getString(R.string.toast_server_communication_error))
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    showToast(getString(R.string.toast_connection_error))
                }
            })
        }

        binding.buttonConferma.setOnClickListener {
            val tipoCamera = binding.spinnerTipoCamera.selectedItemPosition + 1
            val dataCheckIn = binding.editTextDataCheckIn.text.toString()
            val dataCheckOut = binding.editTextDataCheckOut.text.toString()
            val nome = binding.editTextNome.text.toString()
            val cognome = binding.editTextCognome.text.toString()
            val numeroCarta = binding.editTextNumeroCarta.text.toString()
            val cvv = binding.editTextCvv.text.toString()
            if (nome.isNotBlank() && cognome.isNotBlank() && numeroCarta.length == 16 && cvv.length == 3) {
                val idUtente = db.getId()
                effettuaPrenotazione(idUtente, dataCheckIn, dataCheckOut)
            } else {
                showToast(getString(R.string.toast_payment_data_invalid))
            }
        }
    }

    private fun effettuaPrenotazione(idUtente: Int, dataCheckIn: String, dataCheckOut: String) {
        val formattedCheckIn = formatDateForDatabase(dataCheckIn)
        val formattedCheckOut = formatDateForDatabase(dataCheckOut)

        val insertQuery = "INSERT INTO prenotazioni (id_utente, id_stanza, data_check_in, data_check_out, checkin) " +
                "VALUES ($idUtente, $idStanza, '$formattedCheckIn', '$formattedCheckOut', 0)"

        ClientNetwork.retrofit.insert(insertQuery).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    showToast(getString(R.string.toast_booking_success))
                } else {
                    showToast(getString(R.string.toast_booking_error))
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                showToast(getString(R.string.toast_connection_error))
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun formatDate(calendar: Calendar): String {
        val day = calendar.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
        val month = (calendar.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
        val year = calendar.get(Calendar.YEAR)
        return "$year-$month-$day"
    }

    private fun showDatePickerDialog(minDateSet: Boolean, callback: (Calendar) -> Unit) {
        val calendar = Calendar.getInstance()
        if (minDateSet) {
            calendar.timeInMillis = dataCheckIn.timeInMillis
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        val dialog = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }
            callback(selectedCalendar)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        dialog.datePicker.minDate = calendar.timeInMillis
        dialog.show()
    }

    inner class CameraAdapter(private val camera: Camera) : RecyclerView.Adapter<CameraViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CameraViewHolder {
            val binding = ItemCameraBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CameraViewHolder(binding)
        }

        override fun onBindViewHolder(holder: CameraViewHolder, position: Int) {
            val context = holder.itemView.context
            holder.binding.cameraImage.setImageResource(camera.immagini[position])
            holder.binding.cameraText.text = camera.tipo

        }

        override fun getItemCount() = camera.immagini.size
    }

    private fun formatDateForDatabase(date: String): String {
        val parts = date.split("-")
        val year = parts[0]
        val month = parts[1].toInt().toString().padStart(2, '0')
        val day = parts[2].toInt().toString().padStart(2, '0')
        return "$year-$month-$day"
    }

    inner class CameraViewHolder(val binding: ItemCameraBinding) : RecyclerView.ViewHolder(binding.root)
}
