package dev.anilbeesetti.nextplayer.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.anilbeesetti.nextplayer.R
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun EmberSplashScreen(
    isVisible: Boolean,
    onSplashFinished: () -> Unit = {},
) {
    AnimatedVisibility(
        visible = isVisible,
        exit = fadeOut(animationSpec = tween(600, easing = FastOutSlowInEasing)),
    ) {
        val logoScale = remember { Animatable(0.4f) }
        val logoAlpha = remember { Animatable(0f) }
        val textAlpha = remember { Animatable(0f) }

        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val glowScale by infiniteTransition.animateFloat(
            initialValue = 0.85f,
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(2400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "glowScale",
        )

        val shimmerOffset by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1800, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
            label = "shimmer",
        )

        LaunchedEffect(Unit) {
            logoAlpha.animateTo(1f, animationSpec = tween(500))
            logoScale.animateTo(1.0f, animationSpec = tween(700, easing = FastOutSlowInEasing))
            delay(200)
            textAlpha.animateTo(1f, animationSpec = tween(600))
            delay(1200)
            onSplashFinished()
        }

        val emberParticles = remember {
            List(25) {
                EmberParticle(
                    xFrac = Random.nextFloat(),
                    yFrac = Random.nextFloat(),
                    radius = Random.nextFloat() * 4f + 2f,
                    speed = Random.nextFloat() * 0.003f + 0.001f,
                    alpha = Random.nextFloat() * 0.7f + 0.3f,
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A0B0E.toInt())),
            contentAlignment = Alignment.Center,
        ) {
            // Particle & Ember Glow Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val center = Offset(canvasWidth / 2f, canvasHeight / 2f)

                // Background Radial Ember Pulse
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x44FF5722.toInt()),
                            Color(0x22D84315.toInt()),
                            Color(0x00000000),
                        ),
                        center = center,
                        radius = 450f * glowScale,
                    ),
                    center = center,
                    radius = 450f * glowScale,
                )

                // Floating Ember Spark Particles
                emberParticles.forEach { particle ->
                    val px = particle.xFrac * canvasWidth
                    val py = (particle.yFrac - shimmerOffset * particle.speed * 200f).let {
                        if (it < 0) it + 1f else it
                    } * canvasHeight

                    drawCircle(
                        color = Color(0xFFFF9800.toInt()).copy(alpha = particle.alpha * 0.8f),
                        radius = particle.radius,
                        center = Offset(px, py),
                    )
                }
            }

            // Main Branding Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(24.dp),
            ) {
                // Logo Icon with animated scale & glow
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.scale(logoScale.value),
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_ember_splash_logo),
                        contentDescription = "Ember Logo",
                        modifier = Modifier
                            .size(120.dp)
                            .alpha(logoAlpha.value),
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Brand Name "E M B E R"
                Text(
                    text = "EMBER",
                    style = TextStyle(
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 8.sp,
                        color = Color(0xFFFFB300.toInt()),
                        shadow = Shadow(
                            color = Color(0xFFFF5722.toInt()),
                            blurRadius = 24f,
                        ),
                    ),
                    modifier = Modifier.alpha(textAlpha.value),
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Subtitle Tagline
                Text(
                    text = "ULTRA HD MEDIA PLAYER",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 4.sp,
                        color = Color(0xB3FFFFFF.toInt()),
                    ),
                    modifier = Modifier.alpha(textAlpha.value),
                )

                Spacer(modifier = Modifier.height(36.dp))

                // Premium Shimmer Loading Line
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0x33FFFFFF))
                        .alpha(textAlpha.value),
                ) {
                    val startPos = (shimmerOffset * 2f - 0.5f).coerceIn(0f, 1f)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0x00FF5722),
                                        Color(0xFFFFD700.toInt()),
                                        Color(0x00FF5722),
                                    ),
                                    startX = startPos * 400f,
                                    endX = (startPos + 0.5f) * 400f,
                                ),
                            ),
                    )
                }
            }
        }
    }
}

private data class EmberParticle(
    val xFrac: Float,
    val yFrac: Float,
    val radius: Float,
    val speed: Float,
    val alpha: Float,
)
