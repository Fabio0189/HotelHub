package com.esrcitazione.hotelhub

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
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

data class Camera(val tipo: String, val immagini: List<Int>, val prezzo: Double)

class PrenotaFragment : Fragment() {
    private lateinit var binding: FragmentPrenotaBinding
    private lateinit var cameraSelected: String
    private var fatturazionePremuta = false
    private var dataCheckIn: Calendar = Calendar.getInstance()
    private var dataCheckOut: Calendar = Calendar.getInstance()
    private var isCheckInSelected = false
    private var isCheckOutSelected = false

    private val camere = listOf(
        Camera("Camera Singola", listOf(R.drawable.camerasingola1, R.drawable.camerasingola2, R.drawable.camerasingola3, R.drawable.camerasingola4), 50.0),
        Camera("Camera Doppia", listOf(R.drawable.cameradoppia1, R.drawable.cameradoppia2, R.drawable.cameradoppia3, R.drawable.cameradoppia4), 100.0),
        Camera("Camera Familiare", listOf(R.drawable.camerafamiliare1, R.drawable.camerafamiliare2, R.drawable.camerafamiliare3, R.drawable.camerafamiliare4), 200.0)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPrenotaBinding.inflate(inflater, container, false)
        val view = binding.root

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
                binding.textViewPrezzo.text = "Prezzo: $prezzoCamera €"
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
                        Toast.makeText(context, "La data di check-in non può essere successiva o uguale alla data di check-out. Si prega di selezionare di nuovo la data di check-out.", Toast.LENGTH_SHORT).show()
                    }
                }
                binding.buttonFatturazione.isEnabled = isCheckInSelected && isCheckOutSelected
            }
        }

        binding.editTextDataCheckOut.setOnClickListener {
            if (!isCheckInSelected) {
                Toast.makeText(context, "Si prega di selezionare prima la data di check-in.", Toast.LENGTH_SHORT).show()
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
    SELECT COUNT(*) as count FROM stanze
    WHERE capacita = $tipoCamera
    AND NOT EXISTS (
        SELECT *
        FROM prenotazioni
        WHERE id_stanza = stanze.id
        AND (
            (data_check_in <= '${formatDate(dataCheckOut)}' AND data_check_out >= '${formatDate(dataCheckOut)}')
            OR (data_check_in <= '${formatDate(dataCheckIn)}' AND data_check_out >= '${formatDate(dataCheckIn)}')
            OR (data_check_in >= '${formatDate(dataCheckIn)}' AND data_check_out <= '${formatDate(dataCheckOut)}')))
""").enqueue(object: Callback<JsonObject>  {
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        if (response.isSuccessful && response.body() != null) {
                            val result = response.body()?.get("queryset") as JsonArray
                            if(result.asJsonArray.size() > 0) {
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
                            } else {
                            }
                        } else {

                            Toast.makeText(context, "Questo tipo di camera non è disponibile per le date selezionate", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                        Toast.makeText(context, "ERRORE di connesione", Toast.LENGTH_SHORT).show()
                    }

                })
        }

        binding.buttonConferma.setOnClickListener {
            val nome = binding.editTextNome.text.toString()
            val cognome = binding.editTextCognome.text.toString()
            val numeroCarta = binding.editTextNumeroCarta.text.toString()
            val cvv = binding.editTextCvv.text.toString()
            if (nome.isNotBlank() && cognome.isNotBlank() && numeroCarta.length == 16 && cvv.length == 3) {
                Toast.makeText(context, "Pagamento effettuato", Toast.LENGTH_SHORT).show()
                //Qui effettua il pagamento e crea la prenotazione nel database
            } else {
                Toast.makeText(context, "Inserisci correttamente i dati del pagamento", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
    private fun effettuaPrenotazione(tipoCamera: String, dataCheckIn: String, dataCheckOut: String, nome: String, cognome: String, numeroCarta: String, cvv: String) {
        val insertQuery = "INSERT INTO prenotazioni (tipo_camera, data_check_in, data_check_out, nome, cognome, numero_carta, cvv) " +
                "VALUES ('$tipoCamera', '$dataCheckIn', '$dataCheckOut', '$nome', '$cognome', '$numeroCarta', '$cvv')"

        ClientNetwork.retrofit.insert(insertQuery).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    showToast("Prenotazione effettuata con successo!")
                } else {
                    showToast("Errore durante la prenotazione. Riprova.")
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
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            CameraViewHolder(ItemCameraBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        override fun onBindViewHolder(holder: CameraViewHolder, position: Int) {
            holder.binding.cameraImage.setImageResource(camera.immagini[position])
            holder.binding.cameraText.text = "${camera.tipo} ${position + 1}"
        }

        override fun getItemCount() = camera.immagini.size
    }


    inner class CameraViewHolder(val binding: ItemCameraBinding) : RecyclerView.ViewHolder(binding.root)
}
