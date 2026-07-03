package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.data.ChatMessage
import com.example.data.CircleAlbumPhoto
import com.example.data.CircleMessage
import com.example.data.PrivateCircle
import com.example.ui.MainViewModel
import com.example.ui.components.GlassyCard
import com.example.ui.components.GlassyButton
import com.example.ui.components.GlassyInput
import com.example.ui.components.GlowDivider
import com.example.ui.theme.*

@Composable
fun VybChatScreen(viewModel: MainViewModel) {
    var subSection by remember { mutableStateOf("direct") } // "direct", "circles"
    val progress by viewModel.userProgress.collectAsState()

    // Dialog state
    var isCreateCircleOpen by remember { mutableStateOf(false) }
    var isJoinCircleOpen by remember { mutableStateOf(false) }
    val activeChatUser by viewModel.activeChatUser.collectAsState()
    val activeCircleId by viewModel.activeCircleId.collectAsState()
    val currentCircleId = activeCircleId

    Column(modifier = Modifier.fillMaxSize().background(CosmicBlack)) {
        
        // Top Sub-tab switcher
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
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (subSection == "direct") ElectricBlue else Color.Transparent)
                        .clickable { subSection = "direct"; viewModel.setActiveCircle(null) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Direct Chats", color = if (subSection == "direct") Color.White else TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (subSection == "circles") ElectricBlue else Color.Transparent)
                        .clickable { subSection = "circles" }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Private Circles 🔒", color = if (subSection == "circles") Color.White else TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (subSection == "circles" && currentCircleId == null) {
                IconButton(onClick = { isCreateCircleOpen = true }) {
                    Icon(Icons.Default.GroupAdd, contentDescription = "Add Circle", tint = ElectricCyan, modifier = Modifier.size(26.dp))
                }
            }
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (subSection == "direct") {
                DirectChatsHub(viewModel)
            } else {
                PrivateCirclesHub(
                    viewModel = viewModel,
                    onCreateClick = { isCreateCircleOpen = true },
                    onJoinClick = { isJoinCircleOpen = true }
                )
            }
        }
    }

    // Modal Create Circle Dialog
    if (isCreateCircleOpen) {
        var circleName by remember { mutableStateOf("") }
        var circleDesc by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { isCreateCircleOpen = false },
            title = { Text("Form Private Circle", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Private circles are secure, invite-only environments. You will receive a unique 6-digit join key.", color = TextSecondary, fontSize = 12.sp)
                    GlassyInput(value = circleName, onValueChange = { circleName = it }, placeholder = "Circle Name")
                    GlassyInput(value = circleDesc, onValueChange = { circleDesc = it }, placeholder = "Description")
                }
            },
            confirmButton = {
                GlassyButton(
                    text = "Generate",
                    onClick = {
                        if (circleName.isNotEmpty()) {
                            viewModel.createCircle(circleName, circleDesc)
                            isCreateCircleOpen = false
                        }
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { isCreateCircleOpen = false }) {
                    Text("Cancel", color = RoseRed)
                }
            },
            containerColor = DarkSurface
        )
    }

    // Modal Join Circle Dialog
    if (isJoinCircleOpen) {
        var joinKey by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { isJoinCircleOpen = false },
            title = { Text("Enter Invite Code", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Enter the 6-digit code shared by the circle's creator to unlock entry.", color = TextSecondary, fontSize = 12.sp)
                    GlassyInput(value = joinKey, onValueChange = { joinKey = it }, placeholder = "e.g. 240581")
                }
            },
            confirmButton = {
                GlassyButton(
                    text = "Verify",
                    onClick = {
                        if (joinKey.length == 6) {
                            viewModel.earnXp(30)
                            isJoinCircleOpen = false
                        }
                    }
                )
            },
            containerColor = DarkSurface
        )
    }
}

@Composable
fun DirectChatsHub(viewModel: MainViewModel) {
    val messages by viewModel.directMessages.collectAsState()
    val activeUser by viewModel.activeChatUser.collectAsState()
    var isVanishModeActive by remember { mutableStateOf(false) }
    var chatInput by remember { mutableStateOf("") }

    val contacts = listOf(
        Triple("dj_nebula", "DJ Nebula", "https://images.unsplash.com/photo-1534528741775-53994a69daeb"),
        Triple("zara_design", "Zara.Art", "https://images.unsplash.com/photo-1517841905240-472988babdf9"),
        Triple("kae_hawk", "Kaelen Hawk", "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6")
    )

    Row(modifier = Modifier.fillMaxSize()) {
        
        // Contacts sidebar (Apple simplicity narrow grid)
        Column(
            modifier = Modifier
                .width(80.dp)
                .fillMaxHeight()
                .background(Color(0xFF0C0E1A))
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            contacts.forEach { contact ->
                val isSelected = activeUser == contact.first
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) ElectricBlue else Color.Transparent)
                        .padding(3.dp)
                        .clickable { viewModel.setActiveChatUser(contact.first) }
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(contact.third),
                        contentDescription = contact.second,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }
            }
        }

        // Active Conversation Panel
        val currentContact = contacts.find { it.first == activeUser } ?: contacts.first()
        val bgBrush = if (isVanishModeActive) {
            Brush.verticalGradient(listOf(Color(0xFF1F0505), CosmicBlack))
        } else {
            Brush.verticalGradient(listOf(Color(0xFF0F1224), CosmicBlack))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(bgBrush)
        ) {
            // Room Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(currentContact.second, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(EmeraldGreen))
                    }
                    Text("🔒 E2EE Secured Chat", color = ElectricCyan.copy(alpha = 0.8f), fontSize = 10.sp)
                }

                // Vanish Mode Switch
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Vanish", color = if (isVanishModeActive) RoseRed else TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = isVanishModeActive,
                        onCheckedChange = {
                            isVanishModeActive = it
                            if (!it) {
                                viewModel.clearVanishMessages()
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = RoseRed,
                            checkedTrackColor = RoseRed.copy(alpha = 0.3f),
                            uncheckedThumbColor = TextSecondary,
                            uncheckedTrackColor = Color(0xFF1E2235)
                        )
                    )
                }
            }

            GlowDivider(color = if (isVanishModeActive) RoseRed else ElectricCyan)

            // Message History Scroll
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { msg ->
                    val isSelf = msg.senderUsername == "cyber_vyber"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isSelf) Arrangement.End else Arrangement.Start
                    ) {
                        Column(
                            horizontalAlignment = if (isSelf) Alignment.End else Alignment.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 16.dp,
                                            topEnd = 16.dp,
                                            bottomStart = if (isSelf) 16.dp else 4.dp,
                                            bottomEnd = if (isSelf) 4.dp else 16.dp
                                        )
                                    )
                                    .background(
                                        if (isSelf) {
                                            if (msg.isVanishMode) Brush.linearGradient(listOf(RoseRed, Color(0xFF880E4F)))
                                            else Brush.linearGradient(listOf(ElectricBlue, ElectricCyan))
                                        } else {
                                            Brush.linearGradient(listOf(Color(0xFF1F223D), Color(0xFF121426)))
                                        }
                                    )
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                if (msg.isVoiceMessage) {
                                    // Simulated audio message waveform animation
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = "Play voice", tint = Color.White, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        // Sound bars
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            listOf(16, 24, 8, 32, 12, 20, 14, 28).forEach { ht ->
                                                Box(
                                                    modifier = Modifier
                                                        .size(3.dp, ht.dp)
                                                        .clip(RoundedCornerShape(1.5.dp))
                                                        .background(Color.White)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("${msg.voiceDurationSec}s", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    Text(
                                        text = msg.text,
                                        color = Color.White,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (msg.isVanishMode) "⏱️ Disappearing story" else "12:04 PM",
                                    color = TextSecondary,
                                    fontSize = 9.sp
                                )
                                if (isSelf) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.DoneAll,
                                        contentDescription = "Read",
                                        tint = if (msg.isRead) ElectricCyan else TextSecondary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Input Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Micro record button (voice note simulation)
                IconButton(
                    onClick = {
                        viewModel.sendDirectMessage("Voice Message", isVanish = isVanishModeActive, isVoice = true, voiceDur = (3..8).random())
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E2235))
                ) {
                    Icon(Icons.Default.Mic, contentDescription = "Record voice", tint = ElectricCyan)
                }

                GlassyInput(
                    value = chatInput,
                    onValueChange = { chatInput = it },
                    placeholder = if (isVanishModeActive) "Send vanish message..." else "Type message...",
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (chatInput.isNotEmpty()) {
                                    viewModel.sendDirectMessage(chatInput, isVanish = isVanishModeActive)
                                    chatInput = ""
                                }
                            }
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = if (isVanishModeActive) RoseRed else ElectricCyan)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun PrivateCirclesHub(
    viewModel: MainViewModel,
    onCreateClick: () -> Unit,
    onJoinClick: () -> Unit
) {
    val circles by viewModel.circles.collectAsState()
    val activeCircleId by viewModel.activeCircleId.collectAsState()
    val currentCircleId = activeCircleId

    if (currentCircleId == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                
                // Join code trigger card
                GlassyCard(borderColor = ElectricCyan) {
                    Column {
                        Text("Connect via Invite Key", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Instantly jump into exclusive hubs and shared content folders with security keys.", color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            GlassyButton(text = "Join Circle", onClick = onJoinClick, modifier = Modifier.weight(1f))
                            GlassyButton(
                                text = "Create",
                                onClick = onCreateClick,
                                colors = listOf(NeonPurple, Color(0xFFE040FB)),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Text("Your Active Circles", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)

                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(circles) { circle ->
                        GlassyCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setActiveCircle(circle.id) },
                            borderColor = ElectricBlue.copy(alpha = 0.3f)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(circle.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(circle.description, color = TextSecondary, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("🔑 Key: ${circle.inviteCode}", color = ElectricCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF151930))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("👥 ${circle.memberCount} members", color = Color.White, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        CircleWorkspace(viewModel = viewModel, circleId = currentCircleId)
    }
}

@Composable
fun CircleWorkspace(viewModel: MainViewModel, circleId: String) {
    val circles by viewModel.circles.collectAsState()
    val circle = circles.find { it.id == circleId } ?: return
    var innerTab by remember { mutableStateOf("chat") } // "chat", "album", "voice"

    val messages by viewModel.activeCircleMessages.collectAsState()
    val photos by viewModel.activeCirclePhotos.collectAsState()
    var groupInput by remember { mutableStateOf("") }

    var isVoiceRoomJoined by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF090A14))) {
        
        // Workspace Subheader
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.setActiveCircle(null) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(circle.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Private Group Circle", color = ElectricCyan, fontSize = 11.sp)
                }
            }

            // Tabs bar
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF151930))
                    .padding(2.dp)
            ) {
                val tabs = listOf("chat" to "💬 Chat", "album" to "📸 Album", "voice" to "🎙️ Voice")
                tabs.forEach { tb ->
                    val isSelected = innerTab == tb.first
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) ElectricBlue else Color.Transparent)
                            .clickable { innerTab = tb.first }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(tb.second, color = if (isSelected) Color.White else TextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }

        GlowDivider(color = ElectricBlue)

        // Tab Content Panel
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (innerTab) {
                "chat" -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(messages) { msg ->
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Brush.radialGradient(listOf(ElectricCyan, NeonPurple))),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(msg.senderName.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(msg.senderName, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(msg.messageText, color = TextPrimary, fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                        // Input bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GlassyInput(
                                value = groupInput,
                                onValueChange = { groupInput = it },
                                placeholder = "Group message...",
                                modifier = Modifier.weight(1f),
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            if (groupInput.isNotEmpty()) {
                                                viewModel.sendCircleMsg(groupInput)
                                                groupInput = ""
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Default.Send, contentDescription = "Send", tint = ElectricCyan)
                                    }
                                }
                            )
                        }
                    }
                }

                "album" -> {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Circle Shared Album", color = Color.White, fontWeight = FontWeight.Bold)
                            GlassyButton(
                                text = "Upload Photo",
                                onClick = {
                                    viewModel.uploadCirclePhoto("https://images.unsplash.com/photo-1516259762381-22954d7d3ad2")
                                }
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))

                        if (photos.isEmpty()) {
                            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Text("No shared media yet. Keep your circle updated!", color = TextSecondary, fontSize = 12.sp)
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                items(photos) { ph ->
                                    Box(
                                        modifier = Modifier
                                            .aspectRatio(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFF1E2235))
                                    ) {
                                        Image(
                                            painter = rememberAsyncImagePainter(ph.photoUrl),
                                            contentDescription = "Shared image",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .align(Alignment.BottomCenter)
                                                .background(Color.Black.copy(alpha = 0.5f))
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = ph.uploadedBy,
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "voice" -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (!isVoiceRoomJoined) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.SettingsVoice, contentDescription = "Voice Active", tint = ElectricCyan, modifier = Modifier.size(64.dp))
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Voice Room is Active", color = Color.White, fontWeight = FontWeight.Bold)
                                Text("Join other circle members in live audio space.", color = TextSecondary, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(20.dp))
                                GlassyButton(text = "Join Room", onClick = { isVoiceRoomJoined = true })
                            }
                        } else {
                            // Pulsating Speaking Circle
                            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                            val pulseScale by infiniteTransition.animateFloat(
                                initialValue = 1f,
                                targetValue = 1.3f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(1000, easing = LinearEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "sc"
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(120.dp)
                                            .rotate(pulseScale * 360f) // correct rotation angle float
                                            .border(4.dp, Brush.radialGradient(listOf(ElectricCyan, NeonPurple)), CircleShape)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(90.dp)
                                            .clip(CircleShape)
                                            .background(Brush.radialGradient(listOf(ElectricBlue, CosmicBlack))),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Speaking...", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }

                                Text("Active Voice roundtable", color = Color.White, fontWeight = FontWeight.Bold)
                                Text("Zara.Art, DJ Nebula, and Alex inside", color = TextSecondary, fontSize = 12.sp)
                                
                                Spacer(modifier = Modifier.height(30.dp))
                                
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    IconButton(
                                        onClick = {},
                                        modifier = Modifier.size(54.dp).clip(CircleShape).background(Color(0xFF1F223D))
                                    ) {
                                        Icon(Icons.Default.Mic, contentDescription = "Microphone", tint = Color.White)
                                    }
                                    IconButton(
                                        onClick = { isVoiceRoomJoined = false },
                                        modifier = Modifier.size(54.dp).clip(CircleShape).background(RoseRed)
                                    ) {
                                        Icon(Icons.Default.CallEnd, contentDescription = "End Call", tint = Color.White)
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
