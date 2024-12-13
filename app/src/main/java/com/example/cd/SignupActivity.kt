package com.example.cd

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_activity)

        val usernameEditText = findViewById<EditText>(R.id.username)
        val emailEditText = findViewById<EditText>(R.id.email)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val signupButton = findViewById<Button>(R.id.signupButton)
        val login = findViewById<TextView>(R.id.loginText)
        val text = "Already have an account? Login"
        val spannableString = SpannableString(text)
        val signupClick = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Navigate to SignupActivity
                val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        // Set the ClickableSpan for the word "Signup"
        val signupStartIndex = text.indexOf("Login")
        spannableString.setSpan(signupClick, signupStartIndex, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Apply the SpannableString to the TextView
        login.text = spannableString
        login.movementMethod = LinkMovementMethod.getInstance()

        signupButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            signupUser(username, email, password)
        }
    }

    private fun signupUser(username: String, email: String, password: String) {
        val apiService = RetrofitInstance.api

        apiService.signup(username, email, password).enqueue(object : Callback<SignupResponse> {
            override fun onResponse(call: Call<SignupResponse>, response: Response<SignupResponse>) {
                if (response.isSuccessful) {
                    val signupResponse = response.body()
                    if (signupResponse?.success == true) {
                        Toast.makeText(
                            this@SignupActivity,
                            signupResponse.message,
                            Toast.LENGTH_SHORT
                        ).show()

                        // Navigate back to LoginActivity
                        finish()
                    } else {
                        Toast.makeText(
                            this@SignupActivity,
                            "Signup failed: ${signupResponse?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@SignupActivity,
                        "Error: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                Toast.makeText(this@SignupActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}