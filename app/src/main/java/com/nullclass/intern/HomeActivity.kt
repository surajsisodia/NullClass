package com.nullclass.intern

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {
    lateinit var signOutButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        Snackbar.make(
            findViewById(android.R.id.content),
            "Welcome",
            Snackbar.LENGTH_LONG
        )
            .setBackgroundTint(getColor(R.color.success_color))
            .show()

        signOutButton = findViewById(R.id.sign_out_button)

        signOutButton.setOnClickListener(View.OnClickListener {
            FirebaseAuth.getInstance().signOut();
            startActivity(Intent(this, MainActivity::class.java))
            finish()

        })

    }
}