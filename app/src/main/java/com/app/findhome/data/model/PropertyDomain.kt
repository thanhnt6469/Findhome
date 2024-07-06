package com.app.findhome.data.model

data class PropertyDomain(
    val id: Int = 0,
    val type: String = "",
    val title: String = "",
    val address: String = "",
    val pickPath: String = "",
    val price: Int = 0,
    val member: Int = 0,
    val size: Int = 0,
    val score: Double = 0.0,
    val description: String = "",
    val userId: String = "",
    val facilities: Map<String, Boolean> = mapOf()
)

