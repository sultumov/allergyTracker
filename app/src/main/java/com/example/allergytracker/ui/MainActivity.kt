package com.example.allergytracker.ui

import AddAllergyActivity
import AllergyAdapter
import AllergyViewModel
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.allergytracker.R

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: AllergyViewModel
    private lateinit var adapter: AllergyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = AllergyViewModel(application)
        adapter = AllergyAdapter()

        findViewById<RecyclerView>(R.id.rvRecords).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        viewModel.records.observe(this, Observer { records ->
            adapter.submitList(records)
        })

        findViewById<Button>(R.id.btnAddRecord).setOnClickListener {
            startActivity(Intent(this, AddAllergyActivity::class.java))
        }
    }
}