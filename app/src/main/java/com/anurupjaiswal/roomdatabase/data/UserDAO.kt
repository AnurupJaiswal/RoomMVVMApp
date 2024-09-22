package com.anurupjaiswal.roomdatabase.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/** Data Access Object (DAO) for User entity to handle database operations */

@Dao
interface UserDAO {

    /**
     * Insert a new user into the database.
     * This operation is done asynchronously (suspend function).
     */
    @Insert
    suspend fun insertUsers(user: User)

    /**
     * Update an existing user by their email address.
     * The user details (name, password, country, state, profile image path) can be updated.
     */

    @Query("UPDATE UsersList SET name = :name, password = :password,country = :country,state = :state,profileImagePath =:profileImagePath  WHERE email = :email")
    suspend fun updateUser(name: String, password: String,  email: String, country:String, state:String, profileImagePath: String?)


    /**
     * Delete a user by their email address.
     */

    @Query("DELETE FROM UsersList WHERE email = :email")
    suspend fun deleteUserByEmail(email: String)

    /**
     * Get a list of all users from the database.
     * Returns LiveData to observe changes.
     */

    @Query("SELECT * FROM UsersList")
    fun getUsers() : LiveData<List<User>>


    /**
     * Check if an email already exists in the database.
     * Returns a single User object or null if not found.
     */

    @Query("SELECT * FROM UsersList WHERE email = :email LIMIT 1")
    suspend fun checkIfEmailExists(email: String): User?

    /**
     * Get a user by their email and password, used for login or authentication.
     */

    @Query("SELECT * FROM UsersList WHERE email = :email AND password = :password")
    suspend fun getUserByEmailAndPassword(email: String, password: String): User?



    /**
     * Get a user by their email. This can be used for user profile or other features.
     */

    @Query("SELECT * FROM UsersList WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    /**
     * Get a LiveData-wrapped user object by their email, allowing for UI updates when data changes.
     */
    @Query("SELECT * FROM UsersList WHERE email = :email")
    fun getUserLiveData(email: String): LiveData<User?>
}