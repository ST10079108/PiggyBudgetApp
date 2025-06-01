package com.fake.piggybudgetapp.database

import com.fake.piggybudgetapp.database.*
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

object FirebaseDbHelper {

    private val database = FirebaseDatabase.getInstance("https://piggybudget-d8f75-default-rtdb.europe-west1.firebasedatabase.app/")
    private val storageRef = FirebaseStorage.getInstance("gs://piggybudget-d8f75.firebasestorage.app").reference

    //User Methods
    private val usersDb = database.getReference("users")

    fun insertUser(user: UserEntity, onComplete: () -> Unit = {}) {
        val key = usersDb.push().key ?: return
        usersDb.child(key).setValue(user).addOnCompleteListener {
            onComplete()
        }
    }

    fun getAllUsers(callback: (List<UserEntity>) -> Unit) {
        usersDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<UserEntity>()
                snapshot.children.forEach {
                    it.getValue(UserEntity::class.java)?.let { user -> list.add(user) }
                }
                callback(list)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    fun deleteAllUsers(onComplete: () -> Unit = {}) {
        usersDb.removeValue().addOnCompleteListener {
            onComplete()
        }
    }

    //Transaction Methods
    private val transactionsDb = database.getReference("transactions")

    fun insertTransaction(transaction: TransactionEntity,
                          imageBytes: ByteArray?, onComplete: () -> Unit = {}) {
        if (imageBytes != null) {
            val imageRef = FirebaseStorage.getInstance("gs://piggybudget-d8f75.firebasestorage.app").reference
                .child("images/${System.currentTimeMillis()}.png")

            imageRef.putBytes(imageBytes)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        transaction.imageUrl = uri.toString()

                        // Now insert transaction with imageUrl
                        val key = transactionsDb.push().key ?: return@addOnSuccessListener
                        transactionsDb.child(key).setValue(transaction).addOnCompleteListener {
                            onComplete()
                        }
                    }
                }
                .addOnFailureListener {
                    // Failed to upload image, still save transaction without image
                    val key = transactionsDb.push().key ?: return@addOnFailureListener
                    transactionsDb.child(key).setValue(transaction).addOnCompleteListener {
                        onComplete()
                    }
                }
        } else {
            val key = transactionsDb.push().key ?: return
            transactionsDb.child(key).setValue(transaction).addOnCompleteListener {
                onComplete()
            }
        }
    }

    fun getAllTransactions(callback: (List<TransactionEntity>) -> Unit) {
        transactionsDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<TransactionEntity>()
                snapshot.children.forEach {
                    it.getValue(TransactionEntity::class.java)?.let { txn -> list.add(txn) }
                }
                callback(list)
            }
            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    fun getTransactionCategoryTotals(callback: (List<CategoryTotal>) -> Unit) {
        getAllTransactions { transList ->
            val exTransactions = transList.filter {
                it.type == "Expense"
            }
            val catTotals = exTransactions
                .groupBy { it.category }.map { (category, transactions) ->
                    CategoryTotal(category, transactions.sumOf { it.amount })
                }
            callback(catTotals)
        }
    }

    fun getTransactionCategoryTotalsBetweenDates(fromDate: String, toDate: String, callback: (List<CategoryTotal>) -> Unit) {
        getAllTransactions { transList ->
            val filteredTransactions = transList.filter {
                it.type == "Expense" &&
                        it.date >= fromDate &&
                        it.date <= toDate
            }
            val categoryTotals = filteredTransactions
                .groupBy { it.category }.map { (category, transactions) ->
                    CategoryTotal(category, transactions.sumOf { it.amount })
                }
            callback(categoryTotals)
        }
    }

    fun deleteAllTransactions(onComplete: () -> Unit = {}) {
        transactionsDb.removeValue().addOnCompleteListener {
            onComplete()
        }
    }

    fun uploadImage(imageBytes: ByteArray, onComplete: (String?) -> Unit) {
        val imageRef = storageRef.child("images/${System.currentTimeMillis()}.png")
        val uploadTask = imageRef.putBytes(imageBytes)

        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                onComplete(uri.toString())
            }
        }.addOnFailureListener {
            onComplete(null)
        }
    }


    //Goal Methods
    private val goalsDb = database.getReference("goals")

    fun insertGoal(goal: GoalEntity, onComplete: () -> Unit = {}) {
        val key = goalsDb.push().key ?: return
        goalsDb.child(key).setValue(goal).addOnCompleteListener {
            onComplete()
        }
    }

    fun getAllGoals(callback: (List<GoalEntity>) -> Unit) {
        goalsDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<GoalEntity>()
                snapshot.children.forEach {
                    it.getValue(GoalEntity::class.java)?.let { goal -> list.add(goal) }
                }
                callback(list)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    fun deleteAllGoals(onComplete: () -> Unit = {}) {
        goalsDb.removeValue().addOnCompleteListener {
            onComplete()
        }
    }

    //Category Methods
    private val categoriesDb = database.getReference("categories")

    fun insertCategory(category: CategoryEntity, onComplete: () -> Unit = {}) {
        val key = categoriesDb.push().key ?: return
        categoriesDb.child(key).setValue(category).addOnCompleteListener {
            onComplete()
        }
    }

    fun getAllCategories(callback: (List<CategoryEntity>) -> Unit) {
        categoriesDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<CategoryEntity>()
                snapshot.children.forEach {
                    it.getValue(CategoryEntity::class.java)?.let { cat -> list.add(cat) }
                }
                callback(list)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    fun deleteAllCategories(onComplete: () -> Unit = {}) {
        categoriesDb.removeValue().addOnCompleteListener {
            onComplete()
        }
    }

    //Budget Methods
    private val budgetsDb = database.getReference("budgets")

    fun insertBudget(budget: BudgetEntity, onComplete: () -> Unit = {}) {
        val key = budgetsDb.push().key ?: return
        budgetsDb.child(key).setValue(budget).addOnCompleteListener {
            onComplete()
        }
    }

    fun getAllBudgets(callback: (List<BudgetEntity>) -> Unit) {
        budgetsDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<BudgetEntity>()
                snapshot.children.forEach {
                    it.getValue(BudgetEntity::class.java)?.let { budget -> list.add(budget) }
                }
                callback(list)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    fun deleteAllBudgets(onComplete: () -> Unit = {}) {
        budgetsDb.removeValue().addOnCompleteListener {
            onComplete()
        }
    }

}