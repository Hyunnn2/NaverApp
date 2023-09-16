package com.hyun.navermap

class Signal (  val No: String,
                val latitude: Double,
                val longitude: Double,
                val captionText: String,
                val etc: String,
                val time: Map<String, TimeInfo>
)

class TimeInfo(
    val period: Int,
    val delayTime: Int,
    val onTime: Int
)