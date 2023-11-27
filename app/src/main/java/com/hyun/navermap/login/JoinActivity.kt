package com.hyun.navermap.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hyun.navermap.R
import com.hyun.navermap.databinding.ActivityJoinBinding
/**
 * 해당 클래스는 파이어베이스와 연동된다.
 * 사용자의 정보를 입력받아 해당 정보의 양식을 비교해 사용자의 회원가입을 승인한다.
 */
class JoinActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var  binding: ActivityJoinBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        binding = DataBindingUtil.setContentView(this, R.layout.activity_join)

        binding.realjoinBtn.setOnClickListener {

            var isGoToJoin = true

            val email = binding.emailArea.text.toString()
            val password1 = binding.passwordArea1.text.toString()
            val password2 = binding.passwordArea2.text.toString()

            //email 값이 비어었는지 확인
            if(email.isEmpty()){
                Toast.makeText(this,"이메일을 입력해주세요.",Toast.LENGTH_LONG).show()
                isGoToJoin = false
            }
            //email 형식 확인
            if (!email.contains("@")) {
                Toast.makeText(this, "올바른 이메일 형식이 아닙니다.", Toast.LENGTH_LONG).show()
                isGoToJoin = false
            }

            //password, password check 값이 비어었는지 확인
            if(password1.isEmpty()){
                Toast.makeText(this,"password을 입력해주세요.",Toast.LENGTH_LONG).show()
                isGoToJoin = false
            }
            if(password2.isEmpty()){
                Toast.makeText(this,"password check를 입력해주세요.",Toast.LENGTH_LONG).show()
                isGoToJoin = false
            }
            //비밀번호 동일 여부 확인
            if(!password1.equals(password2)){
                Toast.makeText(this,"password와 password check가 다릅니다.",Toast.LENGTH_LONG).show()
                isGoToJoin = false
            }
            //비밀번호 길이 6이상
            if(password1.length < 6){
                Toast.makeText(this,"password를 6자리 이상으로 입력해주세요.",Toast.LENGTH_LONG).show()
                isGoToJoin = false
            }
            if(isGoToJoin){
                auth.createUserWithEmailAndPassword(email, password1).addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this,"성공",Toast.LENGTH_LONG).show()

                            //introActivity로 이동
                            val intent = Intent(this, IntroActivity::class.java)
                            //기존 Activity 다 날리기
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)

                        }
                        else {
                            Toast.makeText(this,"실패",Toast.LENGTH_LONG).show()
                        }
                }
            }
        }
    }
}