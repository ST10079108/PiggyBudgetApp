package com.fake.piggybudgetapp

import android.content.Intent
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fake.piggybudgetapp.databinding.ActivityMainBinding
import com.fake.piggybudgetapp.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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

        // View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Insert preload data into DB
        insertUsers()
        insertBudgets()
        insertTransactions()
        insertCategories()

        binding.btnSignIn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


    }

    // Insert hardcoded data into database
    private fun insertUsers() {
        CoroutineScope(Dispatchers.IO).launch {
            FirebaseDbHelper.getAllUsers { userList ->
                if (userList.isEmpty()) {
                    val users = listOf(
                        UserEntity(username = "User", password = "U4")
                    )
                    users.forEach { user ->
                        FirebaseDbHelper.insertUser(user)
                    }
                }
            }
        }
    }

    private fun insertCategories() {
        CoroutineScope(Dispatchers.IO).launch {
            FirebaseDbHelper.getAllCategories { catList ->
                if (catList.isEmpty()) {
                    val categories = listOf(
                        CategoryEntity(name = "Groceries"),
                        CategoryEntity(name = "Salary")
                    )
                    categories.forEach { category ->
                        FirebaseDbHelper.insertCategory(category)
                    }
                }
            }
        }
    }

    private fun insertBudgets() {
        CoroutineScope(Dispatchers.IO).launch {
            FirebaseDbHelper.getAllBudgets { budgetList ->
                if (budgetList.isEmpty()) {
                    val budgets = listOf(
                        BudgetEntity(name = "Budget 1", amount = 200.0, category = "Groceries")
                    )
                    budgets.forEach { budget ->
                        FirebaseDbHelper.insertBudget(budget)
                    }
                }
            }
        }
    }

    private fun insertTransactions() {
        CoroutineScope(Dispatchers.IO).launch {
            FirebaseDbHelper.getAllTransactions { transList ->
                if (transList.isEmpty()) {
                    val transactions = listOf(
                        TransactionEntity(
                            type = "Expense",
                            amount = 90.0,
                            date = "2025-05-24",
                            description = "Makro",
                            recurrence = "Monthly",
                            category = "Groceries",
                            imageUrl = ""
                        ),
                        TransactionEntity(
                            type = "Income",
                            amount = 150.0,
                            date = "2025-05-24",
                            description = "Microsoft",
                            recurrence = "Monthly",
                            category = "Salary",
                            imageUrl = ""
                        )
                    )
                    transactions.forEach { transaction ->
                        FirebaseDbHelper.insertTransaction(transaction, null)
                    }
                }
            }
        }
    }
}