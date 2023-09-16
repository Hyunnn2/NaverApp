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
import org.json.JSONObject
import java.io.InputStream
import java.util.Calendar


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private val LOCATION_PERMISSTION_REQUEST_CODE: Int = 1000
    private lateinit var locationSource: FusedLocationSource // 위치를 반환
    private lateinit var naverMap: NaverMap

    private lateinit var timerText: String
    private lateinit var handler: Handler

    private lateinit var infoWindow: InfoWindow
    private var timervalue: Int = 10


    // signalDataList를 클래스 멤버 변수로 선언하여 클래스 전체에서 사용할 수 있도록
    private val signalDataList = mutableListOf<Signal>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSTION_REQUEST_CODE)


        // JSON 파일을 읽어오기
        val rawResourceId = resources.getIdentifier("data", "raw", "com.hyun.navermap")
        val inputStream: InputStream = resources.openRawResource(rawResourceId)
        val jsonString = inputStream.bufferedReader().use { it.readText() }

        // JSON 데이터 파싱 jsonObject이용
        val jsonObject = JSONObject(jsonString)

        // 0부터 69까지의 신호등 데이터 가져오기
        for (i in 0 until 70) {
            val signalObject = jsonObject.getJSONObject("Signal").getJSONObject(i.toString())
            val signalData = Signal(
                No = signalObject.getString("No"),
                latitude = signalObject.getDouble("latitude"),
                longitude = signalObject.getDouble("longitude"),
                captionText = signalObject.getString("captionText"),
                etc = signalObject.getString("etc"),
                time = parseTimeInfo(signalObject.getJSONObject("time"))
            )
            signalDataList.add(signalData) // signalDataList에 0부터 69까지의 데이터가 저장
        }

    }

    private fun parseTimeInfo(timeObject: JSONObject): Map<String, TimeInfo> {
        val timeInfoMap = mutableMapOf<String, TimeInfo>()

        // 0부터 69까지의 신호등 데이터 가져오기(time)
        for (key in timeObject.keys()) {
            val timeInfoObject = timeObject.getJSONObject(key)
            val timeInfo = TimeInfo(
                period = timeInfoObject.getInt("period"),
                delayTime = timeInfoObject.getInt("delayTime"),
                onTime = timeInfoObject.getInt("onTime")
            )
            timeInfoMap[key] = timeInfo
        }
        return timeInfoMap

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
        val hour = currentTime.get(Calendar.HOUR_OF_DAY) // 현재 시간의 시 (0-23)

        // 현재 시간에 해당하는 데이터를 찾기
        val timeKey = when {
            hour >= 6.5 && hour < 10 -> "6:30"
            hour >= 10 && hour < 16 -> "10:00"
            hour >= 16 && hour < 21 -> "16:00"
            else -> "21:00"
        }

        // signalDataList에 저장된 데이터를 사용하여 마커추가
        for (signalData in signalDataList) {

            // 현재 시간에 해당하는 데이터를 찾기
            val timeInfo = signalData.time[timeKey]

            //마커 찍기
            val marker = Marker()
            marker.position = LatLng(signalData.latitude, signalData.longitude)
            marker.captionText = signalData.captionText
            marker.icon = MarkerIcons.BLACK // 원하는 마커 아이콘 설정
            marker.map = naverMap

            //현재 시간에 따른 onTime
            val timerText = "${timeInfo?.onTime}초"

            //정보창
            val infoWindow = InfoWindow()
            infoWindow.adapter = object : InfoWindow.DefaultViewAdapter(applicationContext) {
                override fun getContentView(p0: InfoWindow): View {
                    val view = layoutInflater.inflate(R.layout.activity_info, null)
                    val titleText = view.findViewById<TextView>(R.id.Name_cross)
                    titleText.text = signalData.captionText

                    val timerTextView = view.findViewById<TextView>(R.id.Timer_cross)
                    timerTextView.text = timerText

                    /*handler = Handler()
                    handler.postDelayed(timerRunnable, 1000)*/

                    return view
                }
            }

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


        /*        //마커 : 경상대 후문 이노티
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
                }*/
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