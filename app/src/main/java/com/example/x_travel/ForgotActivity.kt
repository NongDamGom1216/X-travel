package com.example.x_travel

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.results.ForgotPasswordResult
import com.amazonaws.mobile.client.results.ForgotPasswordState

class ForgotActivity : AppCompatActivity() {
    private val TAG = AuthActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)
        val code_button = findViewById<Button>(R.id.code_button) // 인증 코드 버튼
        val new_paw_button = findViewById<Button>(R.id.new_paw_button) // 비밀번호 재설정 버튼


        // 인증 버튼
        code_button.setOnClickListener { // 인증코드 확인
            val paw_signUpUsername = findViewById<View>(R.id.paw_signUpUsername) as EditText
            val username = paw_signUpUsername.text.toString()
            AWSMobileClient.getInstance().forgotPassword(username, object :
                Callback<ForgotPasswordResult> {
                override fun onResult(result: ForgotPasswordResult) {
                    runOnUiThread {
                        Log.d(TAG, "forgot password state: " + result.state)
                        if (result.state == ForgotPasswordState.CONFIRMATION_CODE) {
                            Toast.makeText(applicationContext, "이메일 주소로 인증 코드가 전송되었습니다.", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.e(TAG, "un-supported forgot password state")
                        }
                    }
                }

                override fun onError(e: Exception) {
                    Log.e(TAG, "forgot password error", e)
                    if (e.message!!.contains("Value at 'username' failed to satisfy constraint")) {
                        errorMessage("이메일을 입력해주세요.")
                    } else if (e.message!!.contains("Username/client id combination not found.")) {
                        errorMessage("이메일 주소와 일치하는 회원이 없습니다.")
                    }
                }
            })
        }


        // 비밀번호 재설정 버튼
        new_paw_button.setOnClickListener {
            // 인증코드, 비밀번호 재설정
            val paw_code_name = findViewById<View>(R.id.paw_code_name) as EditText
            val new_paw_name = findViewById<View>(R.id.new_paw_name) as EditText
            val CONFIRMATION_CODE = paw_code_name.text.toString()
            val NEW_PASSWORD_HERE = new_paw_name.text.toString()
            AWSMobileClient.getInstance().confirmForgotPassword(
                NEW_PASSWORD_HERE,
                CONFIRMATION_CODE,
                object :
                    Callback<ForgotPasswordResult> {
                    override fun onResult(result: ForgotPasswordResult) {
                        runOnUiThread {
                            Log.d(TAG, "forgot password state: " + result.state)
                            if (result.state == ForgotPasswordState.DONE) {

                                // 비밀번호가 재설정 되었스면 로그인 창으로 이동
                                Toast.makeText(applicationContext, "성공적으로 비밀번호가 재설정 되었습니다.", Toast.LENGTH_SHORT).show()
                                val i = Intent(this@ForgotActivity, AuthActivity::class.java)
                                startActivity(i)
                                finish()
                            } else {
                                Log.e(TAG, "un-supported forgot password state")
                            }
                        }
                    }

                    override fun onError(e: Exception) {
                        Log.e(TAG, "forgot password error", e)
                        when {
                            e.message!!.contains(" Value '' at 'confirmationCode' failed to satisfy constraint") -> {
                                errorMessage("인증 코드를 입력해주세요.")
                            }
                            e.message!!.contains("Invalid verification code provided, please try again.") -> {
                                errorMessage("인증 코드를 다시 확인해주세요.")
                            }
                            e.message!!.contains("Value at 'password' failed to satisfy constraint") -> {
                                errorMessage("비밀번호는 8자 이상이어야 하며 특수 문자를 반드시 포함해야 합니다.")
                            }
                            e.message!!.contains("Password did not conform with policy") -> {
                                errorMessage("비밀번호는 8자 이상이어야 하며 특수 문자를 반드시 포함해야 합니다.")
                            }
                        }
                    }
                })
        }
    }

    override fun onBackPressed() {
        val i = Intent(this@ForgotActivity, AuthActivity::class.java)
        startActivity(i)
        finish()
    }

    // 에러 메시지
    fun errorMessage(message: String?) {
        val mHandler = Handler(Looper.getMainLooper())
        mHandler.post { Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show() }
    }
}