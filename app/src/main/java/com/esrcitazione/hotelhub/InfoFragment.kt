package com.esrcitazione.hotelhub

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.esrcitazione.hotelhub.databinding.FragmentInfoBinding

class InfoFragment : Fragment() {
    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInfoBinding.inflate(inflater, container, false)

        // Sottolineatura dei TextViews
        binding.phoneNumber.paintFlags = binding.phoneNumber.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.email1.paintFlags = binding.email1.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.email2.paintFlags = binding.email2.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        binding.phoneNumber.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Attenzione")
                .setMessage("Vuoi uscire da HotelHub?")
                .setPositiveButton("Sì") { _, _ ->
                    val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${binding.phoneNumber.text}")
                    }
                    startActivity(dialIntent)
                }
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        binding.email1.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Attenzione")
                .setMessage("Vuoi uscire da HotelHub?")
                .setPositiveButton("Sì") { _, _ ->
                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:${binding.email1.text}")
                    }
                    startActivity(Intent.createChooser(emailIntent, "Invia email tramite"))
                }
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        binding.email2.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Attenzione")
                .setMessage("Vuoi uscire da HotelHub?")
                .setPositiveButton("Sì") { _, _ ->
                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:${binding.email2.text}")
                    }
                    startActivity(Intent.createChooser(emailIntent, "Invia email tramite"))
                }
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
