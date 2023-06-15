package com.esrcitazione.hotelhub

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.esrcitazione.hotelhub.databinding.FragmentProfileBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    // Aggiungi la variabile per il binding
    private lateinit var binding: FragmentProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inizializza il binding utilizzando il metodo corretto
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.modificadati.setOnClickListener {
            val modificaFragment = ModificaFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.frameLayout, modificaFragment) // R.id.fragmentContainer è l'ID del contenitore del fragment nel tuo layout
            transaction.addToBackStack(null) // Aggiungi la transazione alla back stack
            transaction.commit()
        }


        val databaseHelper = DatabaseHelper(requireContext())
        val nomeUtente = databaseHelper.getNomeUtente()
        val cognomeUtente = databaseHelper.getCognomeUtente()
        val emailUtente = databaseHelper.getEmail()

        // Assegna i valori ai TextView
        binding.nomeValue.text = nomeUtente
        binding.cognomeValue.text = cognomeUtente
        binding.emailValue.text = emailUtente

        // Restituisci la root view del binding
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
