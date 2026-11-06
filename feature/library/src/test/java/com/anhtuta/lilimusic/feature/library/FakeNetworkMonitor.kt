package com.anhtuta.lilimusic.feature.library

import com.anhtuta.lilimusic.core.data.network.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeNetworkMonitor(
    initiallyOnline: Boolean = true,
) : NetworkMonitor {
    private val online = MutableStateFlow(initiallyOnline)
    override val isOnline: Flow<Boolean> = online

    fun setOnline(value: Boolean) {
        online.value = value
    }
}
