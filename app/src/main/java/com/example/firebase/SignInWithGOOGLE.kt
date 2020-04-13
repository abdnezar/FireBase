package com.example.firebase

    import android.content.Intent
    import android.graphics.Bitmap
    import android.graphics.BitmapFactory
    import android.os.AsyncTask
    import android.os.Bundle
    import android.util.Log
    import android.view.View
    import android.widget.ImageView
    import android.widget.Toast
    import androidx.appcompat.app.AppCompatActivity
    import com.google.android.gms.auth.api.signin.GoogleSignIn
    import com.google.android.gms.auth.api.signin.GoogleSignInAccount
    import com.google.android.gms.auth.api.signin.GoogleSignInClient
    import com.google.android.gms.auth.api.signin.GoogleSignInOptions
    import com.google.android.gms.common.api.ApiException
    import com.google.android.material.snackbar.Snackbar
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.auth.FirebaseUser
    import com.google.firebase.auth.GoogleAuthProvider
    import kotlinx.android.synthetic.main.activity_sign_in_with_g_o_o_g_l_e.*
    import java.io.InputStream
    import java.net.URL


/**
     * Demonstrate Firebase Authentication using a Google ID Token.
     */
    class GoogleSignInActivity : AppCompatActivity(), View.OnClickListener {

        // [START declare_auth]
        private lateinit var auth: FirebaseAuth
        private lateinit var googleSignInClient: GoogleSignInClient

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_sign_in_with_g_o_o_g_l_e)

            // Button listeners
            signInButton.setOnClickListener(this)
            signOutButton.setOnClickListener(this)
            disconnectButton.setOnClickListener(this)

            // [START config_signin]
            // Configure Google Sign In
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            googleSignInClient = GoogleSignIn.getClient(this, gso)

            auth = FirebaseAuth.getInstance()
        }

        // [START on_start_check_user]
        public override fun onStart() {
            super.onStart()
            // Check if user is signed in (non-null) and update UI accordingly.
            val currentUser = auth.currentUser
            updateUI(currentUser)
        }

        // [START onactivityresult]
        public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java) // get Account Informations
                    firebaseAuthWithGoogle(account!!)



                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(TAG, "Google sign in failed", e)
                    // [START_EXCLUDE]
                    updateUI(null)
                    // [END_EXCLUDE]
                }
            }
        }

        // [START auth_with_google]
        private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
            Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)
            // [START_EXCLUDE silent]
            progressBar.visibility = View.VISIBLE

            val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
            auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        Snackbar.make(continer, "Authentication Failed. ${task.exception}", Snackbar.LENGTH_LONG).show()
                        Toast.makeText(applicationContext,"${task.exception}",Toast.LENGTH_LONG).show()
                        updateUI(null)
                    }

                    // [START_EXCLUDE]
                    progressBar.visibility = View.GONE
                }
        }

        override fun onClick(v: View) {
            when (v.id) {
                R.id.signInButton -> {
                    val signInIntent = googleSignInClient.signInIntent
                    startActivityForResult(signInIntent, RC_SIGN_IN)
                }

                R.id.signOutButton -> {
                    // Firebase sign out
                    auth.signOut()

                    // Google sign out
                    googleSignInClient.signOut().addOnCompleteListener(this) {
                        updateUI(null)
                    }
                }

                R.id.disconnectButton -> {
                    // Firebase sign out
                    auth.signOut()

                    // Google revoke access
                    googleSignInClient.revokeAccess().addOnCompleteListener(this) {
                        updateUI(null)
                    }
                }


            }
        }

        private fun updateUI(user: FirebaseUser?) {
        progressBar.visibility = View.GONE

        if (user != null) {
            status.text = "SignIn"

            // account Info
            detail.text = "${user.email} \n ${user.photoUrl} \n ${user.displayName}"
            Toast.makeText(applicationContext,"${user.displayName}",Toast.LENGTH_LONG).show()
            Toast.makeText(applicationContext,"${user.photoUrl}",Toast.LENGTH_LONG).show()

            GetImageTask(personimg).execute(user.photoUrl.toString())


//            startActivity(Intent(applicationContext,LoginAct::class.java))

            signInButton.visibility = View.GONE
            signOutButton.visibility = View.VISIBLE
            disconnectButton.visibility = View.VISIBLE
            Toast.makeText(applicationContext,"${user.email}",Toast.LENGTH_SHORT).show()

//            finish()
        } else {
            status.setText("No Account Found!!")
            detail.text = null

            personimg.setImageBitmap(BitmapFactory.decodeResource(resources,R.drawable.img))
            signInButton.visibility = View.VISIBLE
            disconnectButton.visibility = View.GONE
            signOutButton.visibility = View.GONE

        }
    }

        companion object {
            private const val TAG = "GoogleActivity"
            private const val RC_SIGN_IN = 9001
        }

    // To Get Account Image
    inner class GetImageTask(bmImage: ImageView) : AsyncTask<String?, Void?, Bitmap?>() {
        var bmImage: ImageView

        init {
            this.bmImage = bmImage
        }

        override fun doInBackground(vararg params: String?): Bitmap? {
            val urldisplay = params[0]
            var mIcon11: Bitmap? = null
            try {
                val `in`: InputStream = URL(urldisplay).openStream()
                mIcon11 = BitmapFactory.decodeStream(`in`)
            } catch (e: Exception) {
                Log.e("Error", e.message)
                e.printStackTrace()
            }
            return mIcon11
        }


        override fun onPostExecute(result: Bitmap?) {
            bmImage.setImageBitmap(result)
        }


    }

}