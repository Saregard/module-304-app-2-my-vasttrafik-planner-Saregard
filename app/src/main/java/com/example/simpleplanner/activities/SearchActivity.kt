package com.example.simpleplanner.activities

import android.R
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.simpleplanner.databinding.ActivitySearchBinding
import com.example.simpleplanner.models.StopLocation
import com.example.simpleplanner.util.StopsProvider
import com.google.gson.Gson
import java.lang.Exception
import java.util.*


class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchView.onActionViewExpanded()

        searchResults(StopsProvider.gbgStops(resources, true))
    }

    private fun searchResults(stops: List<StopLocation>) {
        val listOfStops = stops.map { it.name }

        val stopsAdapter : ArrayAdapter<String> = ArrayAdapter(
            this, android.R.layout.simple_list_item_1, listOfStops)

        binding.searchListView.adapter = stopsAdapter

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchView.clearFocus()
                if (listOfStops.contains(query)){
                    stopsAdapter.filter.filter(query)
                }else{
                    Toast.makeText(applicationContext,"Item not found", Toast.LENGTH_LONG).show()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                stopsAdapter.filter.filter(newText)
                return false
            }
        })
    }
}
