package com.example.simpleplanner.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simpleplanner.R
import com.example.simpleplanner.adapter.DepartureRecyclerViewAdapter
import com.example.simpleplanner.models.BusData
import com.example.simpleplanner.models.Departure
import com.example.simpleplanner.models.StopLocation
import com.example.simpleplanner.repository.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class DepartureActivity : AppCompatActivity() {

    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_departure)

        val stop = intent.getParcelableExtra<StopLocation>(KEY_STOP)

        stop?.let {

            progressBar = findViewById(R.id.activityDepartureProgressBar)
            supportActionBar?.apply {
                title = it.name
                setDisplayHomeAsUpEnabled(true)
                setHomeButtonEnabled(true)
            }

            queryBussesForStop(it.id.toLong())

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun queryBussesForStop(stopId: Long) {
        RetrofitClient
            .instance
            .getDepartureTimesFromStop(stopId = stopId, date = getDate(), time = getTime())
            .enqueue(object: Callback<BusData> {
                override fun onResponse(call: Call<BusData>, response: Response<BusData>) {
                    if (response.isSuccessful){
                        val listOfDepartures = response.body()?.departureBoard?.departure ?: emptyList()
                        showItems(listOfDepartures)
                    }else{
                        val message = when (response.code()){
                            401 -> R.string.update_your_api_key
                            500 -> R.string.internal_server_error
                            else -> R.string.unable_to_retrieve_nearby_stops
                        }
                        Toast.makeText(this@DepartureActivity, message, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<BusData>, t: Throwable) {
                    Toast.makeText(this@DepartureActivity, "onFailure", Toast.LENGTH_LONG).show()
                    Log.e(TAG, "Error: ${t.localizedMessage}")
                }
            })
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDate (): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        return dateFormat.format(Date())
    }

    @SuppressLint("SimpleDateFormat")
    private fun getTime (): String {
        val timeFormat = SimpleDateFormat("yyyy-MM-dd")
        return timeFormat.format(Date())
    }

    private fun showItems(listOfDeparture: List<Departure>){
        progressBar?.visibility = View.GONE

        val recyclerViewAdapter = DepartureRecyclerViewAdapter(listOfDeparture, this)

        val recyclerView = findViewById<RecyclerView>(R.id.activityDepartureRecyclerView)
        recyclerView?.apply{
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = recyclerViewAdapter
            visibility = View.VISIBLE
        }
    }

    companion object {
        const val KEY_STOP = "keyStop"
        private val TAG = DepartureActivity::class.java.simpleName
    }
}