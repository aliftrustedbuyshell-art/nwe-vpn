package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.vpn.controller.VpnController
import com.example.vpn.model.ConnectionState
import com.example.vpn.model.DnsProvider
import com.example.vpn.model.VpnProtocol
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun verifyAppName() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Aegis VPN", appName)
    }

    @Test
    fun verifyVpnControllerInitialState() {
        assertNotNull(VpnController.selectedServer.value)
        assertEquals(VpnProtocol.WIREGUARD, VpnController.selectedProtocol.value)
        assertEquals(DnsProvider.CLOUDFLARE, VpnController.dnsProvider.value)
        assertTrue(VpnController.killSwitch.value)
        assertTrue(VpnController.dnsLeakProtection.value)
    }

    @Test
    fun verifyServerAndProtocolSelection() {
        val servers = VpnController.servers.value
        assertTrue(servers.isNotEmpty())

        val testServer = servers.first { it.id == "de_fra" }
        VpnController.selectServer(testServer)
        assertEquals("de_fra", VpnController.selectedServer.value.id)

        VpnController.selectProtocol(VpnProtocol.OPENVPN_UDP)
        assertEquals(VpnProtocol.OPENVPN_UDP, VpnController.selectedProtocol.value)

        VpnController.toggleKillSwitch(false)
        assertFalse(VpnController.killSwitch.value)
        VpnController.toggleKillSwitch(true)
        assertTrue(VpnController.killSwitch.value)
    }

    @Test
    fun verifyVpnConnectionLifecycle() {
        val server = VpnController.selectedServer.value
        VpnController.onVpnConnected(server.name, server.hostAddress)
        assertEquals(ConnectionState.CONNECTED, VpnController.connectionState.value)
        assertTrue(VpnController.virtualIp.value.isNotEmpty())

        VpnController.updateTelemetry(
            downSpeed = 68.5f,
            upSpeed = 22.1f,
            totalDown = 1024000L,
            totalUp = 512000L,
            duration = 15L
        )
        assertEquals(68.5f, VpnController.telemetry.value.downloadSpeedMbps, 0.01f)
        assertTrue(VpnController.telemetry.value.downloadSpeedKbs > 0f)
        assertTrue(VpnController.telemetry.value.formattedDownloadSpeed().contains("KB/s"))
        assertEquals(15L, VpnController.telemetry.value.sessionDurationSeconds)

        VpnController.onVpnDisconnected()
        assertEquals(ConnectionState.DISCONNECTED, VpnController.connectionState.value)
    }
}
