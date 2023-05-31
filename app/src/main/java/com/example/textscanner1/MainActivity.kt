package com.example.textscanner1
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer


class MainActivity : AppCompatActivity() {

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
        private const val CALL_PHONE_PERMISSION_CODE = 101
        const val REQUEST_IMAGE_CAPTURE = 1
    }
    // creating variables for our
    // image view, text view and two buttons.
    private var img: ImageView? = null
    private var textview: TextView? = null
    private var snapBtn: Button? = null
    private var detectBtn: Button? = null
    private var value1: String? = null
    private var txt: String? = null
    private var value2: String? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var logoutButton: Button


    // variable for our image bitmap.
    private var imageBitmap: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        value1=intent.getStringExtra("key1")
        value2=intent.getStringExtra("key2")

        // on below line we are initializing our variables.
        img = findViewById<View>(R.id.image) as ImageView
        textview = findViewById<View>(R.id.text) as TextView
        snapBtn = findViewById<View>(R.id.snapbtn) as Button
        detectBtn = findViewById<View>(R.id.detectbtn) as Button
        logoutButton = findViewById(R.id.logoutButton)

        mAuth = FirebaseAuth.getInstance()
        logoutButton.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(this@MainActivity, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
        // adding on click listener for detect button.
        detectBtn!!.setOnClickListener { // calling a method to
            checkPermission(Manifest.permission.CALL_PHONE,
                CALL_PHONE_PERMISSION_CODE)
            // detect a text .
            detectTxt()

        }
        snapBtn!!.setOnClickListener { // calling a method to capture our image.
            checkPermission(Manifest.permission.CAMERA,
                CAMERA_PERMISSION_CODE)
            dispatchTakePictureIntent()
        }
    }

    private fun dispatchTakePictureIntent() {
        // in the method we are displaying an intent to capture our image.
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // on below line we are calling a start activity
        // for result method to get the image captured.
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // calling on activity result method.
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // on below line we are getting
            // data from our bundles. .
            val extras = data!!.extras
            imageBitmap = extras!!["data"] as Bitmap?

            // below line is to set the
            // image bitmap to our image.
            img!!.setImageBitmap(imageBitmap)
        }
    }

    private fun detectTxt() {
        // this is a method to detect a text from image.
        // below line is to create variable for firebase
        // vision image and we are getting image bitmap.
        val image = FirebaseVisionImage.fromBitmap(imageBitmap!!)

        // below line is to create a variable for detector and we
        // are getting vision text detector from our firebase vision.
        val detector: FirebaseVisionTextRecognizer =
            FirebaseVision.getInstance().onDeviceTextRecognizer

        // adding on success listener method to detect the text from image.
        detector.processImage(image)
            .addOnSuccessListener(OnSuccessListener<FirebaseVisionText> { firebaseVisionText -> // calling a method to process
                // our text after extracting.
                processTxt(firebaseVisionText)
            }).addOnFailureListener(OnFailureListener { // handling an error listener.
                Toast.makeText(
                    this@MainActivity,
                    "Fail to detect the text from image..",
                    Toast.LENGTH_SHORT
                ).show()
            })
    }

    fun processTxt(text: FirebaseVisionText) {
        // below line is to create a list of vision blocks which
        // we will get from our firebase vision text.
        val blocks: List<FirebaseVisionText.TextBlock> = text.getTextBlocks()

        // checking if the size of the block is not equal to zero.
        if (blocks.size == 0) {
            // if the size of blocks is zero then we are displaying
            // a toast message as no text detected.
            Toast.makeText(this@MainActivity, "No Text ", Toast.LENGTH_LONG).show()
            return
        }

        // extract only the first block from the list
        val firstBlock: FirebaseVisionText.TextBlock = blocks[0]

        // extract the first line from the first block
        val firstLine: FirebaseVisionText.Line? = firstBlock.lines.firstOrNull()

        // checking if the first line exists
        if (firstLine != null) {
            // below line is to get text from the first line.
            val firstLineText: String = firstLine.text

            // below line is to set our string to our text view.
            textview!!.text = firstLineText

            activateUSSD(firstLineText)
        }
    }

    private fun activateUSSD(firstLineText:String){
        val ussdCode = "${value1}$firstLineText${value2}"
        val intent=Intent(Intent.ACTION_CALL, Uri.parse("tel:$ussdCode"))
        if(ContextCompat.checkSelfPermission(this@MainActivity,android.Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this@MainActivity,arrayOf(android.Manifest.permission.CALL_PHONE),1)
        }
        else{
            startActivity(intent)
        }
    }


    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this@MainActivity, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
        } else {
            Toast.makeText(this@MainActivity, "Permission already granted", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "Camera Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == CALL_PHONE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "Call Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Call Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, EntryActivity::class.java)
        startActivity(intent)
    }
}
