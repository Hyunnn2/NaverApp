package com.hyun.navermap.TimerInfo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.hyun.navermap.R
import com.hyun.navermap.calculate.Signal
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker

/**
 * 해당 클래스는 마커 정보창의 view를 원하는 형식으로 바꿔주기 위한 클래스이다.
 * 클래스는 정보창을 가진 마커와 정보창의 표시하는 데이터를 인자로 받는다.
 */
class TimerInfo (
    private val marker : Marker,
    private val lasttime_state : Int,
    private val signalData: Signal,
    private var context : Context,
    private val infoWindow: InfoWindow,
    private var state : String
){
    //현재 시간에 따른 onTime
    var timerText = "남은 시간: ${lasttime_state}초"

    //현지 시간에 따른 onTime -1연산을 위한 변수 설정
    var timervar : Int = lasttime_state

    // 하는 일 : infoWindow의 정보창을 원하는 형식으로 변경한다.
    init{
        infoWindow.adapter = object : InfoWindow.DefaultViewAdapter(context) {
            override fun getContentView(p0: InfoWindow): View {

                val view = LayoutInflater.from(context).inflate(R.layout.activity_info, null)
                val timerTextView = view.findViewById<TextView>(R.id.Timer_cross)
                val titleText = view.findViewById<TextView>(R.id.Name_cross)
                val stateText = view.findViewById<TextView>(R.id.State_cross)

                stateText.text = state
                if(state == "적"){
                    stateText.setTextColor(ContextCompat.getColor(context, R.color.Red))
                }
                else if(state == "청"){
                    stateText.setTextColor(ContextCompat.getColor(context, R.color.Green))
                }
                titleText.text = signalData.No
                timerTextView.text = timerText

                return view
            }
        }
    }

    // 정보창 열기
    fun openInfoWindow() {
        infoWindow.open(marker)
    }

    // 정보창 닫기
    fun closeInfoWindow() {
        infoWindow.close()
    }
}