package com.hyun.navermap.info

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hyun.navermap.R

/**
 * 해당 클래스는 activity_info를 열기위한 클래스이다.
 */
class InfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
    }
}