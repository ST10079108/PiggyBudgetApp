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
import androidx.lifecycle.lifecycleScope
import com.fake.piggybudgetapp.database.FirebaseDbHelper
import com.fake.piggybudgetapp.databinding.ActivityProfileBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Hide phone's ui
        window.setDecorFitsSystemWindows(false)
        window.insetsController?.let { controller ->
            controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            controller.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        // View binding
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Stop bottomnavigationbar from auto adding padding on startup
        val navBar = findViewById<BottomNavigationView>(R.id.navbar)
        navBar.setOnApplyWindowInsetsListener { view, insets ->
            view.setPadding(view.paddingLeft, view.paddingTop, view.paddingRight, 0)
            insets
        }

        navBar.selectedItemId = R.id.nav_profile

        binding.navbar.setOnItemSelectedListener {
                item -> when (item.itemId) {
            R.id.nav_stats -> true
            R.id.nav_transactions -> {
                val intent = Intent(this, TransactionActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_home -> {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_budget -> {
                val intent = Intent(this, BudgetActivity::class.java)
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

        binding.btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            startActivity(intent)
            finish()
        }
        lifecycleScope.launch {
            FirebaseDbHelper.getAllUsers{
                allUsers ->
                if (allUsers.isNotEmpty()) {
                    val uName = allUsers.first().username
                    val pWord = allUsers.first().password
                    binding.txtUsername.text = "Username: ${uName}"
                    binding.txtPassword.text = "Password: ${pWord}"
                }
            }
                FirebaseDbHelper.getAllGoals {
                    allGoals ->
                    if (allGoals.isNotEmpty()) {
                        val min = allGoals.first().minimumAmount
                        val max = allGoals.first().maximumAmount
                        binding.txtMinGoal.text = "Minimum Goal: ${min}"
                        binding.txtMaxGoal.text = "Maximum Goal: ${max}"
                    }
                }

                FirebaseDbHelper.getAllTransactions { transList ->
                    var total = transList.size
                    binding.txtTotalTransactions.text = "Transactions: $total"
                }

                FirebaseDbHelper.getAllBudgets { budgetList ->
                    var total = budgetList.size
                    binding.txtTotalBudgets.text = "Budgets: $total"
                }

        }
    }
}