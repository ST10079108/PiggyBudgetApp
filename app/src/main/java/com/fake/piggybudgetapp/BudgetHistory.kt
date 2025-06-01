package com.fake.piggybudgetapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.fake.piggybudgetapp.database.FirebaseDbHelper
import com.fake.piggybudgetapp.databinding.ActivityBudgetHistoryBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BudgetHistory : AppCompatActivity() {

    private lateinit var binding: ActivityBudgetHistoryBinding
    private lateinit var budgetAdapter: BudgetAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // View binding
        binding = ActivityBudgetHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadBudgets()

        budgetAdapter = BudgetAdapter(emptyList())
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = budgetAdapter

        binding.btnClearHistory.setOnClickListener {
            FirebaseDbHelper.deleteAllBudgets {
                loadBudgets()
                runOnUiThread {
                    Toast.makeText(this, "All budgets deleted!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    private fun loadBudgets() {
        FirebaseDbHelper.getAllBudgets { allBudgets ->
            // Filter only budgets that have been filled in
            val completedBudgets = allBudgets.filter {
                it.name.isNotBlank() &&
                        it.category.isNotBlank() &&
                        it.amount != 0.0
            }
            runOnUiThread {
                budgetAdapter.updateBudgets(completedBudgets)
            }
        }
    }
}