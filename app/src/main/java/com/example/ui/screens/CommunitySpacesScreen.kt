package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.data.Community
import com.example.data.CommunityChannel
import com.example.data.CommunityMessage
import com.example.ui.MainViewModel
import com.example.ui.components.GlassyCard
import com.example.ui.components.GlassyButton
import com.example.ui.components.GlassyInput
import com.example.ui.components.GlowDivider
import com.example.ui.theme.*

@Composable
fun CommunitySpacesScreen(viewModel: MainViewModel) {
    val communities by viewModel.communities.collectAsState()
    val activeCommunityId by viewModel.activeCommunityId.collectAsState()
    val channels by viewModel.communityChannels.collectAsState()
    val activeChannelId by viewModel.activeChannelId.collectAsState()
    val messages by viewModel.channelMessages.collectAsState()

    val context = LocalContext.current
    var subTab by remember { mutableStateOf("channels") } // "channels", "events", "achievements"
    var textInput by remember { mutableStateOf("") }
    var rsvpedEvents by remember { mutableStateOf(setOf<String>()) }

    val activeCommunity = communities.find { it.id == activeCommunityId } ?: communities.firstOrNull()

    Row(modifier = Modifier.fillMaxSize().background(CosmicBlack)) {
        
        // Left Column: Communities Icons list (Discord style)
        Column(
            modifier = Modifier
                .width(72.dp)
                .fillMaxHeight()
                .background(Color(0xFF090B15))
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Communities Selections
            communities.forEach { comm ->
                val isSelected = comm.id == activeCommunityId
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(if (isSelected) RoundedCornerShape(12.dp) else CircleShape)
                        .background(if (isSelected) ElectricBlue else Color(0xFF1B1D2D))
                        .clickable { viewModel.selectCommunity(comm.id) }
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = comm.iconUrl.ifEmpty { comm.name.take(1) },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Create Community Action
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E2235))
                    .clickable {
                        viewModel.createCommunity("Cyber Runners", "🎌")
                        Toast.makeText(context, "Community created!", Toast.LENGTH_SHORT).show()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Space", tint = ElectricCyan, modifier = Modifier.size(22.dp))
            }
        }

        // Right Column: Active Community Space
        if (activeCommunity != null) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color(0xFF0F1122))
            ) {
                // Header Details
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = activeCommunity.name,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "👥 ${activeCommunity.memberCount} members • 🏆 Level ${activeCommunity.rankingPoints / 100}",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                        
                        // Workspace Sub Tabs selector
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1F223D))
                                .padding(2.dp)
                        ) {
                            listOf("channels" to "Channels", "events" to "Events", "achievements" to "Milestones").forEach { tab ->
                                val isSel = subTab == tab.first
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSel) ElectricBlue else Color.Transparent)
                                        .clickable { subTab = tab.first }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(tab.second, color = if (isSel) Color.White else TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                GlowDivider(color = ElectricBlue)

                // Sub tab Content panels
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    when (subTab) {
                        "channels" -> {
                            Row(modifier = Modifier.fillMaxSize()) {
                                // Channels list sidebar
                                Column(
                                    modifier = Modifier
                                        .width(130.dp)
                                        .fillMaxHeight()
                                        .background(Color(0xFF0C0D1B))
                                        .padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        "TEXT CHANNELS",
                                        color = TextSecondary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                    
                                    channels.filter { it.type == "TEXT" }.forEach { chan ->
                                        val isSelected = chan.id == activeChannelId
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(if (isSelected) Color(0x1F0070FF) else Color.Transparent)
                                                .clickable { viewModel.selectChannel(chan.id) }
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("#", color = if (isSelected) ElectricCyan else TextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = chan.name,
                                                color = if (isSelected) Color.White else TextSecondary,
                                                fontSize = 12.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        "VOICE CHANNELS",
                                        color = TextSecondary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )

                                    channels.filter { it.type == "VOICE" }.forEach { chan ->
                                        val isSelected = chan.id == activeChannelId
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(if (isSelected) Color(0x1F00F3FF) else Color.Transparent)
                                                .clickable { viewModel.selectChannel(chan.id) }
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.VolumeUp,
                                                contentDescription = "Voice channel",
                                                tint = if (isSelected) ElectricCyan else TextSecondary,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = chan.name,
                                                color = if (isSelected) Color.White else TextSecondary,
                                                fontSize = 12.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }

                                // Interactive Chat Feed/Voice rounded roundtable panel
                                val selectedChannel = channels.find { it.id == activeChannelId } ?: channels.firstOrNull()
                                if (selectedChannel != null) {
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .background(Color(0xFF0F1122))
                                    ) {
                                        if (selectedChannel.type == "TEXT") {
                                            // Text Message log
                                            LazyColumn(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .fillMaxWidth()
                                                    .padding(12.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                items(messages) { msg ->
                                                    Row(modifier = Modifier.fillMaxWidth()) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(32.dp)
                                                                .clip(CircleShape)
                                                                .background(ElectricBlue),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text(msg.senderName.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                        }
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Column {
                                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                                Text(msg.senderName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                                if (msg.isVerified) {
                                                                    Spacer(modifier = Modifier.width(4.dp))
                                                                    Icon(Icons.Default.CheckCircle, tint = ElectricCyan, contentDescription = "Verified", modifier = Modifier.size(12.dp))
                                                                }
                                                                Spacer(modifier = Modifier.width(6.dp))
                                                                Text("12:15 PM", color = TextSecondary, fontSize = 8.sp)
                                                            }
                                                            Text(msg.text, color = TextPrimary, fontSize = 12.sp)
                                                        }
                                                    }
                                                }
                                            }

                                            // Text Chat input
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                GlassyInput(
                                                    value = textInput,
                                                    onValueChange = { textInput = it },
                                                    placeholder = "Send to #${selectedChannel.name}...",
                                                    modifier = Modifier.weight(1f),
                                                    trailingIcon = {
                                                        IconButton(
                                                            onClick = {
                                                                if (textInput.isNotEmpty()) {
                                                                    viewModel.sendCommunityMessage(textInput)
                                                                    textInput = ""
                                                                }
                                                            }
                                                        ) {
                                                            Icon(Icons.Default.Send, contentDescription = "Send message", tint = ElectricCyan)
                                                        }
                                                    }
                                                )
                                            }
                                        } else {
                                            // Voice channel simulator panel
                                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Icon(Icons.Default.SettingsVoice, contentDescription = "Voice connection symbol", tint = ElectricCyan, modifier = Modifier.size(48.dp))
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Text("Connected to Space Voice channel", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                    Text("🔊 Join #${selectedChannel.name}", color = TextSecondary, fontSize = 11.sp)
                                                    Spacer(modifier = Modifier.height(16.dp))
                                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(12.dp))
                                                                .background(RoseRed)
                                                                .clickable { viewModel.selectChannel(null) }
                                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                                        ) {
                                                            Text("Disconnect", color = Color.White, fontSize = 11.sp)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Box(modifier = Modifier.weight(1f).fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text("Select #channel to start vibe check", color = TextSecondary, fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                        "events" -> {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                val events = listOf(
                                    Triple("ev1", "DJ Nebula Cyber Live Concert", "Today, 9:00 PM (EST) • Live Stream Stage"),
                                    Triple("ev2", "Weekly Crypto Art Panel", "Tomorrow, 4:00 PM (EST) • Acoustic Labs"),
                                    Triple("ev3", "Community Fortnite Arena Challenge", "July 6, 2:00 PM (EST) • Text Channel Rules")
                                )

                                items(events) { ev ->
                                    val isRsvped = rsvpedEvents.contains(ev.first)
                                    GlassyCard(borderColor = ElectricCyan) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(ev.second, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(ev.third, color = TextSecondary, fontSize = 11.sp)
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(if (isRsvped) Color(0xFF1E2235) else ElectricBlue)
                                                    .clickable {
                                                        if (!isRsvped) {
                                                            rsvpedEvents = rsvpedEvents + ev.first
                                                            viewModel.earnXp(40) // Reward RSVP
                                                            Toast.makeText(context, "Registered! +40 XP awarded!", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Text(
                                                    text = if (isRsvped) "✓ Registered" else "RSVP +40XP",
                                                    color = Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        "achievements" -> {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                val achievements = listOf(
                                    Triple("Immaculate Vibe Master", "Awarded for completing 5 days streak of cyber activity.", 1.0f),
                                    Triple("Exclusive Circle Founder", "Awarded for inviting 3 contacts into Private Circles.", 0.6f),
                                    Triple("Gemini Copilot", "Ask AI Creator Studio to generate copy concepts 10 times.", 0.4f)
                                )

                                items(achievements) { ach ->
                                    GlassyCard(borderColor = NeonPurple) {
                                        Column {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(ach.first, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                    Text(ach.second, color = TextSecondary, fontSize = 11.sp)
                                                }
                                                Icon(
                                                    imageVector = Icons.Default.Stars,
                                                    contentDescription = "Badge",
                                                    tint = if (ach.third >= 1.0f) GoldYellow else TextSecondary,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(10.dp))
                                            LinearProgressIndicator(
                                                progress = { ach.third },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(4.dp)
                                                    .clip(RoundedCornerShape(2.dp)),
                                                color = NeonPurple,
                                                trackColor = Color(0xFF1E2235),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
