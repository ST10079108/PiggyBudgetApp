package com.fake.piggybudgetapp

import android.content.Context
import android.content.Intent
import android.os.Bundle

fun openIntent(context: Context, user: String,
               activityToOpen: Class<*>) {
    // declare intent with context and class to pass the value to
    val intent = Intent(context, activityToOpen)
    // pass through the string value with key "order"
    intent.putExtra("user", user)
    // start the activity
    context.startActivity(intent)
}

fun shareIntent(context: Context, user: String) {
    var sendIntent = Intent()
    // set the action to indicate what to do - send in this case
    sendIntent.setAction(Intent.ACTION_SEND)
    sendIntent.putExtra(Intent.EXTRA_TEXT, user)
    // we are sending plain text
    sendIntent.setType("text/plain")
    // show the share sheet
    var shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}

