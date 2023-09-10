package com.hyun.navermap

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
import androidx.annotation.NonNull
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private val LOCATION_PERMISSTION_REQUEST_CODE: Int = 1000
    private lateinit var locationSource: FusedLocationSource // 위치를 반환
    private lateinit var naverMap: NaverMap

    private lateinit var timerText : String
    private lateinit var handler : Handler

    //마커
    private val marker = Marker()
    private val marker1 = Marker()

    private lateinit var infoWindow : InfoWindow
    private var timervalue: Int = 10



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSTION_REQUEST_CODE)

        //기본 위치 가좌동으로
//        val options = NaverMapOptions()
//            .camera(CameraPosition(LatLng(35.1524, 128.1049), 16.0))
//            .mapType(NaverMap.MapType.Basic)
//            .enabledLayerGroups(NaverMap.LAYER_GROUP_TRAFFIC, NaverMap.LAYER_GROUP_TRANSIT)
    }

    override fun onMapReady(@NonNull naverMap: NaverMap) {

        //초기 위치 : 경상대 정문
        val cameraPosition = CameraPosition(
            LatLng(35.1524, 128.1049),
            17.0
        )
        naverMap.cameraPosition = cameraPosition

        //대중교통
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRANSIT, true)

        this.naverMap = naverMap
        //위치추적
        naverMap.locationSource = locationSource

        val uiSettings = naverMap.uiSettings
        uiSettings.isLocationButtonEnabled = true
        naverMap.locationTrackingMode = LocationTrackingMode.None //실시간 위치 추적 모드


        //마커 : 경상대 후문 이노티
        marker.position = LatLng(35.155691, 128.107346)
        marker.map = naverMap
        marker.captionText = "경상국립대 이노티 앞"
        marker.icon = MarkerIcons.BLACK // 마커 기본색은 초록색이고 검정 -> 다른색 순으로 해야함
        marker.iconTintColor = Color.CYAN


        //마커1 : 경상대 정문 스벅
        marker1.position = LatLng(35.152453, 128.105398)
        marker1.map = naverMap
        marker1.captionText = "경상국립대 스타벅스 앞"


        infoWindow = InfoWindow()
        timerText = "${timervalue}초"
        infoWindow.adapter = object : InfoWindow.DefaultViewAdapter(applicationContext) {
            override fun getContentView(p0: InfoWindow): View {
                val view = layoutInflater.inflate(R.layout.activity_info, null)
                val titleText = view.findViewById<TextView>(R.id.Name_cross)
                titleText.text = "경상국립대 스타벅스 앞"

                val timerTextView = view.findViewById<TextView>(R.id.Timer_cross)
                timerTextView.text = timerText

                handler = Handler()
                handler.postDelayed(timerRunnable, 1000)

                return view
            }
        }


        marker1.setOnClickListener {
            if(infoWindow.map == null){
                infoWindow.open(marker1)
            }
            else{
                infoWindow.close()
            }
            true
        }
    }


    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    private val timerRunnable: Runnable = object : Runnable {
        override fun run() {
            if (timervalue >= 0) {
                timerText = "남은 시간: $timervalue 초"
                infoWindow.adapter = object : InfoWindow.DefaultViewAdapter(applicationContext) {
                    override fun getContentView(infoWindow: InfoWindow): View {

                        val view = layoutInflater.inflate(R.layout.activity_info, null)
                        val titleText = view.findViewById<TextView>(R.id.Name_cross)
                        titleText.text = "경상국립대 스타벅스 앞"

                        val timerTextView = view.findViewById<TextView>(R.id.Timer_cross)
                        timerTextView.text = timerText
                        return view
                    }
                }
                timervalue--
                handler.postDelayed(this, 1000) // 1초마다 업데이트
            } else {
                infoWindow.close()
            }
        }
    }

}