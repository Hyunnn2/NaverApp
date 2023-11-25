package com.hyun.navermap.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.hyun.navermap.R

/**
 * 해당 클래스는 activity_splashh를 열기위한 클래스이다.
 */
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            startActivity(Intent(this, IntroActivity::class.java))
            finish()
        },3000)
    }
}