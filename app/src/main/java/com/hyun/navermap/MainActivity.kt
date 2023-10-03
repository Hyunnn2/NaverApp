package com.hyun.navermap

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
import java.util.Calendar
import com.hyun.navermap.TimerInfo.TimerInfo

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private val LOCATION_PERMISSTION_REQUEST_CODE: Int = 1000
    private lateinit var locationSource: FusedLocationSource // 위치를 반환
    private lateinit var naverMap: NaverMap

    private lateinit var timerText: String
    private lateinit var handler: Handler


    private lateinit var signalDataList: List<Signal>
    private lateinit var signalDataLoader: SignalDataLoader

    // 가장 최근에 클릭한 마커가 무엇인지 알기 위한 변수
    private val currentmarker : Int = 0

    // 특정 마커를 지칭해주는 배열 설정
    private val markerList = mutableListOf<Marker>()

    // 특정 마커에 해당하는 정보창을 지정해주는 배열 설정
    private val infowindowList = mutableListOf<InfoWindow>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSTION_REQUEST_CODE)

        signalDataLoader = SignalDataLoader(resources)
        signalDataList = signalDataLoader.loadSignalData()
    }

    override fun onMapReady(@NonNull naverMap: NaverMap) {

        //초기 위치 : 경상대 정문
        val cameraPosition = CameraPosition(
            LatLng(35.152391, 128.105066), 18.0
        )
        naverMap.cameraPosition = cameraPosition

        //대중교통 아이콘 띄우기
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRANSIT, true)

        //위치추적
        this.naverMap = naverMap
        naverMap.locationSource = locationSource
        val uiSettings = naverMap.uiSettings
        uiSettings.isLocationButtonEnabled = true
        naverMap.locationTrackingMode = LocationTrackingMode.None //실시간 위치 추적 모드

        // 현재 시간 가져오기
        val currentTime = Calendar.getInstance()
        val currentHour = currentTime.get(Calendar.HOUR_OF_DAY) // 현재 시간의 시 (0-23)

        // 현재 시간 가져오기(분, 초)
        val currentMinutes = currentTime.get(Calendar.MINUTE)
        val currentsecond = currentTime.get(Calendar.SECOND)

        println("분 : ${currentMinutes}")
        println("초 : ${currentsecond}")

        // 가져온 현재시간을 이용해 총 초의 값계산
        var Time_second = currentMinutes * 60 + currentsecond

        println("총초 : ${Time_second}")



        // 현재 시간에 해당하는 데이터를 찾기
        //6:30 주기
        fun getTimeKeyForFirstRange(hour: Int): String {
            return when {
                hour >= 6.5 && hour < 10 -> "6:30"
                hour >= 10 && hour < 16 -> "10:00"
                hour >= 16 && hour <21 -> "16:00"
                else -> "21:00"
            }
        }

        //7:00 주기
        fun getTimeKeyForSecondRange(hour: Int): String {
            return when {
                hour >= 7 && hour < 9 -> "7:00"
                hour >= 9 && hour < 16 -> "9:00"
                hour >= 16 && hour < 20 -> "16:00"
                else -> "20:00"
            }
        }

        // signalDataList에 저장된 데이터를 사용하여 마커추가

        for (signalData in signalDataList) {

            val timeKey1 = getTimeKeyForFirstRange(currentHour)
            val timeKey2 = getTimeKeyForSecondRange(currentHour)

            val timeInfo =
                if (signalData.time.containsKey(timeKey1)) signalData.time[timeKey1]
                else if (signalData.time.containsKey(timeKey2)) signalData.time[timeKey2]
                else continue

            // 각 시간 주기에 맞는 나머지 시간 계산
            println("주기 : ${timeInfo!!.period.toInt()}")
            val ls =  Time_second % (timeInfo!!.period.toInt())

            println("나머지 시간 : ${ls}")

            // 적, 청 구분
            val lg = timeInfo.StartTime + timeInfo.onTime
            var state: String

            // 신호등 상태의 잔여시간
            var lasttime_state : Int


            // 신호등 적,청 구분을 위한 수식
            if(ls < timeInfo.StartTime){
                state = "적"
                lasttime_state = timeInfo.StartTime - ls
            }
            else if(timeInfo.StartTime <= ls && ls <= lg){
                state = "청"
                lasttime_state = lg - ls
            }
            //(lg <= ls)
            else{
                state = "적"
                lasttime_state = timeInfo.StartTime + timeInfo.period - ls
            }

            println("현재 신호등 상태 : ${state} , 현재상태에서의 잔여시간 : ${lasttime_state}")

            //마커 찍기
            val marker = Marker()
            marker.position = LatLng(signalData.latitude, signalData.longitude)
            marker.captionText = signalData.captionText
            marker.icon = MarkerIcons.BLACK // 원하는 마커 아이콘 설정
            marker.map = naverMap

            markerList.add(marker)

            //정보창 설정 및 리스트에 추가
            val infoWindow = InfoWindow()
            infowindowList.add(infoWindow)

            // InfoWindow에 Timer기능을 추가해주는 class 사용
            val offtime = timeInfo.period - timeInfo.onTime
            val infoTimer = TimerInfo(marker, lasttime_state, signalData, applicationContext ,infoWindow, state,timeInfo.onTime,offtime)


            //마커 클릭 이벤트
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

}