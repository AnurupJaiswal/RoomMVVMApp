package com.anurupjaiswal.roomdatabase.ui

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.anurupjaiswal.roomdatabase.BaseActivity
import com.anurupjaiswal.roomdatabase.R
import com.anurupjaiswal.roomdatabase.databinding.ActivityEditProfileBinding
import com.anurupjaiswal.roomdatabase.viewmodel.UserViewModel
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import java.io.IOException


/**
 * Activity for editing user profile information.
 * This includes updating user details and selecting a profile image from the gallery.
 */
class EditProfileActivity : BaseActivity() {

    private val userViewModel: UserViewModel by viewModels()
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var permissionHandler: PermissionHandler



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        binding.viewModel = userViewModel
        binding.lifecycleOwner = this


        // Initialize the PermissionHandler and request necessary permissions
        permissionHandler = PermissionHandler(this) // Initialize the PermissionHandler
        permissionHandler.requestPermissions() // Request necessary permissions


        val email = intent.getStringExtra("email")
        email?.let {
            // Fetch user details for the provided email
            userViewModel.getUserByEmail(it)
        }


        // Set onClick listener to the profile image view for picking an image
        binding.mcvProfile.setOnClickListener {
            // Check for permissions before picking image
            if (hasPermissions()) {
                pickImageFromGallery()
            } else {
                permissionHandler.requestPermissions()
            }
        }


        // Observe the user LiveData to update the UI with user details
        userViewModel.user.observe(this) { user ->
            user?.let {

                Log.d(TAG, "User: ${user.name}, ${user.email}, ${user.country},${user.state},${user.password}")
                // Bind user details to the UI
                binding.etName.setText(it.name)
                binding.etEmail.setText(it.email)
                binding.etPassword.setText(it.password)
                binding.etCountry.setText(it.country)
                binding.etState.setText(it.state)
                if (it.profileImagePath.isNullOrEmpty()) {
                    // Set default image if profileImagePath is null or empty
                    binding.profileImageView.setImageResource(R.drawable.ic_launcher_background)
                } else {
                    // Load the actual profile image if available
                    loadImageFromUri(Uri.parse(it.profileImagePath))
                }

            }
        }


        // Observe update result from the ViewModel
        userViewModel.updateResult.observe(this) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Email cannot be changed", Toast.LENGTH_SHORT).show()
            }
        }


        // Navigate to DeleteUserActivity when delete account button is clicked
        binding.mcvDeleteAccount.setOnClickListener {

             email?.let { it1 -> navigateToNextActivity(it1) }
         }

        // Finish activity when back button is clicked
        binding.icBack.setOnClickListener {
            finish()
        }
    }



    /**
     * Navigates to DeleteUserActivity with the user's email.
     *
     * @param email The email of the user to pass to the next activity.
     */

    private fun navigateToNextActivity(email: String) {
        val intent = Intent(this, DeleteUserActivity::class.java)
        intent.putExtra("email", email)
        startActivity(intent)
    }

    /**
     * Initiates the process to pick an image from the gallery.
     */

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch("image/*") // This opens the gallery to pick any type of image
    }





    // Activity result launcher for picking content from gallery
    private val galleryLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { imageUri ->
                try {
                    // Persist the URI permission
                    contentResolver.takePersistableUriPermission(
                        imageUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )

                    val inputStream = contentResolver.openInputStream(imageUri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    binding.profileImageView.setImageBitmap(bitmap)

                    // Save image URI in the ViewModel
                    userViewModel.profileImageUri.value = imageUri.toString()
                } catch (e: IOException) {
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(this, "Image not selected", Toast.LENGTH_SHORT).show()
            }
        }


    /**
     * Checks if the necessary permissions for accessing external storage are granted.
     *
     * @return Boolean True if permissions are granted, false otherwise.
     */
    private fun hasPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Handles the result of permission requests.
     *
     * @param requestCode The request code associated with the permission request.
     * @param permissions The requested permissions.
     * @param grantResults The grant results for the corresponding permissions.
     */

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHandler.onRequestPermissionsResult(requestCode, grantResults)
    }



    /**
     * Loads an image from the given URI and displays it in the profile image view.
     *
     * @param uri The URI of the image to load.
     */

    private fun loadImageFromUri(uri: Uri) {
        try {
            // Request permission to persist the URI
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)

            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            binding.profileImageView.setImageBitmap(bitmap)
        } catch (e: IOException) {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            Toast.makeText(this, "Permission to access the image is missing", Toast.LENGTH_SHORT).show()
        }
    }

}
