package com.erstuu.app.story.models

data class User(
    val email: String,
    val token: String,
    val isLogin: Boolean = false
)