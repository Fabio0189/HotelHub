package com.esrcitazione.hotelhub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.esrcitazione.hotelhub.databinding.FragmentPrenotaBinding
import com.esrcitazione.hotelhub.databinding.ItemCameraBinding
import androidx.recyclerview.widget.RecyclerView

data class Camera(val tipo: String, val immagini: List<Int>, val prezzo: Double)

class PrenotaFragment : Fragment() {
    private lateinit var binding: FragmentPrenotaBinding
    private lateinit var cameraSelected: String
    private var fatturazionePremuta = false

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

                if (fatturazionePremuta) {
                    binding.buttonFatturazione.isEnabled = false
                    binding.buttonFatturazione.alpha = 0.5f
                }
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
}
