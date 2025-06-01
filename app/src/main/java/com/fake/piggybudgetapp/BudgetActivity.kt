package com.fake.piggybudgetapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.fake.piggybudgetapp.database.BudgetEntity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fake.piggybudgetapp.database.FirebaseDbHelper
import com.fake.piggybudgetapp.databinding.ActivityBudgetBinding
import kotlinx.coroutines.launch

class BudgetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBudgetBinding
    private lateinit var catResultLauncher: ActivityResultLauncher<Intent>
    private var budget = BudgetEntity( "", 0.0, "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBudgetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        catResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val categoryName = result.data?.getStringExtra("selectedCategoryName")
                binding.etCategory.setText(categoryName)
            }
        }

        binding.btnAddBudget.setOnClickListener{
            //Get user input
            val bname = binding.etBudgetName.text.toString().trim()
            val bamount = binding.etAmount.text.toString().trim().toDoubleOrNull() ?: 0.0
            val cat = binding.etCategory.text.toString().trim()

            if (bname.isEmpty() || bamount == 0.0 || cat.isEmpty()) {
                Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            //Update the budget object
            budget.name = bname
            budget.amount = bamount
            budget.category = cat

            // Save to SharedPreferences (optional)
            JsonUtils.saveBudgetToPreferences(this@BudgetActivity, budget)

            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                FirebaseDbHelper.insertBudget(budget)
            }

            Toast.makeText(this, "Budget saved to history!", Toast.LENGTH_SHORT).show()
        }

        binding.btnMyBudgets.setOnClickListener {
            val intent = Intent(this, BudgetHistory::class.java)
            startActivity(intent)
        }
        binding.btnSelectCategory.setOnClickListener {
            val intent = Intent(this, CategoryHistory::class.java)
            catResultLauncher.launch(intent)
        }
        binding.etCategory.setOnClickListener {
            val intent = Intent(this, CategoryHistory::class.java)
            catResultLauncher.launch(intent)
        }
    }
}