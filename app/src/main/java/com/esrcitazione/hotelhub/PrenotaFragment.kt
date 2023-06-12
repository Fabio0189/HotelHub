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

class PrenotaFragment : Fragment() {
    private lateinit var binding: FragmentPrenotaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // To be implemented if needed
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPrenotaBinding.inflate(inflater, container, false)
        val view = binding.root

        // Setup RecyclerView
        val images = listOf(R.drawable.camerasingola1, R.drawable.camerasingola2, R.drawable.camerasingola3, R.drawable.camerasingola4)
        val adapter = CameraAdapter(images)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.adapter = adapter

        // Set up click handlers for your buttons
        binding.buttonScegliCamera.setOnClickListener {
            // Show the RecyclerView and the Spinner here
            binding.recyclerView.visibility = View.VISIBLE
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

    companion object {
        @JvmStatic
        fun newInstance() = PrenotaFragment()
    }

    inner class CameraAdapter(private val images: List<Int>) : RecyclerView.Adapter<CameraAdapter.CameraViewHolder>() {

        inner class CameraViewHolder(private val binding: ItemCameraBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(image: Int) {
                binding.cameraImage.setImageResource(image)
                binding.cameraText.text = "Camera Singola"
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CameraViewHolder {
            val binding = ItemCameraBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CameraViewHolder(binding)
        }

        override fun getItemCount(): Int = images.size

        override fun onBindViewHolder(holder: CameraViewHolder, position: Int) {
            holder.bind(images[position])
        }
    }
}
