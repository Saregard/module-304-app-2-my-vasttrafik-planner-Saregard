package com.example.simpleplanner.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simpleplanner.R
import com.example.simpleplanner.adapter.StopsRecyclerViewAdapter
import com.example.simpleplanner.databinding.ActivityMainBinding
import com.example.simpleplanner.models.StopData
import com.example.simpleplanner.models.StopLocation
import com.example.simpleplanner.repository.RetrofitClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private var userLocation: Location? = null

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.nearby_stops)

        checkLocationPermission()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            MY_LOCATION_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                    getUserLocation()
                }else {
                    Toast.makeText(this, R.string.please_enable_location, Toast.LENGTH_LONG).show()
                }
            }
            else -> {
            }
        }
    }

    private fun queryNearbyStops(latitude: Double, longitude: Double) {
        RetrofitClient
            .instance
            .getNearbyStops(latitude, longitude)
            .enqueue(object: Callback<StopData> {
                override fun onResponse(call: Call<StopData>, response: Response<StopData>) {
                    if (response.isSuccessful) {
                        val filteredListOfStops =
                            response.body()?.locationList?.stopLocation?.distinctBy { it.name }
                            ?: emptyList()
                        showItems(filteredListOfStops)
                    }else {
                        val message = when(response.code()){
                            401 -> R.string.update_your_api_key
                            500 -> R.string.internal_server_error
                            else -> R.string.unable_to_retrieve_nearby_stops
                        }
                        Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
                        Log.e(TAG,"Error: $response")
                    }
                }

                override fun onFailure(call: Call<StopData>, t: Throwable) {
                    Toast.makeText(this@MainActivity, R.string.unable_to_retrieve_nearby_stops, Toast.LENGTH_LONG).show()
                    Log.e(TAG, "Error: ${t.localizedMessage}")
                }
            })
    }

    private fun checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getUserLocation()
        }else{
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_LOCATION_PERMISSION_CODE)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getUserLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener {
                userLocation = it
                queryNearbyStops(it.latitude, it.longitude)
            }
            .addOnFailureListener{
                Toast.makeText(this, getString(R.string.unable_to_get_user_location), Toast.LENGTH_LONG).show()
                Log.e(TAG, "Error: ${it.localizedMessage}")
            }
    }

    private fun showItems(listOfStops: List<StopLocation>) {
        binding.mainActivityProgressBar.visibility = View.GONE

        val recyclerViewAdapter = StopsRecyclerViewAdapter(listOfStops,userLocation, this) {
            val intent = Intent(this, DepartureActivity::class.java)
            intent.putExtra(DepartureActivity.KEY_STOP,it)
            startActivity(intent)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.mainActivityRecyclerView)
        recyclerView?.apply {
            setHasFixedSize(true)
            adapter = recyclerViewAdapter
            layoutManager = LinearLayoutManager(context)
            visibility = View.VISIBLE
        }
    }

    companion object{
        private val TAG = MainActivity::class.java.simpleName
        private const val MY_LOCATION_PERMISSION_CODE = 1000
    }
}