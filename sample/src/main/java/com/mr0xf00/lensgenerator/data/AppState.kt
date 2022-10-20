package com.mr0xf00.lensgenerator.data

import com.mr0xf00.lensgenerator.GenerateLens

@GenerateLens
data class AppState(val users: List<User>) {
    companion object
}