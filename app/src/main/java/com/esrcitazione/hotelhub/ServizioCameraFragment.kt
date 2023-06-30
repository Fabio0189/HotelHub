import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.esrcitazione.hotelhub.ClientNetwork
import com.esrcitazione.hotelhub.DatabaseHelper
import com.esrcitazione.hotelhub.R
import com.esrcitazione.hotelhub.databinding.FragmentServizioCameraBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

class ServizioCameraFragment : Fragment() {
    private lateinit var binding: FragmentServizioCameraBinding
    private lateinit var db: DatabaseHelper

    private var toastMessageErrorSelectingOptions: String? = null
    private var toastMessageErrorSavingData: String? = null
    private var toastMessageNetworkError: String? = null
    private var toastMessageDataSaved: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize toast messages
        toastMessageErrorSelectingOptions = getString(R.string.error_selecting_options_toast)
        toastMessageErrorSavingData = getString(R.string.error_saving_data_toast)
        toastMessageNetworkError = getString(R.string.network_error_toast)
        toastMessageDataSaved = getString(R.string.data_saved_toast)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentServizioCameraBinding.inflate(inflater, container, false)
        db= DatabaseHelper(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenuItems()
        setupTotalButton()
    }

    private fun setupMenuItems() {
        // Primi Piatti
        binding.checkBoxCarbonara.setOnCheckedChangeListener { _, isChecked ->
            binding.editTextCarbonara.isEnabled = isChecked
            updateTotal()
            checkIfAtLeastOneCheckBoxSelected()
        }

        binding.checkBoxAmatriciana.setOnCheckedChangeListener { _, isChecked ->
            binding.editTextAmatriciana.isEnabled = isChecked
            updateTotal()
            checkIfAtLeastOneCheckBoxSelected()
        }

        binding.checkBoxLasagne.setOnCheckedChangeListener { _, isChecked ->
            binding.editTextLasagne.isEnabled = isChecked
            updateTotal()
            checkIfAtLeastOneCheckBoxSelected()
        }

        // Secondi Piatti
        binding.checkBoxCarpaccio.setOnCheckedChangeListener { _, isChecked ->
            binding.editTextCarpaccio.isEnabled = isChecked
            updateTotal()
            checkIfAtLeastOneCheckBoxSelected()
        }

        binding.checkBoxCotoletta.setOnCheckedChangeListener { _, isChecked ->
            binding.editTextCotoletta.isEnabled = isChecked
            updateTotal()
            checkIfAtLeastOneCheckBoxSelected()
        }

        binding.checkBoxTagliata.setOnCheckedChangeListener { _, isChecked ->
            binding.editTextTagliata.isEnabled = isChecked
            updateTotal()
            checkIfAtLeastOneCheckBoxSelected()
        }

        // Contorni
        binding.checkBoxInsalata.setOnCheckedChangeListener { _, isChecked ->
            binding.editTextInsalata.isEnabled = isChecked
            updateTotal()
            checkIfAtLeastOneCheckBoxSelected()
        }

        binding.checkBoxPatatine.setOnCheckedChangeListener { _, isChecked ->
            binding.editTextPatatine.isEnabled = isChecked
            updateTotal()
            checkIfAtLeastOneCheckBoxSelected()
        }

        binding.checkBoxVerdure.setOnCheckedChangeListener { _, isChecked ->
            binding.editTextVerdure.isEnabled = isChecked
            updateTotal()
            checkIfAtLeastOneCheckBoxSelected()
        }

        // Dessert
        binding.checkBoxTortino.setOnCheckedChangeListener { _, isChecked ->
            binding.editTextTortino.isEnabled = isChecked
            updateTotal()
            checkIfAtLeastOneCheckBoxSelected()
        }

        binding.checkBoxTiramisu.setOnCheckedChangeListener { _, isChecked ->
            binding.editTextTiramisu.isEnabled = isChecked
            updateTotal()
            checkIfAtLeastOneCheckBoxSelected()
        }

        binding.checkBoxSfoglia.setOnCheckedChangeListener { _, isChecked ->
            binding.editTextSfoglia.isEnabled = isChecked
            updateTotal()
            checkIfAtLeastOneCheckBoxSelected()
        }
    }

    private fun setupTotalButton() {
        binding.buttonCalcolaTotale.isEnabled = false // Disable the button at the beginning

        binding.buttonCalcolaTotale.setOnClickListener {
            if (isAtLeastOneCheckBoxSelected()) {
                val total = calcolaTotale().toString()
                val totalText = "$total €"
                binding.textViewTotale.text = totalText


            } else {
                // No checkbox selected, show an error message
                showToast(toastMessageErrorSelectingOptions)
            }
        }
        binding.confermaServizio.setOnClickListener {
            if (isAtLeastOneCheckBoxSelected()) {
                val selectedData = getSelectedData()
                val id= db.getId()
                getNumeroCameraFromDatabase(id,selectedData)
            } else {
                showToast(toastMessageErrorSelectingOptions)
            }
        }
    }

    private fun updateTotal() {
        val total = calcolaTotale()
        binding.textViewTotale.text = total.toString()
    }

    private fun calcolaTotale(): Int {
        var total = 0
        // Primi Piatti
        if (binding.checkBoxCarbonara.isChecked) {
            val quantity = binding.editTextCarbonara.text.toString().toIntOrNull() ?: 0
            total += 15 * quantity
        }

        if (binding.checkBoxAmatriciana.isChecked) {
            val quantity = binding.editTextAmatriciana.text.toString().toIntOrNull() ?: 0
            total += 15 * quantity
        }

        if (binding.checkBoxLasagne.isChecked) {
            val quantity = binding.editTextLasagne.text.toString().toIntOrNull() ?: 0
            total += 15 * quantity
        }

        // Secondi Piatti
        if (binding.checkBoxCarpaccio.isChecked) {
            val quantity = binding.editTextCarpaccio.text.toString().toIntOrNull() ?: 0
            total += 20 * quantity
        }

        if (binding.checkBoxCotoletta.isChecked) {
            val quantity = binding.editTextCotoletta.text.toString().toIntOrNull() ?: 0
            total += 20 * quantity
        }

        if (binding.checkBoxTagliata.isChecked) {
            val quantity = binding.editTextTagliata.text.toString().toIntOrNull() ?: 0
            total += 20 * quantity
        }

        // Contorni
        if (binding.checkBoxInsalata.isChecked) {
            val quantity = binding.editTextInsalata.text.toString().toIntOrNull() ?: 0
            total += 5 * quantity
        }

        if (binding.checkBoxPatatine.isChecked) {
            val quantity = binding.editTextPatatine.text.toString().toIntOrNull() ?: 0
            total += 5 * quantity
        }

        if (binding.checkBoxVerdure.isChecked) {
            val quantity = binding.editTextVerdure.text.toString().toIntOrNull() ?: 0
            total += 5 * quantity
        }

        // Dessert
        if (binding.checkBoxTortino.isChecked) {
            val quantity = binding.editTextTortino.text.toString().toIntOrNull() ?: 0
            total += 5 * quantity
        }

        if (binding.checkBoxTiramisu.isChecked) {
            val quantity = binding.editTextTiramisu.text.toString().toIntOrNull() ?: 0
            total += 5 * quantity
        }

        if (binding.checkBoxSfoglia.isChecked) {
            val quantity = binding.editTextSfoglia.text.toString().toIntOrNull() ?: 0
            total += 5 * quantity
        }

        return total
    }

    private fun isAtLeastOneCheckBoxSelected(): Boolean {
        // Check if at least one checkbox is selected
        return binding.checkBoxCarbonara.isChecked ||
                binding.checkBoxAmatriciana.isChecked ||
                binding.checkBoxLasagne.isChecked ||
                // Add the other checkbox checks
                // Secondi Piatti
                binding.checkBoxCarpaccio.isChecked ||
                binding.checkBoxCotoletta.isChecked ||
                binding.checkBoxTagliata.isChecked ||
                // Contorni
                binding.checkBoxInsalata.isChecked ||
                binding.checkBoxPatatine.isChecked ||
                binding.checkBoxVerdure.isChecked ||
                // Dessert
                binding.checkBoxTortino.isChecked ||
                binding.checkBoxTiramisu.isChecked ||
                binding.checkBoxSfoglia.isChecked
    }

    private fun checkIfAtLeastOneCheckBoxSelected() {
        // Check if at least one checkbox is selected
        binding.buttonCalcolaTotale.isEnabled = isAtLeastOneCheckBoxSelected()
    }

    private fun getSelectedData(): String {
        val stringBuilder = StringBuilder()

        // Primi Piatti
        if (binding.checkBoxCarbonara.isChecked) {
            val quantity = binding.editTextCarbonara.text.toString().toIntOrNull() ?: 0
            stringBuilder.append("Carbonara - Quantità: $quantity\n")
        }

        if (binding.checkBoxAmatriciana.isChecked) {
            val quantity = binding.editTextAmatriciana.text.toString().toIntOrNull() ?: 0
            stringBuilder.append("Amatriciana - Quantità: $quantity\n")
        }

        if (binding.checkBoxLasagne.isChecked) {
            val quantity = binding.editTextLasagne.text.toString().toIntOrNull() ?: 0
            stringBuilder.append("Lasagne - Quantità: $quantity\n")
        }

        // Secondi Piatti
        if (binding.checkBoxCarpaccio.isChecked) {
            val quantity = binding.editTextCarpaccio.text.toString().toIntOrNull() ?: 0
            stringBuilder.append("Carpaccio - Quantità: $quantity\n")
        }

        if (binding.checkBoxCotoletta.isChecked) {
            val quantity = binding.editTextCotoletta.text.toString().toIntOrNull() ?: 0
            stringBuilder.append("Cotoletta - Quantità: $quantity\n")
        }

        if (binding.checkBoxTagliata.isChecked) {
            val quantity = binding.editTextTagliata.text.toString().toIntOrNull() ?: 0
            stringBuilder.append("Tagliata - Quantità: $quantity\n")
        }

        // Contorni
        if (binding.checkBoxInsalata.isChecked) {
            val quantity = binding.editTextInsalata.text.toString().toIntOrNull() ?: 0
            stringBuilder.append("Insalata - Quantità: $quantity\n")
        }

        if (binding.checkBoxPatatine.isChecked) {
            val quantity = binding.editTextPatatine.text.toString().toIntOrNull() ?: 0
            stringBuilder.append("Patatine - Quantità: $quantity\n")
        }

        if (binding.checkBoxVerdure.isChecked) {
            val quantity = binding.editTextVerdure.text.toString().toIntOrNull() ?: 0
            stringBuilder.append("Verdure - Quantità: $quantity\n")
        }

        // Dessert
        if (binding.checkBoxTortino.isChecked) {
            val quantity = binding.editTextTortino.text.toString().toIntOrNull() ?: 0
            stringBuilder.append("Tortino - Quantità: $quantity\n")
        }

        if (binding.checkBoxTiramisu.isChecked) {
            val quantity = binding.editTextTiramisu.text.toString().toIntOrNull() ?: 0
            stringBuilder.append("Tiramisù - Quantità: $quantity\n")
        }

        if (binding.checkBoxSfoglia.isChecked) {
            val quantity = binding.editTextSfoglia.text.toString().toIntOrNull() ?: 0
            stringBuilder.append("Sfoglia - Quantità: $quantity\n")
        }

        return stringBuilder.toString()
    }

    private fun saveDataToServer(data: String, numero_camera: Int, id: Int, idPrenotazione: Int) {
        val apiService = ClientNetwork.retrofit
        val query = "INSERT INTO servizio_in_camera (id_p_sc, id_u_sc, numero_camera, servizio ) VALUES ('$idPrenotazione','$id','$numero_camera','$data')"
        val call = apiService.insert(query)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    showToast(toastMessageDataSaved)
                } else {
                    showToast(toastMessageErrorSavingData)
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                showToast(toastMessageNetworkError)
            }
        })
    }

    private fun getNumeroCameraFromDatabase(userId: Int, selectedData: String) {
        val apiService = ClientNetwork.retrofit
        val currentDate = LocalDate.now().toString() // Get the current date
        val query =
            "SELECT numero_stanza, id_p " +
                    "FROM stanze, prenotazioni " +
                    "WHERE stanze.id_s = prenotazioni.id_stanza " +
                    "AND prenotazioni.id_utente = $userId " +
                    "AND '$currentDate' >= prenotazioni.data_check_in " +
                    "AND '$currentDate' <= prenotazioni.data_check_out"
        val call = apiService.select(query)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val jsonResponse = response.body()
                    val jsonArray = jsonResponse?.getAsJsonArray("queryset")
                    if (jsonArray != null && jsonArray.size() > 0) {
                        val jsonObject = jsonArray[0].asJsonObject
                        val numeroCameraJson = jsonObject.get("numero_stanza")
                        val idPrenotazioneJson = jsonObject.get("id_p")
                        if (numeroCameraJson != null && idPrenotazioneJson != null) {
                            val numeroCamera = numeroCameraJson.asInt
                            val idPrenotazione = idPrenotazioneJson.asInt
                            saveDataToServer(selectedData, numeroCamera, userId, idPrenotazione)
                        }
                    } else {
                        // No results found
                    }
                } else {
                    // Error in server response
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                // Network error or communication with the server failed
            }
        })
    }

    private fun showToast(message: String?) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}