package com.hyun.navermap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
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
import com.hyun.navermap.fragments.BookMarkFragment
import com.hyun.navermap.fragments.CMapFragment
import com.hyun.navermap.fragments.UserFragment


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private val LOCATION_PERMISSTION_REQUEST_CODE: Int = 1000
    private lateinit var locationSource: FusedLocationSource // 위치를 반환
    private lateinit var naverMap: NaverMap
    private lateinit var mapLogicHandler: MapLogicHandler

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

    private val fl: FrameLayout by lazy {
        findViewById(R.id.fragment_container)
    }

    private val bn: BottomNavigationView by lazy {
        findViewById(R.id.bottomNavigationView)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSTION_REQUEST_CODE)

        signalDataLoader = SignalDataLoader(resources)
        signalDataList = signalDataLoader.loadSignalData()

        supportFragmentManager.beginTransaction().add(fl.id, CMapFragment()).commit()

        bn.setOnNavigationItemSelectedListener {
            replaceFragment(
                when (it.itemId) {
                    R.id.tab_map -> {
                        mapView.visibility = View.VISIBLE
                        CMapFragment()
                    }

                    R.id.tab_bookmark -> {
                        mapView.visibility = View.GONE
                        BookMarkFragment()
                    }

                    else -> {
                        mapView.visibility = View.GONE
                        UserFragment()
                    }
                }
            )
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(fl.id, fragment).commit()
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

        this.naverMap = naverMap
        mapLogicHandler = MapLogicHandler(signalDataList, naverMap, this) // Context 전달
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) // 현재 시간의 시 (0-23)
        mapLogicHandler.handleMapReady(currentHour)
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