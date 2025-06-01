package com.fake.piggybudgetapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.fake.piggybudgetapp.database.FirebaseDbHelper
import com.fake.piggybudgetapp.databinding.ActivityProfileBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

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