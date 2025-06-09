package com.fake.piggybudgetapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.fake.piggybudgetapp.database.FirebaseDbHelper
import com.fake.piggybudgetapp.databinding.ActivityTransactionHistoryBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TransactionHistory : AppCompatActivity() {

    private lateinit var binding: ActivityTransactionHistoryBinding
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var fDate: String
    private lateinit var tDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // View binding
        binding = ActivityTransactionHistoryBinding.inflate(layoutInflater)
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

        navBar.selectedItemId = R.id.nav_home

        binding.navbar.setOnItemSelectedListener {
                item -> when (item.itemId) {
            R.id.nav_stats -> {
                val intent = Intent(this, StatsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_transactions -> true
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

        binding.btnTransactionsAdd.setOnClickListener{
            val intent = Intent(this, TransactionActivity::class.java)
            startActivity(intent)
            true
        }

        transactionAdapter = TransactionAdapter(emptyList())
        binding.recyclerViewTransaction.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewTransaction.adapter = transactionAdapter


        loadTransactions()

        binding.btnClearHistory.setOnClickListener {
            FirebaseDbHelper.deleteAllTransactions {
                loadTransactions()
                runOnUiThread {
                    Toast.makeText(this, "All transactions deleted!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.llMember1.visibility = View.GONE
        binding.llMember2.visibility = View.GONE
        binding.llMember3.visibility = View.GONE

        binding.btnDisplaySearchForm.setOnClickListener {
            binding.llMember1.visibility = View.VISIBLE
            binding.llMember2.visibility = View.VISIBLE
            binding.llMember3.visibility = View.VISIBLE
        }

        binding.btnDisplayAll.setOnClickListener {
            binding.llMember1.visibility = View.GONE
            binding.llMember2.visibility = View.GONE
            binding.llMember3.visibility = View.GONE
            loadTransactions()
            binding.txtTransacions.text = "Transactions"

        }

        binding.btnSearch.setOnClickListener {

            fDate = binding.etDateFrom.text.toString()
            tDate = binding.etDateTo.text.toString()
            if(fDate.isEmpty() || tDate.isEmpty()) {
                Toast.makeText(this@TransactionHistory, "Enter all details!", Toast.LENGTH_SHORT).show()
            }else{
                loadSearchedTransactions(fD = fDate, tD = tDate)
                binding.txtTransacions.text = "Transactions between ${fDate} and ${tDate}"
            }

        }
    }

    private fun loadTransactions() {
        FirebaseDbHelper.getAllTransactions { allTransactions ->
            // Filter only budgets that have been filled in
            val completedTransactions = allTransactions.filter {
                it.type.isNotBlank() &&
                        it.amount != 0.0 &&
                        it.date.isNotBlank() &&
                        it.category.isNotBlank() &&
                        it.recurrence.isNotBlank()
            }
            runOnUiThread {
                transactionAdapter.updateTransactions(completedTransactions)
            }
        }
    }
    private fun loadSearchedTransactions(fD: String, tD: String) {
        FirebaseDbHelper.getAllTransactions { allTransactions ->
            val searchedTransactions = allTransactions.filter {
                it.date in fD..tD
            }
            // Filter only budgets that have been filled in
            val completedTransactions = searchedTransactions.filter {
                it.type.isNotBlank() &&
                        it.amount != 0.0 &&
                        it.date.isNotBlank() &&
                        it.category.isNotBlank() &&
                        it.recurrence.isNotBlank()
            }

            runOnUiThread {
                transactionAdapter.updateTransactions(completedTransactions)
            }
        }
    }
}