package com.hyun.navermap.TimerInfo

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import com.google.firebase.database.collection.LLRBNode
import com.hyun.navermap.R
import com.hyun.navermap.Signal
import com.hyun.navermap.TimeInfo
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.MarkerIcons
import java.util.Calendar

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
}