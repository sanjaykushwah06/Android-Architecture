package com.iiwa.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.iiwa.data.model.User

/**
 * Data Access Object for User entity
 * Provides database operations for User table
 * 
 * @author IIWA Team
 * @date 2025-01-01
 */
@Dao
interface UserDao {
    
    /**
     * Insert a new user into the database
     * If user with same ID exists, it will be replaced
     * 
     * @param user The user to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)
}
