package com.anhtuta.lilimusic.navigation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data object LibraryDestination

@Serializable
@SerialName("bai-hat")
data class PlayerDestination(
    val path: String,
)
