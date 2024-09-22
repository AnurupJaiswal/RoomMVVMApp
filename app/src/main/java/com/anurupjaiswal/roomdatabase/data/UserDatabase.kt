package com.anurupjaiswal.roomdatabase.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room Database class for the User entity.
 * Defines the database configuration and serves as the main access point to the persisted data.
 */

@Database(entities = [User::class], version = 2 )
abstract class UserDatabase: RoomDatabase() {

    /**
     * Abstract function to get the UserDAO for database operations.
     */

    abstract fun UserDAO(): UserDAO

    companion object{

        // Singleton instance of UserDatabase
        private var INSTANCE: UserDatabase? =null

        fun getDatabase(context: Context): UserDatabase {
            if (INSTANCE ==null){

                /** Synchronized block to prevent multiple instances of the database being created */
                synchronized(this){
                    INSTANCE = Room.databaseBuilder(


                        context.applicationContext, UserDatabase::class.java, "UserDB"
                    ).build()
                }
                }
            return INSTANCE!!
        }
    }

}