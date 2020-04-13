package com.example.firebase

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpAct : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()

        gotologin.setOnClickListener {
            startActivity(Intent(this,LoginAct::class.java))
        }

    }

    override fun onStart() {
        super.onStart()


    }

    override fun onResume() {
        super.onResume()
        val pd = ProgressDialog(this).apply {
            setTitle("جار التحقق من صحة البيانات , الرجاء الانتظار ...")
        }
        btnsignup.setOnClickListener {
            if ( (etemail.text.isNotEmpty()) && (etname.text.isNotEmpty()) && (etpass.text.isNotEmpty()) && (etrepass.text.isNotEmpty())){
                pd.show()
                pd.setCancelable(false)

                it.postDelayed({

                    val task = auth.createUserWithEmailAndPassword(etemail.text.toString(),etpass.text.toString())
                    task.addOnCompleteListener {
                        pd.dismiss()

                        if (task.isSuccessful){
                            val result = auth.currentUser
                            val name = etname.text.toString()
                            val email = result!!.email
                            val id = result.uid
//                        val name = result.displayName
//                        val phone = result.phoneNumber
//                        val img = result.photoUrl.toString()

                            Log.e("user","$email -$id -$name")

                            val i = Intent(this,HomeAct::class.java)
                            i.putExtra("email",email)
                            i.putExtra("id",id)
                            i.putExtra("name",name)
//                            i.putExtra("name",name)
//                            i.putExtra("phone",phone)
                            //      i.putExtra("img",img)
                            startActivity(i)


                        }else{
                            Toast.makeText(applicationContext,"Signup Failed !!",Toast.LENGTH_SHORT).show()
                        }
                    }
                    task.addOnCanceledListener {
                        Toast.makeText(applicationContext,"Canceled",Toast.LENGTH_SHORT).show()
                        pd.dismiss()

                    }
                    task.addOnFailureListener {
                        err.text = it.localizedMessage
                        pd.dismiss()
                    }

                },5000)



            }else{
                Toast.makeText(applicationContext,"\n Enter Correct Data !!! \n", Toast.LENGTH_SHORT).show()
                pd.dismiss()
            }


        }


    }




}
