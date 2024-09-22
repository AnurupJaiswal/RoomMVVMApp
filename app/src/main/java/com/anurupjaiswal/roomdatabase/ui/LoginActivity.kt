package com.anurupjaiswal.roomdatabase.ui

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.anurupjaiswal.roomdatabase.BaseActivity
import com.anurupjaiswal.roomdatabase.R
import com.anurupjaiswal.roomdatabase.databinding.ActivityLoginBinding
import com.anurupjaiswal.roomdatabase.viewmodel.UserViewModel

/**
 * Activity for user login.
 * This activity handles user login and navigation to account creation or profile editing.
 */

class LoginActivity : BaseActivity() {

    private val userViewModel: UserViewModel by viewModels()  // ViewModel instance for user data
    private lateinit var binding: ActivityLoginBinding  // DataBinding instance for layout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.viewModel = userViewModel
        binding.lifecycleOwner = this  // This is necessary to observe LiveData changes

        // Set click listener for creating a new account
        binding.mcvCreateAccount.setOnClickListener {

            val intent = Intent(this, CreateAccountActivity::class.java)

            startActivity(intent)
        }


        // Observe login success LiveData to navigate to the next activity
        userViewModel.loginSuccess.observe(this, Observer { email ->
            email?.let {
                navigateToNextActivity(it)  // If login was successful, navigate to the next activity
            }
        })



    }

    /**
     * Navigates to the EditProfileActivity with the user's email.
     *
     * @param email The email of the user to pass to the next activity.
     */

    private fun navigateToNextActivity(email: String) {
        val intent = Intent(this, EditProfileActivity::class.java)
        intent.putExtra("email", email)
        startActivity(intent)
    }

}
