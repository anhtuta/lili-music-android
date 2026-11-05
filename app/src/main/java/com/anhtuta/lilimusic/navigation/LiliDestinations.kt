package com.anhtuta.lilimusic.navigation

import kotlinx.serialization.Serializable

@Serializable
data object LibraryDestination

@Serializable
data class PlayerDestination(
    val songId: Long,
)
