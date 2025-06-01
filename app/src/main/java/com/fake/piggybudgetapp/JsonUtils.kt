package com.fake.piggybudgetapp

import android.content.Context
import com.fake.piggybudgetapp.database.UserEntity
import com.fake.piggybudgetapp.database.BudgetEntity
import com.fake.piggybudgetapp.database.CategoryEntity
import com.fake.piggybudgetapp.database.GoalEntity
import com.fake.piggybudgetapp.database.TransactionEntity
import com.google.gson.Gson

object JsonUtils {
    private val gson = Gson()

    // User
    fun userToJson(user: UserEntity): String = gson.toJson(user)
    fun jsonToUser(json: String): UserEntity = gson.fromJson(json, UserEntity::class.java)

    fun saveUserToPreferences(context: Context, user: UserEntity) {
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("user_key", userToJson(user))
            apply()
        }
    }

    fun getUserFromPreferences(context: Context): UserEntity? {
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val jsonString = sharedPreferences.getString("user_key", null)
        return jsonString?.let { jsonToUser(it) }
    }

    // Budget
    fun budgetToJson(budget: BudgetEntity): String = gson.toJson(budget)
    fun jsonToBudget(json: String): BudgetEntity = gson.fromJson(json, BudgetEntity::class.java)

    fun saveBudgetToPreferences(context: Context, budget: BudgetEntity) {
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("budget_key", budgetToJson(budget))
            apply()
        }
    }

    fun getBudgetFromPreferences(context: Context): BudgetEntity? {
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val jsonString = sharedPreferences.getString("budget_key", null)
        return jsonString?.let { jsonToBudget(it) }
    }

    // Category
    fun categoryToJson(category: CategoryEntity): String = gson.toJson(category)
    fun jsonToCategory(json: String): CategoryEntity = gson.fromJson(json, CategoryEntity::class.java)

    fun saveCategoryToPreferences(context: Context, category: CategoryEntity) {
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("category_key", categoryToJson(category))
            apply()
        }
    }

    fun getCategoryFromPreferences(context: Context): CategoryEntity? {
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val jsonString = sharedPreferences.getString("category_key", null)
        return jsonString?.let { jsonToCategory(it) }
    }

    // Transaction
    fun transactionToJson(transaction: TransactionEntity): String = gson.toJson(transaction)
    fun jsonToTransaction(json: String): TransactionEntity = gson.fromJson(json, TransactionEntity::class.java)

    fun saveTransactionToPreferences(context: Context, transaction: TransactionEntity) {
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("transaction_key", transactionToJson(transaction))
            apply()
        }
    }

    fun getTransactionFromPreferences(context: Context): TransactionEntity? {
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val jsonString = sharedPreferences.getString("transaction_key", null)
        return jsonString?.let { jsonToTransaction(it) }
    }

    // Goal
    fun goalToJson(goal: GoalEntity): String = gson.toJson(goal)
    fun jsonToGoal(json: String): GoalEntity = gson.fromJson(json, GoalEntity::class.java)

    fun saveGoalToPreferences(context: Context, goal: GoalEntity) {
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("goal_key", goalToJson(goal))
            apply()
        }
    }

    fun getGoalFromPreferences(context: Context): GoalEntity? {
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val jsonString = sharedPreferences.getString("goal_key", null)
        return jsonString?.let { jsonToGoal(it) }
    }
}