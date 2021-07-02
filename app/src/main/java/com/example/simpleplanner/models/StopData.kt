package com.example.simpleplanner.models


import com.google.gson.annotations.SerializedName

data class StopData(
    @SerializedName("LocationList")
    val locationList: LocationList = LocationList()
)