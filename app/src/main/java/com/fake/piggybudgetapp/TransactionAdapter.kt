package com.fake.piggybudgetapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fake.piggybudgetapp.database.TransactionEntity
import com.fake.piggybudgetapp.databinding.ItemTransactionBinding


class TransactionAdapter(private var transactions: List<TransactionEntity>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun getItemCount(): Int = transactions.size

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]

        holder.binding.tvTransactionType.text = transaction.type
        holder.binding.tvTransactionAmount.text = transaction.amount.toString()
        holder.binding.tvTransactionDate.text = transaction.date
        holder.binding.tvTransactionCategory.text = transaction.category
        holder.binding.tvTransactionDescription.text = transaction.description
        holder.binding.tvTransactionRecurrence.text = transaction.recurrence
        if (!transaction.imageUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(transaction.imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .into(holder.binding.ivTransactionImage)
        } else {
            holder.binding.ivTransactionImage.setImageResource(R.drawable.placeholder_image)
        }
    }

    fun updateTransactions(newTransactions: List<TransactionEntity>) {
        this.transactions = newTransactions
        notifyDataSetChanged()
    }
}