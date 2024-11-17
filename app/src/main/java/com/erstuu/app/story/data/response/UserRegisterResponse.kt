package com.erstuu.app.story.data.response

import com.google.gson.annotations.SerializedName

data class UserRegisterResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
