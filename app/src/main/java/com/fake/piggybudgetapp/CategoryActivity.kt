package com.fake.piggybudgetapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fake.piggybudgetapp.database.CategoryEntity
import com.fake.piggybudgetapp.database.FirebaseDbHelper
import com.fake.piggybudgetapp.databinding.ActivityCategoryBinding
import kotlinx.coroutines.launch

class CategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryBinding
    private var category = CategoryEntity( "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAddCategory.setOnClickListener {
            /// Get user input
            val cname = binding.etCategoryName.text.toString().trim()

            if (cname.isEmpty()) {
                Toast.makeText(this, "Please enter category name", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val date = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(java.util.Date())
            category.name = cname

            // Save to SharedPreferences
            JsonUtils.saveCategoryToPreferences(this@CategoryActivity, category)

            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                FirebaseDbHelper.insertCategory(category)
            }

            Toast.makeText(this, "Category saved to history!", Toast.LENGTH_SHORT).show()
            binding.etCategoryName.setText("")
        }

        binding.btnMyCategories.setOnClickListener {
            val intent = Intent(this, CategoryHistory::class.java)
            startActivity(intent)
        }
        binding.etCategoryName.setOnClickListener{
            val intent = Intent(this, CategoryHistory::class.java)
            startActivity(intent)
        }
    }
}