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
    private val currentmarker: Int = 0

    // 특정 마커를 지칭해주는 배열 설정
    private val markerList = mutableListOf<Marker>()

    // 특정 마커에 해당하는 정보창을 지정해주는 배열 설정
    private val infowindowList = mutableListOf<InfoWindow>()

    // 잔여시간 리스트 목록
    private val lasttimeList = mutableListOf<Int>()

    // 청색 신호등 점등시간 리스트 목록
    private var OntimeList = mutableListOf<Int>()

    // 적색 신호등 점등시간 리스트 목록
    private var OfftimeList = mutableListOf<Int>()
    // Runnable 변수 만들기(1000ms 마다 맵을 업데이트)

    private lateinit var MapRunnable: Runnable


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

        // 시간에 따라 빼줘야하는 초값
        var minus_value = 0.0

        // 가져온 현재시간을 이용해 총 초의 값계산
        var Time_second = (currentMinutes * 60) + currentsecond

        // 현재 시간에 해당하는 데이터를 찾기
        //6:30 주기
        fun getTimeKeyForFirstRange(hour: Int): String {
            return when {
                hour >= 6.5 && hour < 10 -> "6:30"
                hour >= 10 && hour < 16 -> "10:00"
                hour >= 16 && hour < 21 -> "16:00"
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

            val timeKey_R =
                if (signalData.time.containsKey(timeKey1)) timeKey1
                else if (signalData.time.containsKey(timeKey2)) timeKey2
                else continue

            println("timeKey_R 값 : ${timeKey_R}")

            when(timeKey_R){
                "6:30" -> minus_value = 6.5
                "7:00" -> minus_value = 7.0
                "9:00" -> minus_value = 9.0
                "10:00" -> minus_value = 10.0
                "16:00" -> minus_value = 16.0
                "20:00" -> minus_value = 20.0
                "21:00" -> minus_value = 21.0
                else -> 0
            }

            println(" minus value : ${minus_value}")

            var unique_time : Int = (Time_second + 90 + ((currentHour - minus_value) * 60 * 60)).toInt()

            println("총 초 : ${unique_time}")


            // 각 시간 주기에 맞는 나머지 시간 계산
            val ls = unique_time % (timeInfo!!.period.toInt())

            // 적, 청 구분
            val lg = timeInfo.StartTime + timeInfo.onTime
            var state: String

            // 신호등 상태의 잔여시간
            var lasttime_state: Int

            // 신호등 적,청 구분을 위한 수식
            if (ls < timeInfo.StartTime) {
                state = "적"
                lasttime_state = timeInfo.StartTime - ls
            } else if (ls in timeInfo.StartTime..lg) {
                state = "청"
                lasttime_state = lg - ls
            }
            //(lg <= ls)
            else {
                state = "적"
                lasttime_state = timeInfo.StartTime + timeInfo.period - ls
            }

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
            val ontime = timeInfo.onTime

            lasttimeList.add(lasttime_state)
            OntimeList.add(ontime)
            OfftimeList.add(offtime)

            if (lasttime_state > 0) {
                val infoTimer = TimerInfo(
                    marker,
                    lasttime_state,
                    signalData,
                    applicationContext,
                    infoWindow,
                    state
                )
            } else {
                if (state == "적") {
                    state = "청"
                    lasttime_state = ontime
                    val infoTimer = TimerInfo(
                        marker,
                        lasttime_state,
                        signalData,
                        applicationContext,
                        infoWindow,
                        state
                    )
                } else if (state == "청") {
                    state = "적"
                    lasttime_state = offtime
                    val infoTimer = TimerInfo(
                        marker,
                        lasttime_state,
                        signalData,
                        applicationContext,
                        infoWindow,
                        state
                    )
                }
            }
            lasttime_state--
            handler = Handler()
            handler.postDelayed(UpdateRunnable(lasttime_state, marker, signalData, infoWindow, state, ontime, offtime),1000)

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

    // 백그라운드 타이머 구현을 위해 정보창 정보를 1초간격으로 업데이트
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
            var state_a = state

            override fun run() {
                if (time > 0) {
                    val infoTimer = TimerInfo(
                        marker,
                        time,
                        signalData,
                        applicationContext,
                        infoWindow,
                        state_a
                    )
                }
                else {
                    if (state_a == "적") {
                        state_a = "청"
                        time = ontime
                        val infoTimer = TimerInfo(
                            marker,
                            time,
                            signalData,
                            applicationContext,
                            infoWindow,
                            state_a
                        )
                    } else if (state_a == "청") {
                        state_a = "적"
                        time = offtime
                        val infoTimer = TimerInfo(
                            marker,
                            time,
                            signalData,
                            applicationContext,
                            infoWindow,
                            state_a
                        )
                    }
                }
                time--
                handler.postDelayed(this,1000)
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