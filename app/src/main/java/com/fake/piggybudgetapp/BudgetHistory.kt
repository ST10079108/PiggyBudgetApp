package com.fake.piggybudgetapp

import android.content.Intent
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.fake.piggybudgetapp.database.FirebaseDbHelper
import com.fake.piggybudgetapp.databinding.ActivityBudgetHistoryBinding
import com.fake.piggybudgetapp.databinding.ActivityProfileBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
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
        // Hide phone's ui
        window.setDecorFitsSystemWindows(false)
        window.insetsController?.let { controller ->
            controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            controller.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        // View binding
        binding = ActivityBudgetHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Stop bottomnavigationbar from auto adding padding on startup
        val navBar = findViewById<BottomNavigationView>(R.id.navbar)
        navBar.setOnApplyWindowInsetsListener { view, insets ->
            view.setPadding(view.paddingLeft, view.paddingTop, view.paddingRight, 0)
            insets
        }

        navBar.selectedItemId = R.id.nav_budget

        binding.navbar.setOnItemSelectedListener {
                item -> when (item.itemId) {
            R.id.nav_stats -> {
                val intent = Intent(this, StatsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_transactions -> {
                val intent = Intent(this, TransactionHistory::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_home -> {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_budget -> true
            R.id.nav_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                true
            }
            else -> false
        }
        }

        binding.btnAddScreen.setOnClickListener {
            val intent = Intent(this, BudgetActivity::class.java)
            startActivity(intent)
            true
        }

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