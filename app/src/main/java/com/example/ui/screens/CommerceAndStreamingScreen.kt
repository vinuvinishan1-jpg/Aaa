package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CreatorProduct
import com.example.ui.MainViewModel
import com.example.ui.components.GlassyCard
import com.example.ui.components.GlassyButton
import com.example.ui.components.GlassyInput
import com.example.ui.components.GlowDivider
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CommerceAndStreamingScreen(viewModel: MainViewModel) {
    var coreSubTab by remember { mutableStateOf("live") } // "live", "store", "dashboard"
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().background(CosmicBlack)) {
        
        // Monetize Tab Bar Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF16192E))
                    .padding(4.dp)
            ) {
                listOf("live" to "Live Stages 🎙️", "store" to "Creator Store 🛒", "dashboard" to "Earnings 📊").forEach { tab ->
                    val isSel = coreSubTab == tab.first
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSel) ElectricBlue else Color.Transparent)
                            .clickable { coreSubTab = tab.first }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(tab.second, color = if (isSel) Color.White else TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (coreSubTab) {
                "live" -> LiveStreamSimulator(viewModel)
                "store" -> MerchandiseStore(viewModel)
                "dashboard" -> EarningsDashboard(viewModel)
            }
        }
    }
}

@Composable
fun LiveStreamSimulator(viewModel: MainViewModel) {
    var isLiveActive by remember { mutableStateOf(false) }
    val viewerCount by viewModel.liveViewerCount.collectAsState()
    val flyingGifts by viewModel.liveGifts.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    if (!isLiveActive) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Active Live Stages", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            
            // Stage item card
            GlassyCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isLiveActive = true },
                borderColor = ElectricCyan
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(RoseRed)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("LIVE", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("DJ Nebula's Cyber Rave", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Interactive electronic mixes, flying digital rewards, and live music overlay challenges.", color = TextSecondary, fontSize = 11.sp)
                    }
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1B1D2D))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("👁️ $viewerCount", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Quick tips for live streams
            GlassyCard(borderColor = NeonPurple.copy(alpha = 0.3f)) {
                Column {
                    Text("Creator Subscriptions Enabled", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("VYBE X allows you to buy subscriber badges, send dynamic neon gifts, and compete in multiplayer screen sharing.", color = TextSecondary, fontSize = 11.sp)
                }
            }
        }
    } else {
        // Multi host live feed emulator
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            Column(modifier = Modifier.fillMaxSize()) {
                
                // Top Screen Host split (Apple Tesla level aesthetic)
                Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .border(1.dp, Color(0xFF1E2235))
                            .background(Brush.radialGradient(listOf(Color(0xFF0F1224), Color.Black))),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(modifier = Modifier.size(54.dp).clip(CircleShape).background(ElectricBlue), contentAlignment = Alignment.Center) {
                                Text("🎧", fontSize = 24.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("DJ Nebula", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Verified Host", color = ElectricCyan, fontSize = 10.sp)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .border(1.dp, Color(0xFF1E2235))
                            .background(Brush.radialGradient(listOf(Color(0xFF1F0520), Color.Black))),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(modifier = Modifier.size(54.dp).clip(CircleShape).background(NeonPurple), contentAlignment = Alignment.Center) {
                                Text("🎤", fontSize = 24.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Guest_MC", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Speaking", color = NeonPurple, fontSize = 10.sp)
                        }
                    }
                }

                // Interactive Overlay Controller Card at Bottom
                GlassyCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    borderColor = ElectricCyan
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(RoseRed))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Audience Stage Live", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("👁️ $viewerCount viewing", color = TextSecondary, fontSize = 11.sp)
                            }

                            IconButton(onClick = { isLiveActive = false; viewModel.clearLiveGifts() }) {
                                Icon(Icons.Default.ExitToApp, contentDescription = "Exit Live Stream", tint = RoseRed)
                            }
                        }

                        // Simulated Gift Sender
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val gifts = listOf(
                                Pair("❤️ Heart", 1.99),
                                Pair("💎 Spark", 4.99),
                                Pair("👑 Trophy", 9.99)
                            )
                            
                            gifts.forEach { gift ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF1E2235))
                                        .border(1.dp, ElectricCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                        .clickable {
                                            viewModel.sendGiftToLiveStream(gift.first, gift.second)
                                            Toast.makeText(context, "${gift.first} sent! Streamer rewarded!", Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(gift.first, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text("$${gift.second}", fontSize = 10.sp, color = ElectricCyan)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Flying Interactive Gifts Animation Area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 120.dp, end = 24.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    flyingGifts.takeLast(4).forEach { gift ->
                        AnimatedFlyingGiftItem(giftText = gift)
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedFlyingGiftItem(giftText: String) {
    val transition = rememberInfiniteTransition(label = "flying_gift")
    val translationY by transition.animateFloat(
        initialValue = 0f,
        targetValue = -300f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseOutQuad),
            repeatMode = RepeatMode.Restart
        ),
        label = "y"
    )
    val alpha by transition.animateFloat(
        initialValue = 1.0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseOutQuad),
            repeatMode = RepeatMode.Restart
        ),
        label = "a"
    )

    Box(
        modifier = Modifier
            .offset(y = translationY.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xCC111424))
            .border(1.dp, ElectricCyan, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text("✨ Received: $giftText", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun MerchandiseStore(viewModel: MainViewModel) {
    val products by viewModel.products.collectAsState()
    var selectedProductForCheckout by remember { mutableStateOf<CreatorProduct?>(null) }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Trending Creator Stores", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(products) { prod ->
                    GlassyCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedProductForCheckout = prod },
                        borderColor = ElectricBlue.copy(alpha = 0.3f)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF151930)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(prod.imageUrl, fontSize = 48.sp)
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(prod.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("By ${prod.creatorName}", color = TextSecondary, fontSize = 11.sp)
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("$${prod.price}", color = ElectricCyan, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(ElectricBlue)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Buy", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Mock One-Click Checkout Bottom Panel overlay
        selectedProductForCheckout?.let { prod ->
            CheckoutDrawer(
                product = prod,
                onDismiss = { selectedProductForCheckout = null },
                onCheckoutSuccess = {
                    viewModel.purchaseProduct(prod.id, prod.price)
                    selectedProductForCheckout = null
                    Toast.makeText(context, "Purchase Securely Completed! Creator Account Credited.", Toast.LENGTH_LONG).show()
                }
            )
        }
    }
}

@Composable
fun CheckoutDrawer(
    product: CreatorProduct,
    onDismiss: () -> Unit,
    onCheckoutSuccess: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onDismiss() }
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(DarkSurface)
                .clickable(enabled = false) {}
                .padding(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🔒 One-Click Checkout", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close Checkout", tint = Color.White)
                    }
                }

                GlowDivider(color = ElectricCyan)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1E2235)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(product.imageUrl, fontSize = 28.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(product.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Digital Creator Item • Secure delivery", color = TextSecondary, fontSize = 11.sp)
                    }
                }

                Text(product.description, color = TextPrimary, fontSize = 12.sp, lineHeight = 16.sp)

                // Cost summary
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF151726))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal", color = TextSecondary, fontSize = 12.sp)
                        Text("$${product.price}", color = Color.White, fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Platform Tech Fee (10%)", color = TextSecondary, fontSize = 12.sp)
                        Text("$${String.format("%.2f", product.price * 0.1)}", color = Color.White, fontSize = 12.sp)
                    }
                    GlowDivider(color = Color(0xFF1E2235))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Bill (E2EE Covered)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("$${String.format("%.2f", product.price * 1.1)}", color = ElectricCyan, fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }
                }

                GlassyButton(
                    text = "Authorize & Pay Securely",
                    onClick = onCheckoutSuccess,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun EarningsDashboard(viewModel: MainViewModel) {
    val progress by viewModel.userProgress.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        
        // Stats Card
        GlassyCard(borderColor = NeonPurple) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Verified Creator Dashboard", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Default.Verified, contentDescription = "Verified Partner Symbol", tint = ElectricCyan, modifier = Modifier.size(16.dp))
                }
                Text("Account Level Status: Premium Partner", color = TextSecondary, fontSize = 12.sp)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Total Earnings", color = TextSecondary, fontSize = 11.sp)
                        Text("$${String.format("%.2f", progress.totalRevenue)}", color = ElectricCyan, fontWeight = FontWeight.Black, fontSize = 20.sp)
                    }
                    Column {
                        Text("Subscribers Count", color = TextSecondary, fontSize = 11.sp)
                        Text("1,420 fans", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
            }
        }

        // Mock chart
        GlassyCard(borderColor = ElectricBlue.copy(alpha = 0.3f)) {
            Column {
                Text("Revenue reports (Past 7 days)", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(12.dp))
                
                // Draw mock chart bars with spacers
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    val days = listOf("Mon" to 40, "Tue" to 90, "Wed" to 50, "Thu" to 110, "Fri" to 80, "Sat" to 140, "Sun" to 100)
                    days.forEach { day ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .fillMaxHeight(day.second / 150f)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(ElectricCyan, ElectricBlue)
                                        )
                                    )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(day.first, color = TextSecondary, fontSize = 9.sp)
                        }
                    }
                }
            }
        }
        
        // Subscription Tier manager
        GlassyCard(borderColor = ElectricBlue.copy(alpha = 0.2f)) {
            Column {
                Text("Active Subscription Tiers", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tier 1: VIP Backstage Pass", color = TextPrimary, fontSize = 12.sp)
                    Text("$4.99/mo", color = ElectricCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tier 2: Ultra Fan Circle Access", color = TextPrimary, fontSize = 12.sp)
                    Text("$14.99/mo", color = ElectricCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}
