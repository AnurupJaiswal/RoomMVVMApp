package com.anurupjaiswal.roomdatabase.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.anurupjaiswal.roomdatabase.BaseActivity
import com.anurupjaiswal.roomdatabase.R
import androidx.activity.viewModels
import com.anurupjaiswal.roomdatabase.databinding.ActivityCreateAccountBinding
import com.anurupjaiswal.roomdatabase.viewmodel.UserViewModel
/**
 * Activity for creating a new user account.
 * It uses DataBinding to bind the ViewModel to the layout and observe LiveData changes.
 */

class CreateAccountActivity : BaseActivity() {

    // ViewModel instance for managing user data
    private val userViewModel: UserViewModel by viewModels()

    // DataBinding instance to bind the layout with the ViewModel
    private lateinit var binding: ActivityCreateAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the content view using DataBindingUtil and bind the ViewModel to the layout
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_account)

        // Bind ViewModel to layout
        binding.viewModel = userViewModel
        binding.lifecycleOwner = this


        // Set up observers for LiveData from the ViewModel
        setupObservers()
    }

    /**
     * Sets up LiveData observers for user data, email existence check, and form validation state.
     * Updates the UI (e.g., displaying Toast messages) based on the observed changes.
     */
    private fun setupObservers() {

        // Observe changes in the list of all users
        userViewModel.allUsers.observe(this) { usersData ->
            usersData?.let {
                for (user in it) {
                    Log.d("CreateAccountActivity", "User: ${user.name}, ${user.email}, ${user.country},${user.state},${user.password}")
                }
            }
        }

        // Observe whether the email already exists in the database
        userViewModel.emailExists.observe(this) { emailExists ->
            if (emailExists) {
                Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "User added successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        // Observe validation state of the form (checking if all fields are filled)
        userViewModel.validationState.observe(this) { isValid ->
            if (!isValid) {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
