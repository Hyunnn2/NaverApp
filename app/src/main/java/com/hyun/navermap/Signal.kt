package com.hyun.navermap

import java.time.LocalTime

class Signal (  val No: String,
                val latitude: Double,
                val longitude: Double,
                val captionText: String,
                val etc: String,
                val time: Map<String, TimeInfo>
)

class TimeInfo(
    val period: Int,
    val StartTime: Int,
    val onTime: Int
)
