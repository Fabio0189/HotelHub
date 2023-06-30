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

        // per la sottolineatura dei TextViews
        binding.phoneNumber.paintFlags = binding.phoneNumber.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.email1.paintFlags = binding.email1.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.email2.paintFlags = binding.email2.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        binding.phoneNumber.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.dialog_title))
                .setMessage(getString(R.string.dialog_message))
                .setPositiveButton(getString(R.string.dialog_positive_button)) { _, _ ->
                    val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${binding.phoneNumber.text}")
                    }
                    startActivity(dialIntent)
                }
                .setNegativeButton(getString(R.string.dialog_negative_button)) { dialog, _ -> dialog.dismiss() }
                .show()
        }

        binding.email1.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.dialog_title))
                .setMessage(getString(R.string.dialog_message))
                .setPositiveButton(getString(R.string.dialog_positive_button)) { _, _ ->
                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:${binding.email1.text}")
                    }
                    startActivity(Intent.createChooser(emailIntent, getString(R.string.email_chooser_title)))
                }
                .setNegativeButton(getString(R.string.dialog_negative_button)) { dialog, _ -> dialog.dismiss() }
                .show()
        }

        binding.email2.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.dialog_title))
                .setMessage(getString(R.string.dialog_message))
                .setPositiveButton(getString(R.string.dialog_positive_button)) { _, _ ->
                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:${binding.email2.text}")
                    }
                    startActivity(Intent.createChooser(emailIntent, getString(R.string.email_chooser_title)))
                }
                .setNegativeButton(getString(R.string.dialog_negative_button)) { dialog, _ -> dialog.dismiss() }
                .show()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
