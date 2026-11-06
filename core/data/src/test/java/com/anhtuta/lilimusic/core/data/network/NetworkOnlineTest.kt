package com.anhtuta.lilimusic.core.data.network

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NetworkOnlineTest {
    @Test
    fun requiresActiveNetworkAndInternetCapability() {
        assertThat(ConnectivityNetworkMonitor.isOnline(activeNetwork = false, hasInternet = false)).isFalse()
        assertThat(ConnectivityNetworkMonitor.isOnline(activeNetwork = true, hasInternet = false)).isFalse()
        assertThat(ConnectivityNetworkMonitor.isOnline(activeNetwork = false, hasInternet = true)).isFalse()
        assertThat(ConnectivityNetworkMonitor.isOnline(activeNetwork = true, hasInternet = true)).isTrue()
    }
}
