package com.example.vpn.model

enum class ConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    DISCONNECTING
}

enum class ServerCategory(val label: String) {
    ALL("All Servers"),
    STREAMING("Streaming"),
    P2P("P2P / Torrent"),
    GAMING("Ultra-Low Ping")
}

data class VpnServer(
    val id: String,
    val name: String,
    val country: String,
    val city: String,
    val flagEmoji: String,
    val hostAddress: String,
    val port: Int = 443,
    val pingMs: Int,
    val loadPercent: Int,
    val speedCapacityGbps: Int = 10,
    val category: ServerCategory = ServerCategory.ALL,
    val isRamOnly: Boolean = true,
    val isAutoFastest: Boolean = false
)

enum class VpnProtocol(
    val title: String,
    val cipher: String,
    val handshake: String,
    val description: String,
    val isMilitaryGrade: Boolean = true
) {
    WIREGUARD(
        title = "WireGuard®",
        cipher = "ChaCha20-Poly1305 (256-bit)",
        handshake = "Curve25519 + Blake2s (PFS)",
        description = "Next-gen protocol offering supreme throughput, instant handshakes, and lightweight code efficiency."
    ),
    OPENVPN_UDP(
        title = "OpenVPN (UDP)",
        cipher = "AES-256-GCM (Military-Grade)",
        handshake = "RSA-4096 / DH-4096 Bit",
        description = "Industry gold standard for security and speed. Best for everyday high-speed streaming & gaming."
    ),
    OPENVPN_TCP(
        title = "OpenVPN (TCP)",
        cipher = "AES-256-CBC with SHA-512",
        handshake = "RSA-4096 / TLS 1.3",
        description = "Guaranteed packet delivery. Engineered to pierce through restrictive network firewalls & censorship."
    ),
    IKEV2(
        title = "IKEv2 / IPsec",
        cipher = "AES-256-GCM",
        handshake = "Diffie-Hellman Group 21",
        description = "Remarkable stability during mobile network switches between 5G, LTE, and Wi-Fi networks."
    )
}

enum class DnsProvider(
    val title: String,
    val primaryIp: String,
    val secondaryIp: String,
    val description: String
) {
    CLOUDFLARE(
        title = "Cloudflare 1.1.1.1",
        primaryIp = "1.1.1.1",
        secondaryIp = "1.0.0.1",
        description = "Ultra-fast DNS privacy with strict 24h purge policy"
    ),
    ADGUARD(
        title = "AdGuard DNS Shield",
        primaryIp = "94.140.14.14",
        secondaryIp = "94.140.15.15",
        description = "Blocks ad trackers, phishing, and malicious domains"
    ),
    QUAD9(
        title = "Quad9 Privacy",
        primaryIp = "9.9.9.9",
        secondaryIp = "149.112.112.112",
        description = "Non-profit security blocking malicious malware hostnames"
    ),
    GOOGLE(
        title = "Google Public DNS",
        primaryIp = "8.8.8.8",
        secondaryIp = "8.8.4.4",
        description = "Global high-availability redundant resolvers"
    )
}

data class NetworkTelemetry(
    val downloadSpeedMbps: Float = 0.0f,
    val uploadSpeedMbps: Float = 0.0f,
    val totalBytesDownloaded: Long = 0L,
    val totalBytesUploaded: Long = 0L,
    val sessionDurationSeconds: Long = 0L,
    val currentPingMs: Int = 18,
    val packetLossPercent: Float = 0.0f
) {
    // 1 Mbps = 128 KB/s
    val downloadSpeedKbs: Float get() = downloadSpeedMbps * 128f
    val uploadSpeedKbs: Float get() = uploadSpeedMbps * 128f

    fun formattedDownloadSpeed(): String = formatKbs(downloadSpeedKbs)
    fun formattedUploadSpeed(): String = formatKbs(uploadSpeedKbs)

    companion object {
        fun formatKbs(kbs: Float): String {
            return if (kbs <= 0f) {
                "0 KB/s"
            } else if (kbs < 1000f) {
                String.format(java.util.Locale.US, "%.0f KB/s", kbs)
            } else {
                String.format(java.util.Locale.US, "%.1f KB/s", kbs)
            }
        }
    }
}
