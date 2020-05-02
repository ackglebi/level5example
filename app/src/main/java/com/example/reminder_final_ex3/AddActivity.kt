package com.example.reminder_final_ex3

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.content_add.*

const val EXTRA_REMINDER = "EXTRA_REMINDER"

class AddActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initViews()
    }

    private fun initViews() {
        fab.setOnClickListener {
            onSaveClick()
        }
    }

    private fun onSaveClick() {
        // check if input is empty
        if (etAddReminder.text.toString().isNotBlank()) {
            // create object of the reminder
            val reminder = Reminder(etAddReminder.text.toString())
            val resultIntent = Intent()

            resultIntent.putExtra(EXTRA_REMINDER, reminder)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()

        } else {
            Toast.makeText(this, "The reminder can't be empty",
                Toast.LENGTH_SHORT).show()
        }
    }


}
