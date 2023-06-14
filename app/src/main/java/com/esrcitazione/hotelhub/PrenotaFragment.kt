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
                updateFatturazioneButtonStatus()
            }
        }

        // Campo di testo per la data di check-in
        binding.editTextDataCheckIn.setOnClickListener {
            showDatePickerDialog(dataCheckIn) { calendar ->
                dataCheckIn = calendar
                binding.editTextDataCheckIn.setText(formatDate(calendar))
                isCheckInSelected = true
                updateFatturazioneButtonStatus()
            }
        }

        // Campo di testo per la data di check-out
        binding.editTextDataCheckOut.setOnClickListener {
            showDatePickerDialog(dataCheckOut) { calendar ->
                dataCheckOut = calendar
                binding.editTextDataCheckOut.setText(formatDate(calendar))
                isCheckOutSelected = true
                updateFatturazioneButtonStatus()
            }
        }

        binding.buttonFatturazione.setOnClickListener {
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
        }

        binding.buttonConferma.setOnClickListener {
            val nome = binding.editTextNome.text.toString()
            val cognome = binding.editTextCognome.text.toString()
            val numeroCarta = binding.editTextNumeroCarta.text.toString()
            val cvv = binding.editTextCvv.text.toString()
            if (nome.isNotBlank() && cognome.isNotBlank() && numeroCarta.length == 16 && cvv.length == 3) {
                Toast.makeText(context, "Pagamento effettuato", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Numero carta o CVV non validi", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun updateFatturazioneButtonStatus() {
        binding.buttonFatturazione.isEnabled = isCheckInSelected && isCheckOutSelected && !fatturazionePremuta
        binding.buttonFatturazione.alpha = if (binding.buttonFatturazione.isEnabled) 1f else 0.5f
    }

    inner class CameraAdapter(private val camera: Camera) : RecyclerView.Adapter<CameraAdapter.CameraViewHolder>() {
        inner class CameraViewHolder(val binding: ItemCameraBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CameraViewHolder {
            val binding = ItemCameraBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CameraViewHolder(binding)
        }

        override fun getItemCount() = camera.immagini.size

        override fun onBindViewHolder(holder: CameraViewHolder, position: Int) {
            holder.binding.cameraImage.setImageResource(camera.immagini[position])
            holder.binding.cameraText.text = "${camera.tipo} ${position + 1}"
        }
    }

    private fun showDatePickerDialog(calendar: Calendar, onDateSetListener: (Calendar) -> Unit) {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                onDateSetListener(calendar)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = calendar.timeInMillis
        datePickerDialog.show()
    }

    private fun formatDate(calendar: Calendar): String {
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        return String.format(Locale.getDefault(), "%02d/%02d/%d", day, month, year)
    }
}
