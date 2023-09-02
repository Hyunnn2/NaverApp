package com.hyun.navermap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.naver.maps.geometry.LatLng

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val coord = LatLng(37.5670135, 126.9783740)

        Toast.makeText(this,
            "위도: ${coord.latitude}, 경도: ${coord.longitude}",
            Toast.LENGTH_SHORT).show()

    }

}