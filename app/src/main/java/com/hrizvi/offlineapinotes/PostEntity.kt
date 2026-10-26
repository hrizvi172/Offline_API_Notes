package com.hrizvi.offlineapinotes

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_posts")
data class PostEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val title: String,
    val body: String
)

fun Post.toEntity() = PostEntity(id, userId, title, body)