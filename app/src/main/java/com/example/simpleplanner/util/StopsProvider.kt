package com.example.simpleplanner.util

import android.content.res.Resources
import com.example.simpleplanner.models.StopLocation
import com.google.gson.Gson
import java.lang.Exception
import java.util.*

object StopsProvider {
    fun gbgStops(resources: Resources, sorted: Boolean = true): List<StopLocation> {

        val inputStream = resources.openRawResource(com.example.simpleplanner.R.raw.gbg_stops)
        return try {
            val scanner = Scanner(inputStream)
            val stringBuilder = StringBuilder()
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine())
            }
            Gson().fromJson(stringBuilder.toString(), Array<StopLocation>::class.java).asList()
        } catch (exception: Exception) {
            emptyList()
        } finally {
            inputStream.close()
        }
    }
}