package com.example.textscanner1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class SplashScreenActivity : AppCompatActivity() {
    lateinit var imageview:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        imageview=findViewById(R.id.Iv_scanner)
        imageview.alpha = 0f
        imageview.animate().setDuration(1500).alpha(1f).withEndAction{
            val intent =Intent(this,SignInActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            finish()
        }
    }
}