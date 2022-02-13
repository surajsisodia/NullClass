package com.nullclass.intern

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    lateinit var emailEditText: EditText
    lateinit var pwdEditText: EditText
    lateinit var confPwdEditText: EditText
    lateinit var signupButton: LinearLayout
    lateinit var loginButton: TextView
    lateinit var auth: FirebaseAuth
    lateinit var visibilityIcon: ImageView
    lateinit var confVisibilityIcon: ImageView
    lateinit var signUpContent : LinearLayout
    lateinit var signLoading : ProgressBar

    var isPwdVisible = false
    var isConfPwdVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()

        emailEditText = findViewById(R.id.sign_up_email)
        pwdEditText = findViewById(R.id.sign_up_pwd)
        confPwdEditText = findViewById(R.id.sign_up_con_pwd)
        visibilityIcon = findViewById(R.id.visibility_icon_sign_up)
        confVisibilityIcon = findViewById(R.id.visibility_icon_sign_up_conf)
        signupButton = findViewById(R.id.sign_Button)
        loginButton = findViewById(R.id.login_in_sign_up)
        signUpContent = findViewById(R.id.sign_button_content)
        signLoading = findViewById(R.id.signup_loading)


        signLoading.visibility = View.GONE

        loginButton.setOnClickListener(View.OnClickListener {
            finish()
        })

        signupButton.setOnClickListener(View.OnClickListener {
            if (pwdEditText.text.toString() != confPwdEditText.text.toString())
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Password Mismatch", Snackbar.LENGTH_LONG
                ).setBackgroundTint(getColor(R.color.error_color)).show()
            else {
                signUpContent.visibility = View.GONE
                signLoading.visibility = View.VISIBLE
                signUp()
            }
        })

        visibilityIcon.setOnClickListener(View.OnClickListener {
            if (!isPwdVisible) {
                visibilityIcon.setImageResource(R.drawable.icon_password_visibility_off)
                pwdEditText.transformationMethod = null
            } else {
                visibilityIcon.setImageResource(R.drawable.icon_password_visible)
                pwdEditText.transformationMethod = PasswordTransformationMethod()
            }

            isPwdVisible = !isPwdVisible
        })

        confVisibilityIcon.setOnClickListener(View.OnClickListener {
            if (!isConfPwdVisible) {
                confVisibilityIcon.setImageResource(R.drawable.icon_password_visibility_off)
                confPwdEditText.transformationMethod = null
            } else {
                confVisibilityIcon.setImageResource(R.drawable.icon_password_visible)
                confPwdEditText.transformationMethod = PasswordTransformationMethod()
            }

            isConfPwdVisible = !isConfPwdVisible
        })
    }

    private fun signUp() {
        val email = emailEditText.text
        val pwd = pwdEditText.text

        auth.createUserWithEmailAndPassword(email.toString(), pwd.toString())
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {
                    Log.d("Auth Success", "Email Signup Success")
                    var i = Intent(this, HomeActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(i)
                    finish()
                } else {
                    signUpContent.visibility = View.VISIBLE
                    signLoading.visibility = View.GONE

                    Log.e("Auth Error", task.exception?.message ?: "Sign up failed")
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        task.exception?.message ?: "Error Encountered",
                        Snackbar.LENGTH_LONG
                    )
                        .setBackgroundTint(getColor(R.color.error_color))
                        .show()
                }

            }
    }
}