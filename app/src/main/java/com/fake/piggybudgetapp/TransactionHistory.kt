package com.fake.piggybudgetapp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.fake.piggybudgetapp.database.FirebaseDbHelper
import com.fake.piggybudgetapp.databinding.ActivityTransactionHistoryBinding
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