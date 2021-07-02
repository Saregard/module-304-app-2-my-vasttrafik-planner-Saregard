package com.example.simpleplanner.models


import com.google.gson.annotations.SerializedName

data class JourneyDetailRef(
    @SerializedName("ref")
    val ref: String = ""
)