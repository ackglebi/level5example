package com.example.reminder_final_ex3

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    private val reminders = arrayListOf<Reminder>()
    private val reminderAdapter = ReminderAdapter(reminders)
//    private lateinit var reminderRepository: ReminderRepository
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

//        reminderRepository = ReminderRepository(this)
        initViews()
        observeViewModel()
    }

    private fun startAddActivity() {
        val intent = Intent(this, AddActivity::class.java)
        startActivityForResult(intent, ADD_REMINDER_REQUEST_CODE)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initViews() {
        // Initialize the recycler view with a linear layout manager adapter

        // add linear layout manager
        rvReminders.layoutManager = LinearLayoutManager(this@MainActivity,
            RecyclerView.VERTICAL, false)

        // define adapter
        rvReminders.adapter = reminderAdapter

        // add item decoration
        rvReminders.addItemDecoration(DividerItemDecoration(this@MainActivity,
            DividerItemDecoration.VERTICAL))

        createItemTouchHelper().attachToRecyclerView(rvReminders)

        fab.setOnClickListener {
            startAddActivity()
        }
    }

    private fun observeViewModel() {
        viewModel.reminders.observe(this, Observer{ reminders ->
            this@MainActivity.reminders.clear()
            this@MainActivity.reminders.addAll(reminders)
            reminderAdapter.notifyDataSetChanged()
        })
    }


    /**
     * retrieve data (reminder object) from other view
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                ADD_REMINDER_REQUEST_CODE -> {
                    data?.let{safeData ->
                        val reminder = safeData.getParcelableExtra<Reminder>(EXTRA_REMINDER)
                        reminder?.let { safeReminder ->

                            viewModel.insertReminder(safeReminder)
                        } ?: run {
                            Log.e("TAG", "Reminder is null")
                        }
                    } ?: run {
                        Log.e("TAG", "null intent data received")
                    }
                }
            }
        }
    }

    private fun createItemTouchHelper() : ItemTouchHelper {
        val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val remindersToDelete = reminders[position]
                viewModel.deleteReminder(remindersToDelete)
            }
        }

        return ItemTouchHelper(callback)
    }

    companion object {
        const val ADD_REMINDER_REQUEST_CODE = 100
    }
}
