package com.example.x_travel

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserState
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobile.client.results.SignInResult
import com.amazonaws.mobile.client.results.SignInState


class AuthActivity : AppCompatActivity() {
    private val TAG = AuthActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        val signIn_button = findViewById<Button>(R.id.signIn_button) // 로그인 버튼
        val signUp_button = findViewById<Button>(R.id.signUp_button) // 회원가입 버튼
        val forgot_Password_button =
            findViewById<Button>(R.id.forgot_Password_button) // 비밀번호를 잊어버리셨나요?

        // 로그인이 되어있는지 확인
        AWSMobileClient.getInstance()
            .initialize(applicationContext, object : Callback<UserStateDetails> {
                override fun onResult(userStateDetails: UserStateDetails) {
                    Log.i(TAG, userStateDetails.userState.toString())

                    // 로그인이 되어있으면 MainActivity 로 이동
                    if (userStateDetails.userState == UserState.SIGNED_IN) {
                        val i = Intent(this@AuthActivity, MainActivity::class.java)
                        startActivity(i)
                        finish()
                    }
                }

                override fun onError(e: Exception) {
                    Log.e(TAG, e.toString())
                }
            })

        // 로그인 버튼
        signIn_button.setOnClickListener { showSignIn() }

        // 회원가입 버튼
        signUp_button.setOnClickListener {
            val i = Intent(this@AuthActivity, SignUpActivity::class.java)
            startActivity(i)
            finish()
        }

        // 비밀번호를 잊어버리셨나요?
        forgot_Password_button.setOnClickListener {
            val i = Intent(this@AuthActivity, ForgotActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    // 로그인 함수
    private fun showSignIn() {

        // 아이디 비밀번호 순
        val login_id = findViewById<EditText>(R.id.login_id)
        val login_paw = findViewById<EditText>(R.id.login_paw)
        val username = login_id.text.toString()
        val password = login_paw.text.toString()
        AWSMobileClient.getInstance().signIn(username, password, null, object : Callback<SignInResult> {
            override fun onResult(signInResult: SignInResult) {
                runOnUiThread {
                    Log.d(TAG, "Sign-in callback state: " + signInResult.signInState)
                    when (signInResult.signInState) {
                        SignInState.DONE -> {
                            Toast.makeText(applicationContext, "환영합니다!", Toast.LENGTH_SHORT).show()
                            val i = Intent(this@AuthActivity, MainActivity::class.java)
                            startActivity(i)
                            finish()
                        }
                        SignInState.SMS_MFA -> Toast.makeText(applicationContext, "Please confirm sign-in with SMS.", Toast.LENGTH_SHORT).show()

                        SignInState.NEW_PASSWORD_REQUIRED -> Toast.makeText(applicationContext, "Please confirm sign-in with new password.", Toast.LENGTH_SHORT).show()

                        else -> Toast.makeText(applicationContext, "Unsupported sign-in confirmation: " + signInResult.signInState, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onError(e: Exception) {
                Log.e(TAG, "Sign-in error : " + e.message)
                when {
                    e.message!!.contains("Missing required parameter USERNAME") -> {
                        errorMessage("아이디와 비밀번호를 입력해주세요.")
                    }
                    e.message!!.contains("Incorrect username or password.") -> {
                        errorMessage("아이디와 비밀번호가 일치하지 않습니다.")
                    }
                    e.message!!.contains("User does not exist.") -> {
                        errorMessage("존재하지 않는 아이디입니다.")
                    }
                    e.message!!.contains("Unable to execute HTTP request") -> {
                        errorMessage("네트워크가 원활하지 않습니다.\n네트워크 연결 상태를 확인하세요.")
                    }
                    e.message!!.contains("User is not confirmed.") -> {


                        // 다이어로그 생성
                        val mHandler = Handler(Looper.getMainLooper())
                        mHandler.post {
                            val ad = AlertDialog.Builder(this@AuthActivity)
                            ad.setIcon(R.mipmap.ic_launcher)
                            ad.setTitle("인증 코드 미승인")
                            ad.setMessage("인증 코드를 승인하지 않았습니다.\n인증 코드를 승인하러 가시겠습니까?")

                            // 확인버튼
                            ad.setPositiveButton(
                                "확인"
                            ) { dialog, which -> // 이메일에 문제가 없으면 인증 코드 창으로 이동
                                val i = Intent(this@AuthActivity, OkActivity::class.java)
                                i.putExtra("email", username) // username을 인증 코드 창에서 사용하기 위해
                                startActivity(i)
                                finish()
                                dialog.dismiss()
                            }

                            // 취소버튼
                            ad.setNegativeButton(
                                "취소"
                            ) { dialog, which -> dialog.dismiss() }
                            ad.show()
                        }
                    }
                }
            }
        })
    }

    // 뒤로가기 2번 눌러야 종료
    private val FINISH_INTERVAL_TIME: Long = 2500
    private var backPressedTime: Long = 0
    private var toast: Toast? = null
    override fun onBackPressed() {
        val tempTime = System.currentTimeMillis()
        val intervalTime = tempTime - backPressedTime


        // 뒤로 가기 할 경우 홈 화면으로 이동
        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed()
            // 뒤로가기 토스트 종료
            toast!!.cancel()
            finish()
        } else {
            backPressedTime = tempTime
            toast =
                Toast.makeText(applicationContext, "'뒤로'버튼 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT)
//            toast.show()
        }
    }

    // 에러 메시지
    fun errorMessage(message: String?) {
        val mHandler = Handler(Looper.getMainLooper())
        mHandler.post { Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show() }
    }
}