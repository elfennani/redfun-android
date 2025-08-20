package com.elfen.redfun.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.elfen.redfun.domain.model.Profile

@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey val id: String,
    val username: String,
    val fullName: String? = null,
    val icon: String? = null,
    val banner: String? = null,
    val commentKarma: Int = 0,
    val linkKarma: Int = 0,
    val totalKarma: Int = 0,
    val isMod: Boolean = false
)

fun ProfileEntity.toAppModel() = Profile(
    id = id,
    username = username,
    fullName = fullName,
    icon = icon,
    banner = banner,
    commentKarma = commentKarma,
    linkKarma = linkKarma,
    totalKarma = totalKarma,
    isMod = isMod
)