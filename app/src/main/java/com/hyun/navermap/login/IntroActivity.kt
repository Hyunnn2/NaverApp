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

                    val intent = Intent(this, MainActivity::class.java)
                    //기존 Activity 다 날리기
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)

                }
                else {
                    Toast.makeText(this,"로그인 실패", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}