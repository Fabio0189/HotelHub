package com.esrcitazione.hotelhub

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.esrcitazione.hotelhub.databinding.FragmentHomeBinding
import java.util.*

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.datePickerButton.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(requireActivity(), { _, year, monthOfYear, dayOfMonth ->
                // Display Selected date in TextView
                Toast.makeText(context, "$dayOfMonth/${monthOfYear+1}/$year", Toast.LENGTH_LONG).show()
            }, year, month, day)
            dpd.show()
        }

        binding.paymentButton.setOnClickListener {
            val cardNumber = binding.cardNumberEditText.text.toString()
            val cvv = binding.cvvEditText.text.toString()

            if (cardNumber.length != 16 || cvv.length != 3) {
                Toast.makeText(context, "Invalid card details", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Payment successful", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
