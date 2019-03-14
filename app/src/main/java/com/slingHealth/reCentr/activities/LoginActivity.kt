package com.slingHealth.reCentr.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.slingHealth.reCentr.R
import com.slingHealth.reCentr.models.User
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        Log.d("ACTION: ", "signIn:$database")

        b_signin.setOnClickListener(this)
        b_signup.setOnClickListener(this)


    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser


        if (currentUser != null) {
            Log.d("OnStart: ", "${currentUser.email}")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


        // updateUI(currentUser)
    }

    private fun signIn(email: String, password: String) {
        Log.d("ACTION: ", "signIn:$email")
        if (!validateForm()) {
            return
        }

        //showProgressDialog()

        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("ACTION: ", "signInWithEmail:success")
                    val user = auth.currentUser
                    //updateUI(user)
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("ACTION: ", "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    //updateUI(null)
                }

                // [START_EXCLUDE]
//                if (!task.isSuccessful) {
//                    status.setText(R.string.auth_failed)
//                }
                //hideProgressDialog()
                // [END_EXCLUDE]
            }
        // [END sign_in_with_email]
    }

    private fun signUp(email: String, password: String) {
        Log.d("", "createAccount:$email")
        if (!validateForm()) {
            return
        }

        //showProgressDialog()

        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    onAuthSuccess(task.result?.user!!)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                // [START_EXCLUDE]
                //hideProgressDialog()
                // [END_EXCLUDE]
            }
        // [END create_user_with_email]
    }


    private fun validateForm(): Boolean {
        var valid = true

        val email = et_email.text.toString()
        if (TextUtils.isEmpty(email)) {
            et_email.error = "Required."
            valid = false
        } else {
            et_email.error = null
        }

        val password = et_password.text.toString()
        if (TextUtils.isEmpty(password)) {
            et_password.error = "Required."
            valid = false
        } else {
            et_password.error = null
        }

        return valid
    }


    override fun onClick(v: View) {
        when (v) {
            b_signin -> signIn(et_email.text.toString(), et_password.text.toString())
            b_signup -> signUp(et_email.text.toString(), et_password.text.toString())
        }
    }

    private fun onAuthSuccess(user: FirebaseUser) {

        Log.d("OnAuthSuccess: ", "${user.uid}")

        // Write new user
        writeNewUser(user.uid, user.email)

        // Go to MainActivity
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun writeNewUser(userId: String, email: String?) {
        val user = User(userId, email)
        database.child("users").child(userId).setValue(user)
    }
}
