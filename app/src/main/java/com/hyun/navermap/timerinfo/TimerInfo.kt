package com.hyun.navermap.timerinfo

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.hyun.navermap.R
import com.hyun.navermap.calculate.Signal
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage

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
    var timerText = "${lasttime_state}"

    //현지 시간에 따른 onTime -1연산을 위한 변수 설정
    var timervar : Int = lasttime_state





    // 하는 일 : infoWindow의 정보창을 원하는 형식으로 변경한다.
    init{
        infoWindow.adapter = object : InfoWindow.DefaultViewAdapter(context) {
            override fun getContentView(p0: InfoWindow): View {

                val view = LayoutInflater.from(context).inflate(R.layout.activity_info, null)
                val timerTextView = view.findViewById<TextView>(R.id.Timer_cross)
                val captionText = view.findViewById<TextView>(R.id.caption_cross)
                val noText = view.findViewById<TextView>(R.id.No_cross)

                if(state == "적"){
                    timerTextView.setTextColor(ContextCompat.getColor(context, R.color.Red))
                }
                else if(state == "청"){
                    timerTextView.setTextColor(ContextCompat.getColor(context, R.color.Green))
                }

                noText.text = signalData.No
                captionText.text = signalData.captionText
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

    fun resizeBitmap(originalBitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        return Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
    }
}