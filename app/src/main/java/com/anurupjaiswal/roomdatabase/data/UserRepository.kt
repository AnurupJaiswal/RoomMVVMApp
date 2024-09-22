package com.anurupjaiswal.roomdatabase.data

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
/**
 * Repository class that abstracts access to data sources
 */
class UserRepository(private val userDao: UserDAO) {

    /**
     * Retrieves all users from the database as LiveData.
     * LiveData allows automatic updates in the UI when the data changes.
     *
     * @return LiveData<List<User>> List of all users.
     */
    fun getAllUsers(): LiveData<List<User>> {
        return userDao.getUsers()
    }


    /**
     * Inserts a user into the database.
     *
     * @param user The user object to be inserted.
     */

    suspend fun insert(user: User) {
        userDao.insertUsers(user)
    }


    /**
     * Retrieves a user by their email.
     * The operation is performed in the IO thread.
     *
     * @param email The email address of the user.
     * @return User? The user object if found, otherwise null.
     */
    suspend fun getUserByEmail(email: String): User? {
        return withContext(Dispatchers.IO) {
            userDao.getUserByEmail(email)
        }
    }



    /**
     * Checks if an email already exists in the database.
     * The operation is performed in the IO thread.
     *
     * @param email The email address to check.
     * @return Boolean True if the email exists, false otherwise.
     */

    suspend fun checkIfEmailExists(email: String): Boolean {
        return withContext(Dispatchers.IO) {
            userDao.checkIfEmailExists(email) != null
        }
    }



    /**
     * Retrieves a user by their email and password.
     * This can be used for authentication or login.
     *
     * @param email The email address of the user.
     * @param password The password of the user.
     * @return User? The user object if found, otherwise null.
     */

    suspend fun getUserByEmailAndPassword(email: String, password: String): User? {
        return userDao.getUserByEmailAndPassword(email, password)
    }


    /**
     * Updates an existing user's details in the database.
     *
     * @param name The updated name of the user.
     * @param password The updated password of the user.
     * @param email The email address of the user to update.
     * @param country The updated country of the user.
     * @param state The updated state of the user.
     * @param profileImagePath The updated profile image path of the user.
     */

    suspend fun updateUser(name: String, password: String, email: String,country:String, state:String, profileImagePath:String?) {
        userDao.updateUser(name, password, email,country,state,profileImagePath)
    }


    /**
     * Deletes a user from the database by their email.
     * @param email The email address of the user to delete.
     */

    suspend fun deleteUserByEmail(email: String) {
        userDao.deleteUserByEmail(email)
    }


}

