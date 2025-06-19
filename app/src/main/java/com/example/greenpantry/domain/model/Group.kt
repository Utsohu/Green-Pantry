package com.example.greenpantry.domain.model

data class Group(
    val groupCode: String,
    var groupName: String,
    val creatorId: String?,
    var members: List<String?>
)