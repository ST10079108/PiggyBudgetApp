package com.fake.piggybudgetapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.fake.piggybudgetapp.database.FirebaseDbHelper
import com.fake.piggybudgetapp.databinding.ActivityCategoryHistoryBinding
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