package com.hyun.navermap.calculate

// 신호등 데이터가 가지고 있는 값을 관리하는 클래스
class Signal (  val No: String,
                val latitude: Double,
                val longitude: Double,
                val captionText: String,
                val etc: String,
                val time: Map<String, TimeInfo>
)

// 해당 신호등의 주기값과 시작시간, 점등시간을 관리하는 클래스
class TimeInfo(
    val period: Int,
    val StartTime: Int,
    val onTime: Int
)
