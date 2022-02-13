package com.nullclass.intern

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {

    lateinit var emailEditText: EditText
    lateinit var pwdEditText: EditText
    lateinit var loginButton: LinearLayout
    lateinit var googleLoginButton: LinearLayout
    lateinit var auth: FirebaseAuth
    lateinit var signUpText: TextView
    lateinit var loginLoading: ProgressBar
    lateinit var loginButtonContent: LinearLayout
    lateinit var visibilityIcon: ImageView

    var isIconVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        emailEditText = findViewById(R.id.emailEditText)
        pwdEditText = findViewById(R.id.pwdEditText)
        loginButton = findViewById(R.id.loginButton)
        googleLoginButton = findViewById(R.id.google_login_button)
        signUpText = findViewById(R.id.signUpBtn)
        loginLoading = findViewById(R.id.login_loading)
        loginButtonContent = findViewById(R.id.login_button_content)
        visibilityIcon = findViewById(R.id.visibility_icon_login)

        loginLoading.visibility = View.GONE


        loginButton.setOnClickListener(View.OnClickListener {
            loginButtonContent.visibility = View.GONE
            loginLoading.visibility = View.VISIBLE
            emailLogin()
        })

        googleLoginButton.setOnClickListener(View.OnClickListener {
            googleLogin()
        })

        signUpText.setOnClickListener(View.OnClickListener {
            var i = Intent(this, SignUpActivity::class.java)
            startActivity(i)
        })

        visibilityIcon.setOnClickListener(View.OnClickListener {
            if (!isIconVisible) {
                visibilityIcon.setImageResource(R.drawable.icon_password_visibility_off)
                pwdEditText.transformationMethod =null
            }
            else {
                visibilityIcon.setImageResource(R.drawable.icon_password_visible)
                pwdEditText.transformationMethod = PasswordTransformationMethod()
            }

            isIconVisible = !isIconVisible
        })

    }

    private fun googleLogin() {

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.Google_Auth_Web_Token))
            .requestEmail()
            .build()

        var googleSigninClient = GoogleSignIn.getClient(this, gso)

        val signInIntent = googleSigninClient.signInIntent
        startActivityForResult(signInIntent, 200)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 200) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")


                    val i = Intent(this, HomeActivity::class.java)
                    startActivity(i)
                    finish()
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    loginButtonContent.visibility = View.VISIBLE
                    loginLoading.visibility = View.GONE
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

    private fun emailLogin() {

        var email = emailEditText.text
        var pwd = pwdEditText.text;

        println("Email : $email")
        println("Password : $pwd")


        auth.signInWithEmailAndPassword(email.toString(), pwd.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Login Success")

                    val i = Intent(this, HomeActivity::class.java)
                    startActivity(i)
                    finish()
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    loginButtonContent.visibility = View.VISIBLE
                    loginLoading.visibility = View.GONE
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


