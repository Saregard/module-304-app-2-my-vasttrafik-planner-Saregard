package com.example.simpleplanner.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.simpleplanner.databinding.ActivitySearchBinding
import com.example.simpleplanner.models.StopLocation
import com.example.simpleplanner.util.StopsProvider

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchView.onActionViewExpanded()

        searchResults(StopsProvider.gbgStops(resources))
    }

    private fun searchResults(stops: List<StopLocation>) {
        var filteredList = stops

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
                filteredList = stops.filter { it.name.contains(newText.toString(), ignoreCase = true) }
                return false
            }
        })

        binding.searchListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val clickedItem = filteredList[position]
            val intent = Intent(this, DepartureActivity::class.java)
            intent.putExtra(DepartureActivity.KEY_STOP, clickedItem)
            startActivity(intent)


        }
    }
}
