package com.iiwa.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User entity for Room database
 * Contains user information stored locally
 * 
 * @author IIWA Team
 * @date 2025-01-01
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val userId: String,
    val username: String,
    val token: String
)
