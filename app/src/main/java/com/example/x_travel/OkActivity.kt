package com.example.x_travel

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.results.SignUpResult


class OkActivity : AppCompatActivity() {
    var TAG = AuthActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ok)

        // 인증 확인 버튼
        val Ok_button = findViewById<Button>(R.id.Ok_button)
        // 인증 재전송 버튼
        val Re_Ok_button = findViewById<Button>(R.id.Re_Ok_button)

        // SignUpActivity 에서 사용된 username 정보를 가져와 TextView에 넣는다.
        val TextView = findViewById<TextView>(R.id.signUpUsername2)
        val intent = intent
        val bundle = intent.extras
        val username = bundle!!.getString("email")
        TextView.text = username

        // 인증 버튼
        Ok_button.setOnClickListener {
            val code_name = findViewById<EditText>(R.id.code_name)
            val code = code_name.text.toString()
            AWSMobileClient.getInstance().confirmSignUp(
                username,
                code,
                object :
                    Callback<SignUpResult> {
                    override fun onResult(signUpResult: SignUpResult) {
                        runOnUiThread {
                            Log.d(TAG, "Sign-up callback state: " + signUpResult.confirmationState)
                            if (!signUpResult.confirmationState) {
                                val details = signUpResult.userCodeDeliveryDetails
                                Toast.makeText(applicationContext, "Confirm sign-up with: " + details.destination, Toast.LENGTH_SHORT).show()
                            } else {

                                // 회원가입이 완료되면 로그인 창으로 이동
                                Toast.makeText(applicationContext, "성공적으로 회원가입 되셨습니다.", Toast.LENGTH_SHORT).show()
                                val i = Intent(this@OkActivity, AuthActivity::class.java)
                                startActivity(i)
                                finish()
                            }
                        }
                    }

                    override fun onError(e: Exception) {
                        Log.e(TAG, "Confirm sign-up error", e)
                        if (e.message!!.contains("Value '' at 'confirmationCode' failed to satisfy constraint")) {
                            errorMessage("인증 코드를 입력해주세요.")
                        } else if (e.message!!.contains("Invalid verification code provided, please try again.")) {
                            errorMessage("인증 코드를 다시 확인해주세요.")
                        }
                    }
                })
        }

        // 인증 코드 재전송 버튼
        Re_Ok_button.setOnClickListener {
            AWSMobileClient.getInstance().resendSignUp(
                username,
                object :
                    Callback<SignUpResult> {
                    override fun onResult(signUpResult: SignUpResult) {
                        Log.i(TAG, "A verification code has been sent via" + signUpResult.userCodeDeliveryDetails.deliveryMedium + " at " + signUpResult.userCodeDeliveryDetails.destination
                        )
                        Toast.makeText(applicationContext, "인증 메일이 재전송 되었습니다.", Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(e: Exception) {
                        Log.e(TAG, e.toString())
                    }
                })
        }
    }

    // 뒤로가기 2번 눌러야 종료
    private val FINISH_INTERVAL_TIME: Long = 2500
    private var backPressedTime: Long = 0
    private var toast: Toast? = null
    override fun onBackPressed() {
        val tempTime = System.currentTimeMillis()
        val intervalTime = tempTime - backPressedTime


        // 뒤로 가기 할 경우 로그인 화면으로 이동
        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            val i = Intent(this@OkActivity, AuthActivity::class.java)
            startActivity(i)

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