package com.anurupjaiswal.roomdatabase.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.anurupjaiswal.roomdatabase.data.User
import com.anurupjaiswal.roomdatabase.data.UserDatabase
import com.anurupjaiswal.roomdatabase.data.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    // LiveData for observing user-related data
    private val repository: UserRepository
    val allUsers: LiveData<List<User>>
    val email = MutableLiveData<String>()// User email input
    val name = MutableLiveData<String>()  // User name input
    val password = MutableLiveData<String>()// User password input
    val country = MutableLiveData<String>()// User countryName input
    val state = MutableLiveData<String>()  // User stateName input
    val user = MutableLiveData<User?>()  // LiveData for the user data
    val loginSuccess = MutableLiveData<String?>()// LiveData to indicate successful login
    val updateResult = MutableLiveData<Boolean>() // LiveData for profile update result
    val validationState = MutableLiveData<Boolean>()  // LiveData for input validation state
    val emailExists = MutableLiveData<Boolean>() // LiveData to check if email already exists
    val isUserDeleted = MutableLiveData<Boolean>()// LiveData to indicate if user is deleted
    val profileImageUri = MutableLiveData<String?>() // LiveData for storing profile image URI

    init {
        val userDAO = UserDatabase.getDatabase(application).UserDAO()
        repository = UserRepository(userDAO)
        allUsers = repository.getAllUsers() // Get all users from the repository

        // Initialize LiveData user based on userEmail

    }

    // Function to insert a new user into the database
    private fun insertUser(name: String, email: String, pass: String ,country:String, state:String) {
        viewModelScope.launch {
            repository.insert(User(name = name, email = email, password = pass, country = country, state = state))
        }
    }



    // Function to fetch user by email
    fun getUserByEmail(email: String) {
        viewModelScope.launch {
            val userResult = repository.getUserByEmail(email)
            user.postValue(userResult)
        }
    }

    // Check if an email exists in the database
    private suspend fun checkIfEmailExists(email: String): Boolean {
        return repository.checkIfEmailExists(email)
    }



    // Function to handle user login
    fun login() {
        val emailValue = email.value?.trim()
        val passwordValue = password.value?.trim()

        if (emailValue.isNullOrEmpty() || passwordValue.isNullOrEmpty()) {
            Toast.makeText(getApplication(), "Please enter both email and password", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            val user = repository.getUserByEmailAndPassword(emailValue, passwordValue)
            if (user != null) {
                Toast.makeText(getApplication(), "Login successful", Toast.LENGTH_SHORT).show()
                // Notify the Activity that login was successful and pass the email
                loginSuccess.postValue(emailValue)
            } else {
                Toast.makeText(getApplication(), "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to handle profile update on save button click
    fun onSaveButtonClick() {
        val updatedName = name.value ?: ""
        val updatedPassword = password.value ?: ""
        val currentEmail = email.value ?: ""
        val currentCountry = country.value ?: ""
        val currentState = state.value ?: ""
        val updatedProfileImageUri = profileImageUri.value ?: "" // Get the selected profile image URI

        if (user.value != null && currentEmail == user.value!!.email) {
            // Call the repository to update the user
            val updatedUser = User(name = updatedName, password = updatedPassword, email = currentEmail, country = currentCountry, state = currentState, profileImagePath = updatedProfileImageUri
            )
            viewModelScope.launch {
                repository.updateUser(updatedUser.name, updatedUser.password, updatedUser.email,updatedUser.country,updatedUser.state,updatedUser.profileImagePath)
                updateResult.postValue(true) // Notify success
            }
        } else {
            updateResult.postValue(false) // Notify failure if email was changed
        }
    }


    // Function to delete a user by email
    fun deleteUser() {
        val userEmail = email.value ?: return

        viewModelScope.launch {
            repository.deleteUserByEmail(userEmail)
            isUserDeleted.postValue(true)  // User deleted, update the LiveData

        }
    }


    // Function to handle sign-up button click
    fun onSignupClicked() {
        val nameValue = name.value?.trim()
        val emailValue = email.value?.trim()
        val passwordValue = password.value?.trim()
        val countryValue = country.value?.trim()
        val stateValue = state.value?.trim()

        // Validate inputs
        if (nameValue.isNullOrBlank() || emailValue.isNullOrBlank() || passwordValue.isNullOrBlank()|| countryValue.isNullOrBlank()|| stateValue.isNullOrBlank()) {
            validationState.postValue(false)
            return
        }

        // Check if the email format is valid
        if (!isValidEmail(emailValue)) {
            Toast.makeText(getApplication(), "Please enter a valid email", Toast.LENGTH_SHORT).show()
            return
        }

        // Proceed with signup
        viewModelScope.launch {
            val emailExistsValue = checkIfEmailExists(emailValue)
            emailExists.postValue(emailExistsValue)
            if (!emailExistsValue) {
                insertUser(nameValue, emailValue, passwordValue,countryValue,stateValue)
            }
        }
    }

    // Function to validate email format
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailRegex.toRegex())
    }



}
