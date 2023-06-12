package com.esrcitazione.hotelhub

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.esrcitazione.hotelhub.databinding.FragmentPrenotaBinding
import com.esrcitazione.hotelhub.databinding.ItemCameraBinding
import androidx.recyclerview.widget.RecyclerView

data class Camera(val tipo: String, val immagini: List<Int>)

class PrenotaFragment : Fragment() {
    private lateinit var binding: FragmentPrenotaBinding

    private val camere = listOf(
        Camera("Camera Singola", listOf(R.drawable.camerasingola1, R.drawable.camerasingola2, R.drawable.camerasingola3, R.drawable.camerasingola4)),
        Camera("Camera Doppia", listOf(R.drawable.cameradoppia1, R.drawable.cameradoppia2, R.drawable.cameradoppia3, R.drawable.cameradoppia4)),
        Camera("Camera Familiare", listOf(R.drawable.camerafamiliare1, R.drawable.camerafamiliare2, R.drawable.camerafamiliare3, R.drawable.camerafamiliare4))
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
            binding.layoutRecyclerViews.addView(recyclerView) //Assumendo che layoutRecyclerViews sia un LinearLayout nel tuo layout
        }

        // Set up click handlers for your buttons
        binding.buttonScegliCamera.setOnClickListener {
            // Show the layoutRecyclerViews and the Spinner here
            binding.layoutRecyclerViews.visibility = View.VISIBLE
            binding.spinnerTipoCamera.visibility = View.VISIBLE
        }

        binding.buttonFatturazione.setOnClickListener {
            // Show the layout for the billing information here
            binding.textViewNome.visibility = View.VISIBLE
            binding.editTextNome.visibility = View.VISIBLE
            binding.textViewCognome.visibility = View.VISIBLE
            binding.editTextCognome.visibility = View.VISIBLE
            binding.textViewNumeroCarta.visibility = View.VISIBLE
            binding.editTextNumeroCarta.visibility = View.VISIBLE
            binding.textViewCvv.visibility = View.VISIBLE
            binding.editTextCvv.visibility = View.VISIBLE
            binding.buttonConferma.visibility = View.VISIBLE
        }

        binding.buttonConferma.setOnClickListener {
            val numeroCarta = binding.editTextNumeroCarta.text.toString()
            val cvv = binding.editTextCvv.text.toString()
            if (numeroCarta.length == 16 && cvv.length == 3) {
                Toast.makeText(context, "Pagamento effettuato", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Numero carta o CVV non validi", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    inner class CameraAdapter(private val camera: Camera) : RecyclerView.Adapter<CameraAdapter.CameraViewHolder>() {

        inner class CameraViewHolder(private val binding: ItemCameraBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(image: Int) {
                binding.cameraImage.setImageResource(image)
                binding.cameraText.text = camera.tipo
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CameraViewHolder {
            val binding = ItemCameraBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CameraViewHolder(binding)
        }

        override fun getItemCount(): Int = camera.immagini.size

        override fun onBindViewHolder(holder: CameraViewHolder, position: Int) {
            holder.bind(camera.immagini[position])
        }
    }
}
