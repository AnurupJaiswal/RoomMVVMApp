package com.anurupjaiswal.roomdatabase.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Annotation to define this class as a Room entity and set the table name to "UsersList"
@Entity(tableName = "UsersList")
class User(
    // Defining the primary key for the entity (email in this case, must be unique)
    @PrimaryKey val email: String,

    // Name of the user
    val name: String,

    // User's password
    val password: String,

    // Country of residence
    val country: String,

    // State of residence
    val state: String,

    // Optional field to store the file path of the user's profile image, can be null
    val profileImagePath: String? = null
)
