package com.esrcitazione.hotelhub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.esrcitazione.hotelhub.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        val homeOspiteActivity = activity as HomeOspiteActivity
        // Aggiungi il listener di click a ciascuna immagine
        binding.image1.setOnClickListener {
            homeOspiteActivity.openFragment(RistoranteFragment())
        }

        binding.image2.setOnClickListener {
            homeOspiteActivity.openFragment(LoungeBarFragment())
        }

        binding.image3.setOnClickListener {
            homeOspiteActivity.openFragment(SaunaFragment())
        }

        binding.image4.setOnClickListener {
            homeOspiteActivity.openFragment(PiscinaFragment())
        }

        binding.image5.setOnClickListener {
            homeOspiteActivity.openFragment(DogCareFragment())
        }

        binding.image6.setOnClickListener {
            homeOspiteActivity.openFragment(AreaRelaxFragment())
        }

        binding.image7.setOnClickListener {
            homeOspiteActivity.openFragment(LavanderiaFragment())
        }

        binding.image8.setOnClickListener {
            homeOspiteActivity.openFragment(LibreriaFragment())
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
