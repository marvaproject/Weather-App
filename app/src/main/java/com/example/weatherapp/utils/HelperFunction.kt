package com.example.weatherapp.utils

import java.math.RoundingMode

object HelperFunction {

    fun formatterDegree(temperature: Double?): String {
        val temp = temperature as Double
        val tempToCelcius = temp - 273.15
        val formatDegree = tempToCelcius.toBigDecimal().setScale(2, RoundingMode.CEILING)
        return "$formatDegree°C"
    }
}