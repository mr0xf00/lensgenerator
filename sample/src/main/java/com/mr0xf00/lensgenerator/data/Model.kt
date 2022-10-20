package com.mr0xf00.lensgenerator.data

import com.mr0xf00.lensgenerator.GenerateLens
import java.util.UUID

inline class UserId(val value: String) {
    companion object {
        fun random() = UserId(UUID.randomUUID().toString())
    }
}

enum class UserType {
    Admin, Developer, Viewer
}

@GenerateLens
data class User(val id: UserId, val name: String, val type: UserType) {
    companion object
}