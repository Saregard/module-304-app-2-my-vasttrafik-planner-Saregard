package com.example.simpleplanner.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.simpleplanner.R
import com.example.simpleplanner.databinding.ActivityMapBinding
import com.example.simpleplanner.models.StopLocation
import com.google.gson.Gson
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import java.lang.Exception
import java.net.URI
import java.util.*

class MapActivity : AppCompatActivity() {

    private var stopCardView: View? = null

    private lateinit var binding: ActivityMapBinding
    private var mapStyle: Style? = null
    private var mapboxMap: MapboxMap? = null
    private var focusedStop: StopLocation? = null
    private val listOfGbgStops: List<StopLocation> by lazy {
        val inputStream = resources.openRawResource(R.raw.gbg_stops)
        try {
            val scanner = Scanner(inputStream)
            val stringBuilder = StringBuilder()
            while (scanner.hasNextLine()){
                stringBuilder.append(scanner.nextLine())
            }
            Gson().fromJson(stringBuilder.toString(), Array<StopLocation>::class.java).asList()
        }catch(exception: Exception){
            emptyList<StopLocation>()
        }finally{
            inputStream.close()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))

        setTheme(R.style.Theme_SimplePlannerMap)

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        stopCardView = findViewById(R.id.layoutStopInfo)
        stopCardView?.setOnClickListener {
            val intent = Intent(this, DepartureActivity::class.java)
            intent.putExtra(DepartureActivity.KEY_STOP, focusedStop)
            startActivity(intent)
        }

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync{mapboxMap ->
            this.mapboxMap = mapboxMap
            mapboxMap.setStyle(Style.MAPBOX_STREETS){
                mapStyle = it
                it.addImage(
                    ICON_ID,
                    BitmapFactory.decodeResource(resources, R.drawable.mapbox_marker_icon_default)
                )
                configureStopIcons()
                configureUserLocation()
            }
        }
    }

    private fun configureStopIcons() {
        mapStyle?.let {
            val symbolList = ArrayList<SymbolOptions>()
            listOfGbgStops.forEach { stop ->
                val symbolOption = SymbolOptions()
                    .withLatLng(LatLng(stop.lat.toDouble(), stop.lon.toDouble()))
                    .withIconImage(ICON_ID)
                symbolList.add(symbolOption)
            }
            val symbolManager = SymbolManager(binding.mapView, mapboxMap!!, it).apply {
                iconAllowOverlap = true
                iconIgnorePlacement = true
                create(symbolList)
            }

            symbolManager.addClickListener {symbol ->
                setCameraPosition(symbol.latLng.latitude, symbol.latLng.longitude, true)

                val stop = listOfGbgStops.first {stop ->
                    stop.lat.toDouble() == symbol.latLng.latitude && stop.lon.toDouble() == symbol.latLng.longitude
                }
                focusedStop = stop
                showStopInfo(stop)
                true
            }
        }
    }

    private fun configureUserLocation() {
        if (!PermissionsManager.areLocationPermissionsGranted(this)) {
            Toast.makeText(this, R.string.please_enable_location, Toast.LENGTH_LONG).show()

            setCameraPosition(GBG_CENTER.latitude, GBG_CENTER.longitude)
        }else {
            enableLocationComponent()
        }
    }

    private fun setCameraPosition(lat: Double, lon: Double, withEase: Boolean = false) {
        val position = CameraPosition.Builder()
            .target(LatLng(lat, lon))
            .zoom(14.0)
            .tilt(20.0)
            .build()

        if (withEase) {
            mapboxMap?.easeCamera(CameraUpdateFactory.newCameraPosition(position))
        }else {
            mapboxMap?.cameraPosition = position
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent() {
        if (mapStyle != null && mapboxMap != null) {
            val customLocationComponentOption = LocationComponentOptions.builder(this)
                .pulseEnabled(true)
                .build()

            val locationComponent = mapboxMap!!.locationComponent
            locationComponent.activateLocationComponent(
                LocationComponentActivationOptions.builder(this, mapStyle!!)
                    .locationComponentOptions(customLocationComponentOption)
                    .build()
            )

            locationComponent.isLocationComponentEnabled = true

            locationComponent.cameraMode = CameraMode.TRACKING

            locationComponent.renderMode = RenderMode.NORMAL

            locationComponent.locationEngine?.getLastLocation(object: LocationEngineCallback<LocationEngineResult>{
                override fun onSuccess(result: LocationEngineResult?) {
                    result?.lastLocation?.let {
                        setCameraPosition(it.latitude, it.longitude)
                    }
                }

                override fun onFailure(exception: Exception) {
                    Toast.makeText(this@MapActivity, R.string.unable_to_get_user_location, Toast.LENGTH_LONG).show()
                    setCameraPosition(GBG_CENTER.latitude, GBG_CENTER.longitude)
                }
            })

        }
    }

    private fun showStopInfo(stop: StopLocation) {
        stopCardView?.visibility = View.VISIBLE

        findViewById<TextView>(R.id.recyclerViewTextViewStopName)?.text = stop.name

        if (PermissionsManager.areLocationPermissionsGranted(this)){
            mapboxMap?.locationComponent?.lastKnownLocation?.let {
                val distanceTopStop = LatLng(it.latitude, it.longitude).distanceTo(LatLng(stop.lat.toDouble(), stop.lon.toDouble()))
                findViewById< TextView>(R.id.recyclerViewTextViewStopDistance).text = getString(R.string.meters_away, distanceTopStop.toInt())
            }
        }else {
            findViewById<TextView>(R.id.recyclerViewTextViewStopDistance).visibility = View.GONE
        }
    }

    //region Lifecycle Methods
    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }
    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }
    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    //endregion

    companion object {
        private const val ICON_ID = "stop"
        var GBG_CENTER = LatLng(57.708691, 11.974782)
    }
}