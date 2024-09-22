package com.anurupjaiswal.roomdatabase.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
/**
 * Handles runtime permission requests for the application.
 * This includes permissions for reading media, camera access, and audio recording.
 *
 * @property activity The activity from which permissions are requested.
 */
class PermissionHandler(private val activity: Activity) {

    companion object {
        const val REQUEST_CODE = 1001  // Request code for permission requests
    }

    /**
     * Requests the necessary permissions from the user.
     * It checks for permissions based on the Android version and prompts the user if needed.
     */
    fun requestPermissions() {
        val permissionsNeeded = mutableListOf<String>()

        // Check permissions based on the Android version
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (!isPermissionGranted(Manifest.permission.READ_MEDIA_IMAGES)) {
                permissionsNeeded.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
            if (!isPermissionGranted(Manifest.permission.READ_MEDIA_VIDEO)) {
                permissionsNeeded.add(Manifest.permission.READ_MEDIA_VIDEO)
            }
        } else {
            if (!isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            if (!isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
        // Always check for camera and audio permissions
        if (!isPermissionGranted(Manifest.permission.CAMERA)) {
            permissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (!isPermissionGranted(Manifest.permission.RECORD_AUDIO)) {
            permissionsNeeded.add(Manifest.permission.RECORD_AUDIO)
        }

        // Request the permissions if any are needed
        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(activity, permissionsNeeded.toTypedArray(), REQUEST_CODE)
        }
    }

    /**
     * Checks if a specific permission is granted.
     *
     * @param permission The permission to check.
     * @return Boolean indicating whether the permission is granted.
     */
    private fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Handles the result of permission requests.
     *
     * @param requestCode The request code passed in requestPermissions.
     * @param grantResults The grant results for the corresponding permissions.
     */
    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE) {
            val deniedPermissions = grantResults.indices.filter { grantResults[it] != PackageManager.PERMISSION_GRANTED }
            if (deniedPermissions.isNotEmpty()) {
                showPermissionDeniedDialog()
            }
        }
    }

    /**
     * Displays a dialog informing the user that permissions are needed.
     */
    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(activity)
            .setTitle("Permissions Needed")
            .setMessage("This app requires permissions to access storage, camera, and audio.")
            .setPositiveButton("OK") { _, _ ->
                // Open the app info settings
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", activity.packageName, null)
                intent.data = uri
                activity.startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }  // Dismiss the dialog
            .create()
            .show()
    }

}
