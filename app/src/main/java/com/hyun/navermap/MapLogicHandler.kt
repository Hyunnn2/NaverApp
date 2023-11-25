package com.hyun.navermap

import android.content.Context
import android.os.Handler
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.MarkerIcons
import kotlinx.coroutines.Runnable
import java.util.Calendar
import com.hyun.navermap.TimerInfo.TimerInfo

class MapLogicHandler(
    private val signalDataList : List<Signal>,
    private val naverMap : NaverMap,
    private val context : Context
) {

    //타이머를 동일하게 1초씩 감소시키기 위한 핸들러 변수
    private lateinit var handler : Handler

    fun handleMapReady(currentHour: Int) {
        val currentTime = Calendar.getInstance()
        val currentMinutes = currentTime.get(Calendar.MINUTE)
        val currentsecond = currentTime.get(Calendar.SECOND)
        var minusValue = 0.0
        var Timesecond = (currentMinutes * 60) + currentsecond

        for (signalData in signalDataList) {
            val timeKey1 = getTimeKeyForFirstRange(currentHour)
            val timeKey2 = getTimeKeyForSecondRange(currentHour)

            val timeInfo =
                if (signalData.time.containsKey(timeKey1)) signalData.time[timeKey1]
                else if (signalData.time.containsKey(timeKey2)) signalData.time[timeKey2]
                else continue

            val timeKey_R =
                if (signalData.time.containsKey(timeKey1)) timeKey1
                else if (signalData.time.containsKey(timeKey2)) timeKey2
                else continue

            when(timeKey_R){
                "6:30" -> minusValue = 6.5
                "7:00" -> minusValue = 7.0
                "9:00" -> minusValue = 9.0
                "10:00" -> minusValue = 10.0
                "16:00" -> minusValue = 16.0
                "20:00" -> minusValue = 20.0
                "21:00" -> minusValue = 21.0
                else -> 0
            }

            var uniqueTime : Int = (Timesecond + 90 + ((currentHour - minusValue) * 60 * 60)).toInt()

            // 각 시간 주기에 맞는 나머지 시간 계산
            val ls = uniqueTime % (timeInfo!!.period.toInt())

            // 적, 청 구분
            val lg = timeInfo.StartTime + timeInfo.onTime
            var state: String

            // 신호등 상태의 잔여시간
            var lasttimeState: Int

            // 신호등 적,청 구분을 위한 수식
            if (ls < timeInfo.StartTime) {
                state = "적"
                lasttimeState = timeInfo.StartTime - ls
            } else if (ls in timeInfo.StartTime..lg) {
                state = "청"
                lasttimeState = lg - ls
            }

            //(lg <= ls)
            else {
                state = "적"
                lasttimeState = timeInfo.StartTime + timeInfo.period - ls
            }

            //마커 찍기
            val marker = Marker()
            marker.position = LatLng(signalData.latitude, signalData.longitude)
            marker.captionText = signalData.captionText
            marker.icon = MarkerIcons.BLACK // 원하는 마커 아이콘 설정
            marker.map = naverMap

            //정보창 설정 및 리스트에 추가
            val infoWindow = InfoWindow()

            // InfoWindow에 Timer기능을 추가해주는 class 사용
            val offtime = timeInfo.period - timeInfo.onTime
            val ontime = timeInfo.onTime

            if (lasttimeState > 0) {
                val infoTimer = TimerInfo(
                    marker,
                    lasttimeState,
                    signalData,
                    context,
                    infoWindow,
                    state
                )
            } else {
                if (state == "적") {
                    state = "청"
                    lasttimeState = ontime
                    val infoTimer = TimerInfo(
                        marker,
                        lasttimeState,
                        signalData,
                        context,
                        infoWindow,
                        state
                    )
                } else if (state == "청") {
                    state = "적"
                    lasttimeState = offtime
                    val infoTimer = TimerInfo(
                        marker,
                        lasttimeState,
                        signalData,
                        context,
                        infoWindow,
                        state
                    )
                }
            }
            lasttimeState--
            handler = Handler()
            handler.postDelayed(
                UpdateRunnable(
                    lasttimeState,
                    marker,
                    signalData,
                    infoWindow,
                    state,
                    ontime,
                    offtime
                ), 1000
            )

            marker.setOnClickListener {
                if (infoWindow.map == null) {
                    infoWindow.open(marker)
                } else {
                    infoWindow.close()
                }
                true
            }
        }
    }

    private fun getTimeKeyForFirstRange(hour: Int): String {
        return when {
            hour >= 6.5 && hour < 10 -> "6:30"
            hour >= 10 && hour < 16 -> "10:00"
            hour >= 16 && hour < 21 -> "16:00"
            else -> "21:00"
        }
    }

    private fun getTimeKeyForSecondRange(hour: Int): String {
        return when {
            hour >= 7 && hour < 9 -> "7:00"
            hour >= 9 && hour < 16 -> "9:00"
            hour >= 16 && hour < 20 -> "16:00"
            else -> "20:00"
        }
    }
    private fun UpdateRunnable(
        lasttime_state : Int,
        marker: Marker,
        signalData : Signal,
        infoWindow : InfoWindow,
        state : String,
        ontime : Int,
        offtime : Int
    ) : Runnable{
        return object : Runnable{

            var time = lasttime_state
            var stateA = state

            override fun run() {
                if (time > 0) {
                    val infoTimer = TimerInfo(
                        marker,
                        time,
                        signalData,
                        context,
                        infoWindow,
                        stateA
                    )
                }
                else {
                    if (stateA == "적") {
                        stateA = "청"
                        time = ontime
                        val infoTimer = TimerInfo(
                            marker,
                            time,
                            signalData,
                            context,
                            infoWindow,
                            stateA
                        )
                    } else if (stateA == "청") {
                        stateA = "적"
                        time = offtime
                        val infoTimer = TimerInfo(
                            marker,
                            time,
                            signalData,
                            context,
                            infoWindow,
                            stateA
                        )
                    }
                }
                time--
                handler.postDelayed(this,1000)
            }
        }
    }
}