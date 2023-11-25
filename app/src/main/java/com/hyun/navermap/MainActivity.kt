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

    private lateinit var signalDataList: List<Signal>
    private lateinit var signalDataLoader: SignalDataLoader

    private val fl : FrameLayout by lazy {
        findViewById(R.id.fragment_container)
    }

    private val bn : BottomNavigationView by lazy {
        findViewById(R.id.bottomNavigationView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSTION_REQUEST_CODE)

        // 신호등 데이터를 가져오기 위한 연산
        signalDataLoader = SignalDataLoader(resources)
        signalDataList = signalDataLoader.loadSignalData()

        supportFragmentManager.beginTransaction().add(fl.id, CMapFragment()).commit()


        // 네비게이션 클릭시 이벤트에 맞는 작동
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

        //실시간 위치 추적 모드
        naverMap.locationTrackingMode = LocationTrackingMode.None

        this.naverMap = naverMap

        // Context 전달
        mapLogicHandler = MapLogicHandler(signalDataList, naverMap, this)

        // 현재 시간의 시 (0-23)
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        // 지도 만들기 연산
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