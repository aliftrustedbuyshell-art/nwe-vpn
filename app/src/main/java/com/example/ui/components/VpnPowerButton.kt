package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberAccentGlow
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberError
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberSecondary
import com.example.ui.theme.CyberSuccessGlow
import com.example.vpn.model.ConnectionState

@Composable
fun VpnPowerButton(
    connectionState: ConnectionState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (connectionState == ConnectionState.CONNECTING || connectionState == ConnectionState.CONNECTED) 1.15f else 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (connectionState == ConnectionState.CONNECTING) 750 else 1800,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val haloAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = if (connectionState == ConnectionState.CONNECTED) 0.6f else if (connectionState == ConnectionState.CONNECTING) 0.8f else 0.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "haloAlpha"
    )

    val activeColor by animateColorAsState(
        targetValue = when (connectionState) {
            ConnectionState.CONNECTED -> CyberSecondary
            ConnectionState.CONNECTING -> CyberPrimary
            ConnectionState.DISCONNECTING -> CyberError
            ConnectionState.DISCONNECTED -> Color(0xFF64748B)
        },
        animationSpec = tween(400),
        label = "buttonColor"
    )

    val glowColor = when (connectionState) {
        ConnectionState.CONNECTED -> CyberSuccessGlow
        ConnectionState.CONNECTING -> CyberAccentGlow
        else -> Color.Transparent
    }

    Box(
        modifier = modifier
            .size(200.dp)
            .testTag("vpn_power_button_container"),
        contentAlignment = Alignment.Center
    ) {
        // Outer pulsing energy ring
        if (connectionState == ConnectionState.CONNECTING || connectionState == ConnectionState.CONNECTED) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(glowColor.copy(alpha = haloAlpha))
            )
        }

        // Secondary boundary ring
        Box(
            modifier = Modifier
                .size(170.dp)
                .clip(CircleShape)
                .border(2.dp, activeColor.copy(alpha = 0.5f), CircleShape)
        )

        // Main Button Core
        Box(
            modifier = Modifier
                .size(144.dp)
                .shadow(
                    elevation = if (connectionState == ConnectionState.CONNECTED) 24.dp else 8.dp,
                    shape = CircleShape,
                    spotColor = activeColor,
                    ambientColor = activeColor
                )
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF1E293B),
                            Color(0xFF0F172A),
                            Color(0xFF0A0F1D)
                        )
                    )
                )
                .border(
                    width = 2.5.dp,
                    brush = Brush.sweepGradient(
                        listOf(
                            activeColor,
                            activeColor.copy(alpha = 0.3f),
                            activeColor
                        )
                    ),
                    shape = CircleShape
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true, color = activeColor),
                    onClick = onClick
                )
                .testTag("vpn_power_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PowerSettingsNew,
                contentDescription = when (connectionState) {
                    ConnectionState.CONNECTED -> "Disconnect VPN"
                    ConnectionState.CONNECTING -> "Connecting..."
                    ConnectionState.DISCONNECTING -> "Disconnecting..."
                    ConnectionState.DISCONNECTED -> "Connect VPN"
                },
                tint = activeColor,
                modifier = Modifier.size(68.dp)
            )
        }
    }
}
