package com.example.simpleplanner.activities

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.simpleplanner.R
import com.example.simpleplanner.models.StopData
import com.example.simpleplanner.models.StopLocation
import com.example.simpleplanner.repository.RetrofitClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = getString(R.string.nearby_stops)

        checkLocationPermission()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
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
                        val listOfStops = response.body()?.locationList?.stopLocation ?: emptyList()
                        showItems(listOfStops)
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

    private fun getUserLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener {
                queryNearbyStops(it.latitude, it.longitude)
            }
            .addOnFailureListener{
                Toast.makeText(this, getString(R.string.unable_to_get_user_location), Toast.LENGTH_LONG).show()
                Log.e(TAG, "Error: ${it.localizedMessage}")
            }
    }

    private fun showItems(listOfStops: List<StopLocation>) {

    }

    companion object{
        private val TAG = MainActivity::class.java.simpleName
        private const val MY_LOCATION_PERMISSION_CODE = 1000
    }
}