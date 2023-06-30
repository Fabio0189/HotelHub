package com.esrcitazione.hotelhub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.esrcitazione.hotelhub.databinding.FragmentModificaBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ModificaFragment : Fragment() {

    private var _binding: FragmentModificaBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentModificaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = DatabaseHelper(requireContext())

        binding.buttonSalva.setOnClickListener {
            val newName = binding.editNome.text.toString().trim()
            val newCognome = binding.editCognome.text.toString().trim()
            val newEmail = binding.editEmail.text.toString().trim()
            val newPassword = binding.editPassword.text.toString().trim()
            val confirmPassword = binding.editPasswordConferma.text.toString().trim()

            val idUtente = db.getId()

            val updatedName = if (newName.isNotEmpty()) newName else db.getNomeUtente()
            val updatedCognome = if (newCognome.isNotEmpty()) newCognome else db.getCognomeUtente()
            val updatedEmail = if (newEmail.isNotEmpty()) newEmail else db.getEmail()
            val updatedPassword = if (newPassword.isNotEmpty()) newPassword else db.getPassword()

            if (newPassword.isNotEmpty() && newPassword != confirmPassword) {
                val toastMessage = getString(R.string.toast_passwords_do_not_match)
                Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            updateUserDataOnline(idUtente, updatedName, updatedCognome, updatedEmail, updatedPassword) { success ->
                if (success) {
                    db.updateUserData(idUtente, updatedName, updatedCognome, updatedEmail, updatedPassword)
                    val toastMessage = getString(R.string.toast_data_updated_successfully)
                    Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_SHORT).show()
                } else {
                    val toastMessage = getString(R.string.toast_data_update_error)
                    Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUserDataOnline(userId: Int, updatedName: String, updatedCognome: String, updatedEmail: String, updatedPassword: String, callback: (Boolean) -> Unit) {
        val query = "UPDATE utenti SET " +
                "nome = '$updatedName', " +
                "cognome = '$updatedCognome', " +
                "email = '$updatedEmail', " +
                "password = '$updatedPassword' " +
                "WHERE id_u = $userId"

        ClientNetwork.retrofit.update(query).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    callback.invoke(true)
                } else {
                    callback.invoke(false)
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                callback.invoke(false)
            }
        })
    }
}
