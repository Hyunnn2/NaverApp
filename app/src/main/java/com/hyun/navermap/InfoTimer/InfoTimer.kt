package com.hyun.navermap.InfoTimer


import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.common.internal.Objects
import com.hyun.navermap.R
import com.hyun.navermap.Signal
import com.hyun.navermap.TimeInfo
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker

class InfoTimer (
    private val marker : Marker,
    private val timeInfo : TimeInfo?,
    private val signalData: Signal,
    private var context : Context,
    private val infoWindow: InfoWindow
){
    private lateinit var handler : Handler

    //현재 시간에 따른 onTime
    var timerText = "${timeInfo?.onTime}초"

    //현지 시간에 따른 onTime -1연산을 위한 변수 설정
    var timervar : Int = timeInfo?.onTime?.toInt() ?: 0

    //각 마커마다의 timerRunnable 생성(해당 함수는 마커의 정보창,신호등 이름, 온타임을 인자로 받아서처리 추가 처리 하고싶은 내용 있으면 말하길)
    val timerRunnable = CreatetimerRunnable(infoWindow, timervar, signalData.No)

    init{
        infoWindow.adapter = object : InfoWindow.DefaultViewAdapter(context) {
            override fun getContentView(p0: InfoWindow): View {

                val view = LayoutInflater.from(context).inflate(R.layout.activity_info, null)
                val timerTextView = view.findViewById<TextView>(R.id.Timer_cross)
                val titleText = view.findViewById<TextView>(R.id.Name_cross)

                titleText.text = signalData.No
                timerTextView.text = timerText

                handler = Handler()
                handler.postDelayed(timerRunnable, 1000)

                return view
            }
        }
    }


    private fun CreatetimerRunnable(infoWindow : InfoWindow, timervar : Int, captiontext : String): Runnable{
        return object : Runnable {
            var timervar_a = timervar - 1

            override fun run() {
                if (timervar_a >= 0) {
                    timerText = "남은 시간: ${timervar_a} 초"
                    infoWindow.adapter = object : InfoWindow.DefaultViewAdapter(context) {
                        override fun getContentView(infoWindow: InfoWindow): View {

                            val view = LayoutInflater.from(context).inflate(R.layout.activity_info, null)
                            val titleText = view.findViewById<TextView>(R.id.Name_cross)
                            titleText.text = captiontext

                            val timerTextView = view.findViewById<TextView>(R.id.Timer_cross)
                            timerTextView.text = timerText
                            return view
                        }
                    }
                    timervar_a--
                    handler.postDelayed(this, 1000) // 1초마다 업데이트
                } else {
                    infoWindow.close()
                }
            }
        }
    }
}