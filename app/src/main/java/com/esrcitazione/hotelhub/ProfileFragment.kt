package com.esrcitazione.hotelhub

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.esrcitazione.hotelhub.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.modificadati.setOnClickListener {
            val modificaFragment = ModificaFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.frameLayout, modificaFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        val databaseHelper = DatabaseHelper(requireContext())
        val nomeUtente = databaseHelper.getNomeUtente()
        val cognomeUtente = databaseHelper.getCognomeUtente()
        val emailUtente = databaseHelper.getEmail()

        binding.nomeValue.text = nomeUtente
        binding.cognomeValue.text = cognomeUtente
        binding.emailValue.text = emailUtente

        return binding.root
    }
}
