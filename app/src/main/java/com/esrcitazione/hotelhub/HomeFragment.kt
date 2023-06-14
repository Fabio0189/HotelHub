package com.esrcitazione.hotelhub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.esrcitazione.hotelhub.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        // Aggiungi il listener di click a ciascuna immagine
        binding.image1.setOnClickListener {
            openFragment(RistoranteFragment())
        }

        binding.image2.setOnClickListener {
            openFragment(LoungeBarFragment())
        }

        binding.image3.setOnClickListener {
            openFragment(SaunaFragment())
        }

        binding.image4.setOnClickListener {
            openFragment(PiscinaFragment())
        }

        binding.image5.setOnClickListener {
            openFragment(DogCareFragment())
        }

        binding.image6.setOnClickListener {
            openFragment(AreaRelaxFragment())
        }

        binding.image7.setOnClickListener {
            openFragment(LavanderiaFragment())
        }

        binding.image8.setOnClickListener {
            openFragment(LibreriaFragment())
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openFragment(fragment: Fragment) {
        val fragmentManager = requireActivity().supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
