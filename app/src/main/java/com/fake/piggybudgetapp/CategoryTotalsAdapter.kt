package com.fake.piggybudgetapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fake.piggybudgetapp.databinding.ItemCategoryTotalsBinding
import com.fake.piggybudgetapp.database.CategoryTotal



class CategoryTotalsAdapter(private var transactions: List<CategoryTotal>) :
    RecyclerView.Adapter<CategoryTotalsAdapter.CategoryTotalsViewHolder>() {

    inner class CategoryTotalsViewHolder(val binding: ItemCategoryTotalsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryTotalsViewHolder {
        val binding = ItemCategoryTotalsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryTotalsViewHolder(binding)
    }

    override fun getItemCount(): Int = transactions.size

    override fun onBindViewHolder(holder: CategoryTotalsViewHolder, position: Int) {

        val transaction = transactions[position]
        holder.binding.tvCategoryName.text = transaction.name
        holder.binding.tvCategoryTotal.text = "R${transaction.total}"
    }

    fun updateCategoryTotals(newTotals: List<CategoryTotal>) {
        this.transactions = newTotals
        notifyDataSetChanged()
    }


}