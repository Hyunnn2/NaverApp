package com.hyun.navermap.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hyun.navermap.MainActivity
import com.hyun.navermap.R
import com.hyun.navermap.databinding.ActivityIntroBinding
/**
 * 해당 클래스는 파이어베이스와 연동된다.
 * 파이어베이스에 입력된 사용자의 정보가 존재한다면 사용자의 접근을 허용한다.
 *
 */
class IntroActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var binding: ActivityIntroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro)

        // 사용자가 이미 인증되어 있는지 확인
        if (auth.currentUser != null) {
            navigateToMainActivity()
        }

        binding.joinBtn.setOnClickListener {
            val intent = Intent(this,JoinActivity::class.java )
            startActivity(intent)
        }

        binding.loginBtn.setOnClickListener {

            val email = binding.emailArea.text.toString()
            val password = binding.passwordArea.text.toString()

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this,"로그인 성공", Toast.LENGTH_LONG).show()

                    // 사용자 토큰을 로컬에 안전하게 저장 (예: SharedPreferences 등)
                    auth.currentUser?.getIdToken(true)?.addOnCompleteListener { tokenTask ->
                        if (tokenTask.isSuccessful) {
                            val userToken = tokenTask.result?.token
                            saveTokenLocally(userToken)
                        }
                    }

                    navigateToMainActivity()

                } else {
                    Toast.makeText(this, "로그인 실패", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun saveTokenLocally(token: String?) {
        // 토큰을 안전하게 저장하는 메서드 구현 (예: SharedPreferences 등)
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        // 백 스택을 지웁니다.
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}