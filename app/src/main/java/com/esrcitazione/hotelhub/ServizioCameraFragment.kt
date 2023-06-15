import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.esrcitazione.hotelhub.ClientNetwork
import com.esrcitazione.hotelhub.R
import com.esrcitazione.hotelhub.databinding.FragmentServizioCameraBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ServizioCameraFragment : Fragment() {
    private lateinit var binding: FragmentServizioCameraBinding
    private lateinit var selectedDishes: HashMap<String, Int> // Mappa per memorizzare i piatti selezionati e le relative quantità

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentServizioCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenuItems()
        setupTotalButton()
        setupConfermaButton()
        selectedDishes = HashMap()
    }

    private fun setupMenuItems() {
        // Primi Piatti
        binding.checkBoxCarbonara.setOnCheckedChangeListener { _, isChecked ->
            binding.editTextCarbonara.isEnabled = isChecked
            updateTotal()
            isAtLeastOneCheckBoxSelected()
        }

        binding.checkBoxAmatriciana.setOnCheckedChangeListener { _, isChecked ->
            binding.editTextAmatriciana.isEnabled = isChecked
            updateTotal()
            isAtLeastOneCheckBoxSelected()
        }

        binding.checkBoxLasagne.setOnCheckedChangeListener { _, isChecked ->
            binding.editTextLasagne.isEnabled = isChecked
            updateTotal()
            isAtLeastOneCheckBoxSelected()
        }

        // Secondi Piatti
        binding.checkBoxCarpaccio.setOnCheckedChangeListener { _, isChecked ->
            binding.editTextCarpaccio.isEnabled = isChecked
            updateTotal()
            isAtLeastOneCheckBoxSelected()
        }

        binding.checkBoxCotoletta.setOnCheckedChangeListener { _, isChecked ->
            binding.editTextCotoletta.isEnabled = isChecked
            updateTotal()
            isAtLeastOneCheckBoxSelected()
        }

        binding.checkBoxTagliata.setOnCheckedChangeListener { _, isChecked ->
            binding.editTextTagliata.isEnabled = isChecked
            updateTotal()
            isAtLeastOneCheckBoxSelected()
        }

        // Contorni
        binding.checkBoxInsalata.setOnCheckedChangeListener { _, isChecked ->
            binding.editTextInsalata.isEnabled = isChecked
            updateTotal()
            isAtLeastOneCheckBoxSelected()
        }

        binding.checkBoxPatatine.setOnCheckedChangeListener { _, isChecked ->
            binding.editTextPatatine.isEnabled = isChecked
            updateTotal()
            isAtLeastOneCheckBoxSelected()
        }

        binding.checkBoxVerdure.setOnCheckedChangeListener { _, isChecked ->
            binding.editTextVerdure.isEnabled = isChecked
            updateTotal()
            isAtLeastOneCheckBoxSelected()
        }

        // Dessert
        binding.checkBoxTortino.setOnCheckedChangeListener { _, isChecked ->
            binding.editTextTortino.isEnabled = isChecked
            updateTotal()
            isAtLeastOneCheckBoxSelected()
        }

        binding.checkBoxTiramisu.setOnCheckedChangeListener { _, isChecked ->
            binding.editTextTiramisu.isEnabled = isChecked
            updateTotal()
            isAtLeastOneCheckBoxSelected()
        }

        binding.checkBoxSfoglia.setOnCheckedChangeListener { _, isChecked ->
            binding.editTextSfoglia.isEnabled = isChecked
            updateTotal()
            isAtLeastOneCheckBoxSelected()
        }
    }

    private fun setupTotalButton() {
        binding.buttonCalcolaTotale.isEnabled = false // Disabilita il pulsante all'inizio

        binding.buttonCalcolaTotale.setOnClickListener {
            if (isAtLeastOneCheckBoxSelected()) {
                val total = calculateTotal()
                binding.textViewTotale.text = total.toString()
                binding.confermaServizio.isEnabled = true // Abilita il pulsante "Conferma Servizio"
            } else {
                // Nessuna checkbox selezionata, mostra un messaggio di errore
                Toast.makeText(requireContext(), "Seleziona almeno una opzione", Toast.LENGTH_SHORT).show()
                binding.confermaServizio.isEnabled = false // Disabilita il pulsante "Conferma Servizio"
            }
        }
    }

    private fun setupConfermaButton() {
        binding.confermaServizio.isEnabled = false // Disabilita il pulsante "Conferma Servizio"

        binding.confermaServizio.setOnClickListener {
            inviaDatiAlDatabase()
        }
    }

    private fun updateTotal() {
        val total = calculateTotal()
        binding.textViewTotale.text = total.toString()
    }

    private fun calculateTotal(): Int {
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
        // Controlla se almeno una checkbox è selezionata
        return binding.checkBoxCarbonara.isChecked ||
                binding.checkBoxAmatriciana.isChecked ||
                binding.checkBoxLasagne.isChecked ||
                binding.checkBoxCarpaccio.isChecked ||
                binding.checkBoxCotoletta.isChecked ||
                binding.checkBoxTagliata.isChecked ||
                binding.checkBoxInsalata.isChecked ||
                binding.checkBoxPatatine.isChecked ||
                binding.checkBoxVerdure.isChecked ||
                binding.checkBoxTortino.isChecked ||
                binding.checkBoxTiramisu.isChecked ||
                binding.checkBoxSfoglia.isChecked
    }

    private fun inviaDatiAlDatabase() {
        // Costruisci la stringa dei piatti selezionati con le relative quantità
        val piattiSelezionati = StringBuilder()
        for ((piatto, quantita) in selectedDishes) {
            piattiSelezionati.append("$piatto x$quantita, ")
        }
        val piattiStringa = piattiSelezionati.toString().trimEnd(',', ' ')

        // Costruisci la query SQL per l'inserimento dei dati
        val query = "INSERT INTO servizio_in_camera (piatti) VALUES ('$piattiStringa')"

        // Effettua la richiesta al tuo database tramite Retrofit
        ClientNetwork.retrofit.insert(query).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    // I dati sono stati inviati con successo al database
                    Toast.makeText(requireContext(), "Dati inviati al database", Toast.LENGTH_SHORT).show()
                } else {
                    // Si è verificato un errore durante l'invio dei dati al database
                    Toast.makeText(requireContext(), "Errore durante l'invio dei dati", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                // Gestisci l'errore di rete o del server qui
                Toast.makeText(requireContext(), "Errore di connessione", Toast.LENGTH_SHORT).show()
            }
        })
    }
}