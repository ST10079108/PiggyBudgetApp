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
import com.fake.piggybudgetapp.databinding.ActivityCategoryHistoryBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryHistory : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryHistoryBinding
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //View binding
        binding = ActivityCategoryHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide phone's ui
        window.setDecorFitsSystemWindows(false)
        window.insetsController?.let { controller ->
            controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            controller.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        // Stop bottomnavigationbar from auto adding padding on startup
        val navBar = findViewById<BottomNavigationView>(R.id.navbar)
        navBar.setOnApplyWindowInsetsListener { view, insets ->
            view.setPadding(view.paddingLeft, view.paddingTop, view.paddingRight, 0)
            insets
        }

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
            R.id.nav_budget -> {
                val intent = Intent(this, BudgetHistory::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                true
            }
            else -> false
        }
        }

        categoryAdapter = CategoryAdapter(emptyList()) { selectedCategory ->
            val intent = Intent()
            intent.putExtra("selectedCategoryName", selectedCategory.name)
            setResult(RESULT_OK, intent)
            finish()
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = categoryAdapter

        loadCategories()

        binding.btnAddCategoryPage.setOnClickListener{
            val intent = Intent(this, CategoryActivity::class.java)
            startActivity(intent)
            true
        }

        binding.btnClearHistory.setOnClickListener {
            FirebaseDbHelper.deleteAllCategories {
                loadCategories()
                runOnUiThread {
                    Toast.makeText(this, "All budgets deleted!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadCategories() {
        FirebaseDbHelper.getAllCategories { allCategories ->
            // Filter only categories that have been filled in
            val completedCategories = allCategories.filter {
                it.name.isNotBlank()
            }
            runOnUiThread {
                categoryAdapter.updateCategories(completedCategories)
            }
        }
    }
}