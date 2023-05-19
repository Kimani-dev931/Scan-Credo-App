package com.example.textscanner1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.net.URLEncoder

class EntryActivity : AppCompatActivity() {
    lateinit var prefix:EditText
    lateinit var suffix:EditText
    lateinit var proceedBtn:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)
         prefix=findViewById(R.id.editText_1)
         suffix=findViewById(R.id.editText_2)
         proceedBtn=findViewById(R.id.btn_proceed)

         proceedBtn.setOnClickListener {
             var myprefix=prefix.text.toString()
             var mysuffix=suffix.text.toString()

             mysuffix = URLEncoder.encode(mysuffix, "UTF-8")
             if (myprefix.isEmpty() && mysuffix.isEmpty()){
                 Toast.makeText(this,"Fill in the empty fields" , Toast.LENGTH_SHORT).show()
             }else{
                 val intent=Intent(this,MainActivity::class.java)
                 intent.putExtra("key1",myprefix)
                 intent.putExtra("key2",mysuffix)
                 startActivity(intent)

             }


         }
    }
}