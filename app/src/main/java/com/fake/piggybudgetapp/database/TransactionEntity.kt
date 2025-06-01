package com.fake.piggybudgetapp.database




data class TransactionEntity(

    var type: String = "", //income or expense
    var amount: Double = 0.0,
    var date: String = "", // dd/mm/yy
    var description: String = "",
    var imageUrl: String? = null,
    var recurrence: String = "", // no, weekly, monthly, yearly
    var category: String = "" //groceries, entertainment, salary, rent
)
