package com.example.firebase

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import java.io.ByteArrayOutputStream


class LoginAct : AppCompatActivity() {

    final lateinit var auth : FirebaseAuth

    companion object{
        const val EMAIL = "email"
    }


    // save data before on Stop execute
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(EMAIL,etloginemail.text.toString())
        super.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // retrive Data
        if (savedInstanceState != null) {
            val returnedEmail = savedInstanceState.getString(EMAIL)
            etloginemail.setText("$returnedEmail")
            etloginemail.animate().rotation(360f).duration = 1500
            Log.e("EMAIL",savedInstanceState.getString(EMAIL))
        }


        /// Send image
        val bmp = BitmapFactory.decodeResource(resources, R.drawable.img)
        val stream = ByteArrayOutputStream()
        bmp.run {
            compress(Bitmap.CompressFormat.PNG, 100, stream)
        }
        val byteArray: ByteArray = stream.toByteArray()

        // Sign UP  Screen
        gotosignup.setOnClickListener {
            startActivity(Intent(this,SignUpAct::class.java))
        }

        val pd = ProgressDialog(this).apply {
            setTitle("جار التحقق من صحة البيانات , الرجاء الانتظار ...")
            setCancelable(false)
        }

        btnlogin.setOnClickListener { it ->
            pd.show()
            if (etloginemail.text.isNotEmpty() && etloginpass.text.isNotEmpty()) {

                    auth = FirebaseAuth.getInstance()
                    val loginresult = auth.signInWithEmailAndPassword(
                        etloginemail.text.toString(),
                        etloginpass.text.toString()
                    )
                    loginresult.addOnCompleteListener {
                        pd.dismiss()

                        if (loginresult.isSuccessful) {
                            val result = auth.currentUser
                            val email = result!!.email
                            val id = result.uid

                            Log.e("user", "$email -$id ")

                            val i = Intent(this, HomeAct::class.java)
                            i.putExtra("email", email)
                            i.putExtra("id", id)
                            i.putExtra("picture", byteArray)
                            startActivity(i)

                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Login Failed !!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    loginresult.addOnCanceledListener {
                        Toast.makeText(applicationContext, "Canceled", Toast.LENGTH_SHORT).show()
                        pd.dismiss()
                    }
                    loginresult.addOnFailureListener {
                        err.text = it.localizedMessage
                        pd.dismiss()
                    }

            }
        }



        /////// Link Email /////////
        btnemailLink.setOnClickListener {

            // SEND EMAIL AUTH
            val actionCodeSettings = ActionCodeSettings.newBuilder()
                // URL you want to redirect back to. The domain (www.example.com) for this
                // URL must be whitelisted in the Firebase Console.
                .setUrl("https://www.example.com/finishSignUp?cartId=1234")
                // This must be true
                .setHandleCodeInApp(true)
                .setIOSBundleId("com.example.ios")
                .setAndroidPackageName(
                    "com.example.firebase",
                    true, /* installIfNotAvailable */
                    "12" /* minimumVersion */
                )
                .build()

            val authSendEmail = FirebaseAuth.getInstance()
            authSendEmail.sendSignInLinkToEmail(etloginemail.text.toString(), actionCodeSettings)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("TAG", "Email sent.")
                        Toast.makeText(
                            applicationContext,
                            "Email Send Successful",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Log.d("TAG", "Email sent Failed!!!.(${task.exception})")
                        Toast.makeText(
                            applicationContext,
                            "Email Send Failed!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }


            // Check If User Verify Link and Signin Done...
            val auth = FirebaseAuth.getInstance()
            //           val intent = intent
//            val emailLink = intent.data!!.toString()

            // Confirm the link is a sign-in with email link.
            if (auth.isSignInWithEmailLink(etloginemail.text.toString())) {
                // Retrieve this from wherever you stored it
                val email = etloginemail.text.toString()

                // The client SDK will parse the code from the link for you.
                auth.signInWithEmailLink(email, etloginemail.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("TAG", "Successfully signed in with email link!")
                            val result = task.result
                            // You can access the new user via result.getUser()
                            // Additional user info profile *not* available via:
                            // result.getAdditionalUserInfo().getProfile() == null
                            // You can check if the user is new or existing:
                            // result.getAdditionalUserInfo().isNewUser()
                        } else {
                            Log.e("TAG", "Error signing in with email link", task.exception)
                        }
                    }
            }

        }
    }



}
