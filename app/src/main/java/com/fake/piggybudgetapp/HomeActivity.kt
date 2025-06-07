package com.fake.piggybudgetapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.fake.piggybudgetapp.database.*
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.fake.piggybudgetapp.databinding.ActivityHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {

    private var goal = GoalEntity( 0.0, 0.0)
    private lateinit var binding: ActivityHomeBinding
    private var user = UserEntity( "", "")
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // View binding
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        actionBarDrawerToggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar, R.string.nav_open, R.string.nav_close
        )

        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        // Navigation menu items
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_budgets -> {
                    startActivity(Intent(this, BudgetActivity::class.java))
                }
                R.id.nav_transactions -> {
                    startActivity(Intent(this, TransactionActivity::class.java))
                }
                R.id.nav_categories -> {
                    startActivity(Intent(this, CategoryActivity::class.java))
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                }
                R.id.nav_stats -> {
                    Toast.makeText(this, "Stats clicked", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_logout -> {
                    Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
                    finish()
                }
                R.id.nav_cat_totals -> {
                    startActivity(Intent(this, StatsActivity::class.java))
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        val userFromPrefs = JsonUtils.getUserFromPreferences(this)
        userFromPrefs?.let { user = it }

        val goalFromPrefs = JsonUtils.getGoalFromPreferences(this)
        goalFromPrefs?.let { goal = it }

        binding.tvUsername.text = "Welcome ${user.username}!"

        FirebaseDbHelper.getAllGoals {
            allGoals -> if (allGoals.isNotEmpty()){
                binding.cvGoal.visibility = View.GONE
                binding.tvGoalAmount.text = "R${allGoals.last().maximumAmount}"

            }else{
            binding.cvGoal.visibility = View.VISIBLE
            }
        }

        loadAchievements()

        binding.btnSaveGoal.setOnClickListener{
            //Get user input
            val gmin = binding.etMin.text.toString().trim().toDoubleOrNull() ?: 0.0
            val gmax = binding.etMax.text.toString().trim().toDoubleOrNull() ?: 0.0

            if ( gmin == 0.0 || gmax == 0.0) {
                Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            //Update the budget object
            goal.minimumAmount = gmin
            goal.maximumAmount = gmax

            binding.tvGoalAmount.text = "R${goal.maximumAmount}"
            binding.cvGoal.visibility = View.GONE

            //Save to SharedPreferences
            JsonUtils.saveGoalToPreferences(this@HomeActivity, goal)


            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                FirebaseDbHelper.insertGoal(goal)
            }

            Toast.makeText(this, "Goal: ${goal.minimumAmount} and ${goal.maximumAmount}", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, BudgetActivity::class.java)
            startActivity(intent)
            loadAchievements()
        }

        lifecycleScope.launch {
            FirebaseDbHelper.getAllTransactions { transList ->
                var exTotal = 0.0
                var inTotal = 0.0
                for (i in transList) {
                    when (i.type) {
                        "Expense" -> exTotal += i.amount
                        "Income"  -> inTotal += i.amount
                    }
                }
                binding.tvIncomeAmount.text = "R $inTotal"
                binding.tvExpensesAmount.text = "R $exTotal"
                binding.tvBalanceAmount.text = "R ${(inTotal - exTotal)}"
            }
        }
        loadAchievements()
    } //end of onCreate

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    private fun loadAchievements() {
        // Bronze
        Log.d("ACHIEVEMENTS", "Calling getAllGoals...")
        FirebaseDbHelper.getAllGoals { goals ->
            Log.d("ACHIEVEMENTS", "Goals fetched: ${goals.size}")
            val bronzeProgress = goals.size
            runOnUiThread {
                binding.pbBronze.max = 1
                binding.pbBronze.progress = bronzeProgress.coerceAtMost(1)
                if (binding.pbBronze.progress == binding.pbBronze.max) {
                    showAchievementDialog("Bronze")
                }
            }
        }

        // Silver
        FirebaseDbHelper.getAllBudgets { budgets ->
            val silverProgress = budgets.size
            runOnUiThread {
                binding.pbSilver.max = 5
                binding.pbSilver.progress = silverProgress.coerceAtMost(5)
                if (binding.pbSilver.progress == binding.pbSilver.max) {
                    showAchievementDialog("Silver")
                }
            }
        }

        // Gold
        FirebaseDbHelper.getAllTransactions { transactions ->
            val goldProgress = transactions.size
            runOnUiThread {
                binding.pbGold.max = 5
                binding.pbGold.progress = goldProgress.coerceAtMost(5)
                if (binding.pbGold.progress == binding.pbGold.max) {
                    showAchievementDialog("Gold")
                }
            }
        }

        // Elite
        FirebaseDbHelper.getAllCategories { categories ->
            val eliteProgress = categories.size
            runOnUiThread {
                binding.pbElite.max = 10
                binding.pbElite.progress = eliteProgress.coerceAtMost(10)
                if (binding.pbElite.progress == binding.pbElite.max) {
                    showAchievementDialog("Elite")
                }
            }
        }
    }

    private fun showAchievementDialog(achievement: String) {
        AlertDialog.Builder(this)
            .setTitle("Achievement Unlocked!")
            .setMessage("You've unlocked the $achievement achievement!")
            .setPositiveButton("Nice!") { dialog, _ -> dialog.dismiss() }
            .show()
    }

}