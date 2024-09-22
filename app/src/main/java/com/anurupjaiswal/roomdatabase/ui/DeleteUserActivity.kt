package com.anurupjaiswal.roomdatabase.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.anurupjaiswal.roomdatabase.BaseActivity
import com.anurupjaiswal.roomdatabase.R
import com.anurupjaiswal.roomdatabase.databinding.ActivityDeleteUserBinding
import com.anurupjaiswal.roomdatabase.viewmodel.UserViewModel

/**
 * Activity for Delete the user account from RoomDataBase.
 */

class DeleteUserActivity : BaseActivity() {
    private lateinit var binding: ActivityDeleteUserBinding
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set up data binding with the layout and ViewModel
        binding = DataBindingUtil.setContentView(this, R.layout.activity_delete_user)
        binding.lifecycleOwner = this  // Set the lifecycle owner for LiveData binding
        binding.viewModel = userViewModel  // Bind the ViewModel to the layout

        val email = intent.getStringExtra("email")
        userViewModel.email.value = email


        // Observe if the user is deleted
        userViewModel.isUserDeleted.observe(this, Observer { isDeleted ->
            if (isDeleted) {
                // Show a toast message
                Toast.makeText(this, "User deleted successfully", Toast.LENGTH_SHORT).show()

                // Navigate to the login activity
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)

                // Finish this activity so the user cannot go back
                finish()
            }
        })
    }
}