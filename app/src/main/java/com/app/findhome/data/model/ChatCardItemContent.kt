package com.app.findhome.data.model

data class ChatCardItemContent(
    val id : String,
    val image : String,
    val messengerName:String,
    val message:String,
    val messageDate:String,
    val messageNumberBadge:Int = 0
)