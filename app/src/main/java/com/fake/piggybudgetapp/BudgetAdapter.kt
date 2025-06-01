package com.fake.piggybudgetapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fake.piggybudgetapp.database.BudgetEntity
import com.fake.piggybudgetapp.databinding.ItemBudgetBinding


class BudgetAdapter(private var budgets: List<BudgetEntity>) :
    RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {

    inner class BudgetViewHolder(val binding: ItemBudgetBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val binding = ItemBudgetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BudgetViewHolder(binding)
    }

    override fun getItemCount(): Int = budgets.size

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        val budget = budgets[position]
        holder.binding.tvBudgetName.text = budget.name
        holder.binding.tvBudgetCategory.text = budget.category
        holder.binding.tvBudgetAmount.text = budget.amount.toString()
    }

    fun updateBudgets(newBudgets: List<BudgetEntity>) {
        this.budgets = newBudgets
        notifyDataSetChanged()
    }
}