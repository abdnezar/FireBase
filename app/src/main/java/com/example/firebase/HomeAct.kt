package com.example.firebase

  import android.content.Intent
  import android.graphics.BitmapFactory
  import android.net.Uri
  import android.os.Bundle
  import android.util.Log
  import android.view.Gravity
  import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
  import com.google.firebase.auth.EmailAuthProvider
  import com.google.firebase.auth.FirebaseAuth
  import com.google.firebase.auth.UserProfileChangeRequest
  import kotlinx.android.synthetic.main.activity_home.*


class HomeAct : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        email.text = intent.getStringExtra("email")
        id.text = intent.getStringExtra("id")
        name.text = intent.getStringExtra("name")

        // Recive Image Form Login Act
        val extras = intent.extras
        val byteArray = extras!!.getByteArray("picture")
        val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
        reciveImg.setImageBitmap(bmp)

        //Access User Data
        // Photo and Name will return NULL , Should Register With Google Account TO SEE IT.
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            // Name, email address, and profile photo Url
            val name = user.displayName
            val email = user.email
            val photoUrl = user.photoUrl

            // Check if user's email is verified
            val emailVerified = user.isEmailVerified

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            val uid = user.uid

            Toast.makeText(applicationContext,">             USERDATA          < \n Username($name) \n Email($email) \n PhotoUrl($photoUrl) \n isVerified($emailVerified) \n ID($uid)",Toast.LENGTH_LONG).apply {
                setGravity(Gravity.CENTER,0,0)
                show()
            }
        }


        // Check if User SignIn
        val isSignIn = FirebaseAuth.getInstance().currentUser
        if (isSignIn != null) {
            Toast.makeText(applicationContext,FirebaseAuth.getInstance().currentUser!!.email+" IS SIGNIN",Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(applicationContext,FirebaseAuth.getInstance().currentUser!!.email+" IS NOT SIGNIN",Toast.LENGTH_LONG).show()
        }


        //Get Provider Data
        val providerInfo = FirebaseAuth.getInstance().currentUser
        providerInfo?.let {
            for (profile in it.providerData) {
                // Id of the provider (ex: google.com)
                val providerId = profile.providerId

                // UID specific to the provider
                val uid = profile.uid

                // Name, email address, and profile photo Url
                val name = profile.displayName
                val email = profile.email
                val photoUrl = profile.photoUrl

                Toast.makeText(applicationContext,">          PROVIDERDATA           < \n ProviderUID ($providerId) \n ProviderID ($uid) \n ProviderName ($name) \n ProviderEmail ($email) \n ProviderPhotoURL ($photoUrl) \n",Toast.LENGTH_LONG).show()

            }
        }
        ////////

        // Update User Data
        val updateuser = FirebaseAuth.getInstance().currentUser

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName("Jane Q. User")
            .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
            .build()

        updateuser?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "User profile updated.")
                    Toast.makeText(applicationContext," >     UserData Updated Successfully     < \n new Name : ${FirebaseAuth.getInstance().currentUser!!.displayName}",Toast.LENGTH_LONG).show()
                }
            }
        ///////


        // Update User EMAIL ::
        val updateEmail = FirebaseAuth.getInstance().currentUser
        updateEmail?.updateEmail("abooodnezar2018@gmail.com")
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "User email address updated.")
                    Toast.makeText(applicationContext,"Email Updated Successfully : ${FirebaseAuth.getInstance().currentUser!!.email}",Toast.LENGTH_LONG).show()
                }
            }
        //////////



        // Send Email Verfication
        val auth = FirebaseAuth.getInstance()
        val emailVerifay = auth.currentUser
        emailVerifay?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "Email sent.")
                    Toast.makeText(applicationContext,"Email Verify Successfully : ${FirebaseAuth.getInstance().currentUser!!.email}",Toast.LENGTH_LONG).show()
                }
            }
        auth.setLanguageCode("ar")
        // To apply the default app language instead of explicitly setting it.
        // auth.useAppLanguage()
        ////////



        /// Update User PASSWORD :::
        val updatepass = FirebaseAuth.getInstance().currentUser
        val newPassword = "123456"

        updatepass?.updatePassword(newPassword)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "User password updated.")
                    Toast.makeText(applicationContext,"Password Changes Successfully : ${FirebaseAuth.getInstance().currentUser!!.email}",Toast.LENGTH_LONG).show()

                }
            }
        //////



        // Password Changed Email Notify :
        val passChangeEmail = FirebaseAuth.getInstance()
        passChangeEmail.sendPasswordResetEmail(passChangeEmail.currentUser!!.email.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "Email sent.")
                    Toast.makeText(applicationContext,"Password Changes Email Notify Sent Successfully : ${FirebaseAuth.getInstance().currentUser!!.email}",Toast.LENGTH_LONG).show()
                }
            }
        //auth.setLanguageCode("fr")
        // To apply the default app language instead of explicitly setting it.
        passChangeEmail.useAppLanguage()
        ////



        fun deletAccount() {
            // DELETE USER
            val delUser = FirebaseAuth.getInstance().currentUser
            delUser?.delete()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("TAG", "User account deleted.")
                        Toast.makeText(
                            applicationContext,
                            "Your Account Deleted Successfully : ${FirebaseAuth.getInstance().currentUser!!.email}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }


            ////// Re_authentication   -> Is Requiered For (Change Email , Change Password, Delete Account)
            // Get auth credentials from the user for re-authentication. The example below shows
            // email and password credentials but there are multiple possible providers,
            // such as GoogleAuthProvider or FacebookAuthProvider.
            val credential = EmailAuthProvider.getCredential("user@example.com", "password1234")

            // Prompt the user to re-provide their sign-in credentials
            user?.reauthenticate(credential)
                ?.addOnCompleteListener {
                    Log.d("TAG", "User re-authenticated.")
                }
            /////
        }



        ///Get All Users
        //firebase auth:import users.json --hash-algo=scrypt --rounds=8 --mem-cost=14    ->Firebase CLI's



        // SignOut Action
        btnSignOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(applicationContext,LoginAct::class.java))
            try {
                if (isSignIn != null){
                    Toast.makeText(applicationContext,FirebaseAuth.getInstance().currentUser!!.email+" IS SIGNIN",Toast.LENGTH_LONG).show()
                }
            }catch (e : Exception){
                Toast.makeText(applicationContext,"You SignOut ",Toast.LENGTH_LONG).show()
            }
        }
        //////

        next.setOnClickListener {
            startActivity(Intent(this,TextSwitcherAct::class.java))
        }
    }
}
