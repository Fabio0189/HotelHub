import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.esrcitazione.hotelhub.R
import com.esrcitazione.hotelhub.databinding.FragmentServizioCameraBinding

class ServizioCameraFragment : Fragment() {
    private lateinit var binding: FragmentServizioCameraBinding

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
        binding.buttonCalcolaTotale.isEnabled = false // Disabilita il pulsante all'inizio

        binding.buttonCalcolaTotale.setOnClickListener {
            if (isAtLeastOneCheckBoxSelected()) {
                val total = calculateTotal()
                binding.textViewTotale.text = total.toString()
            } else {
                // Nessuna checkbox selezionata, mostra un messaggio di errore
                Toast.makeText(requireContext(), "Seleziona almeno una opzione", Toast.LENGTH_SHORT).show()
            }
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
                // Aggiungi gli altri controlli per le checkbox
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
        // Verifica se almeno una checkbox è selezionata
        binding.buttonCalcolaTotale.isEnabled = isAtLeastOneCheckBoxSelected()
    }
}