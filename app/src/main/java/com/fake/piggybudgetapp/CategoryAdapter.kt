package com.fake.piggybudgetapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fake.piggybudgetapp.databinding.ItemCategoryBinding
import com.fake.piggybudgetapp.database.CategoryEntity

class CategoryAdapter(
    private var categories: List<CategoryEntity>,
    private val onItemClick: (CategoryEntity) -> Unit) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun getItemCount(): Int = categories.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.binding.tvCategoryName.text = category.name

        holder.itemView.setOnClickListener {
            onItemClick(category)
        }
    }

    fun updateCategories(newCategories: List<CategoryEntity>) {
        this.categories = newCategories
        notifyDataSetChanged()
    }
}