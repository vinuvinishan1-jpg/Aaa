package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.data.FeedPost
import com.example.data.Story
import com.example.ui.MainViewModel
import com.example.ui.components.GlassyCard
import com.example.ui.components.GlassyButton
import com.example.ui.components.GlassyInput
import com.example.ui.theme.*

@Composable
fun SmartFeedScreen(viewModel: MainViewModel) {
    val posts by viewModel.feedPosts.collectAsState()
    val stories by viewModel.stories.collectAsState()
    val category by viewModel.feedCategory.collectAsState()
    
    var selectedStoryForViewer by remember { mutableStateOf<Story?>(null) }
    var isCommentsOpenForPost by remember { mutableStateOf<FeedPost?>(null) }
    var currentInterestSelected by remember { mutableStateOf("All") }

    Box(modifier = Modifier.fillMaxSize().background(CosmicBlack)) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // Top Navigation / Brand Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "VYBE X",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                
                // Categories
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF16192E))
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                ) {
                    val categories = listOf("For You", "Trending", "Global")
                    categories.forEach { cat ->
                        val isSel = category == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSel) ElectricBlue else Color.Transparent)
                                .clickable { viewModel.setFeedCategory(cat) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat,
                                color = if (isSel) Color.White else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Top Disappearing Stories Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Add your own story
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            // Quick Add simulation
                            viewModel.addStory(
                                Story(
                                    creatorName = "Alex Rivera",
                                    creatorAvatar = "",
                                    mediaUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2",
                                    stickerType = "POLL",
                                    stickerQuestion = "Is Jetpack Compose awesome?",
                                    stickerOptions = "Yes 💎,Absolutely 🚀"
                                )
                            )
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E2235)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                tint = ElectricCyan,
                                contentDescription = "Add Story",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("My Story", color = TextSecondary, fontSize = 11.sp)
                    }
                }

                items(stories) { story ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { selectedStoryForViewer = story }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .border(2.dp, Brush.sweepGradient(listOf(ElectricBlue, ElectricCyan, NeonPurple)), CircleShape)
                                .padding(3.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E2235))
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(story.creatorAvatar.ifEmpty { "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde" }),
                                contentDescription = story.creatorName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = story.creatorName,
                            color = TextPrimary,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.width(64.dp)
                        )
                    }
                }
            }

            // Discovery Interest Graph tags
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val tags = listOf("All", "⚡ Metaverse", "🎵 LoFi", "🎨 UI Design", "🎌 Tokyo", "📈 Crypto")
                items(tags) { tag ->
                    val isSel = currentInterestSelected == tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSel) Color(0x3300F3FF) else Color(0xFF111424))
                            .border(1.dp, if (isSel) ElectricCyan else Color.Transparent, RoundedCornerShape(12.dp))
                            .clickable { currentInterestSelected = tag }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(tag, color = if (isSel) ElectricCyan else TextSecondary, fontSize = 12.sp)
                    }
                }
            }

            // TikTok Feed LazyColumn
            if (posts.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Setting up the Cyber vybes...", color = TextSecondary, fontSize = 14.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(posts) { post ->
                        FeedItemCard(
                            post = post,
                            onLike = { viewModel.likePost(post.id) },
                            onFollow = { viewModel.followCreator(post.id) },
                            onCommentsOpen = { isCommentsOpenForPost = post }
                        )
                    }
                }
            }
        }

        // Story Viewer Dialog overlay
        selectedStoryForViewer?.let { story ->
            StoryViewerPopup(
                story = story,
                onDismiss = { selectedStoryForViewer = null },
                onVote = { opt -> viewModel.votePoll(story.id, opt) },
                onAnswer = { ans -> viewModel.answerStoryQuestion(story.id, ans) }
            )
        }

        // Comments Drawer overlay
        isCommentsOpenForPost?.let { post ->
            CommentsDrawer(
                post = post,
                onDismiss = { isCommentsOpenForPost = null },
                onSendComment = { txt ->
                    viewModel.earnXp(15) // Reward commenting
                    isCommentsOpenForPost = null
                }
            )
        }
    }
}

@Composable
fun FeedItemCard(
    post: FeedPost,
    onLike: () -> Unit,
    onFollow: () -> Unit,
    onCommentsOpen: () -> Unit
) {
    // Rotation animation for sound track
    val infiniteTransition = rememberInfiniteTransition(label = "sound_rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rot"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF10121F))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            
            // Background Artwork (Cosmic neon abstract representation)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF0F2027),
                                Color(0xFF203A43),
                                Color(0xFF2C5364)
                            )
                        )
                    )
            ) {
                // Neon glow accents on card
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawBehind {
                            drawCircle(
                                brush = Brush.radialGradient(listOf(ElectricCyan.copy(alpha = 0.15f), Color.Transparent)),
                                radius = size.minDimension * 0.8f,
                                center = this.center
                            )
                        }
                )
                
                // Show standard content placeholder representation
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        tint = ElectricCyan.copy(alpha = 0.4f),
                        contentDescription = "Video preview",
                        modifier = Modifier.size(64.dp)
                    )
                }
            }

            // Glassy overlay at bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                        )
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "@${post.creatorUsername}",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (post.isVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    tint = ElectricCyan,
                                    contentDescription = "Verified",
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            // Follow button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (post.isFollowing) Color(0xFF1E2235) else ElectricBlue)
                                    .clickable { onFollow() }
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (post.isFollowing) "Following" else "+ Follow",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = post.caption,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = post.hashtags,
                            color = ElectricCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MusicNote, contentDescription = "Music symbol", tint = TextSecondary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${post.soundTitle} - ${post.soundArtist}",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Floating action column on right
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Likes
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(onClick = onLike) {
                                Icon(
                                    imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Like button",
                                    tint = if (post.isLiked) RoseRed else Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Text(
                                text = formatCount(post.likesCount),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Comments
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(onClick = onCommentsOpen) {
                                Icon(
                                    imageVector = Icons.Outlined.ChatBubbleOutline,
                                    contentDescription = "Comments button",
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Text(
                                text = post.commentsCount.toString(),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Spinning record audio vinyl
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .rotate(rotation)
                                .clip(CircleShape)
                                .background(Color.Black)
                                .border(2.dp, ElectricCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(ElectricBlue)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StoryViewerPopup(
    story: Story,
    onDismiss: () -> Unit,
    onVote: (Int) -> Unit,
    onAnswer: (String) -> Unit
) {
    var textInput by remember { mutableStateOf("") }
    var hasVoted by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.95f))
            .clickable { onDismiss() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .clickable(enabled = false) {}, // prevent closing
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(story.creatorAvatar.ifEmpty { "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde" }),
                            contentDescription = story.creatorName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(story.creatorName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Story • 4 hours ago", color = TextSecondary, fontSize = 11.sp)
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close Story", tint = Color.White)
                }
            }

            // Center interactive sticker / poll representation
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (story.stickerType == "POLL") {
                    GlassyCard(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .border(1.dp, ElectricCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                        borderColor = ElectricCyan
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Poll, contentDescription = "Sticker Poll", tint = ElectricCyan, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = story.stickerQuestion,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            
                            val options = story.stickerOptions.split(",")
                            options.forEachIndexed { idx, opt ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (hasVoted) Color(0xFF1E2235) else ElectricBlue)
                                        .clickable {
                                            if (!hasVoted) {
                                                onVote(idx)
                                                hasVoted = true
                                            }
                                        }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (hasVoted) {
                                            "$opt (${if (idx == 0) story.pollVotesOption1 + 1 else story.pollVotesOption2} votes)"
                                        } else opt,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                } else if (story.stickerType == "QUESTION") {
                    GlassyCard(
                        modifier = Modifier.fillMaxWidth(0.85f),
                        borderColor = NeonPurple
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.QuestionAnswer, contentDescription = "Sticker Question", tint = NeonPurple, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = story.stickerQuestion,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            GlassyInput(
                                value = textInput,
                                onValueChange = { textInput = it },
                                placeholder = "Type something..."
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            GlassyButton(
                                text = "Reply",
                                onClick = {
                                    if (textInput.isNotEmpty()) {
                                        onAnswer(textInput)
                                        textInput = ""
                                        onDismiss()
                                    }
                                }
                            )
                        }
                    }
                } else {
                    // Regular media representation
                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF151724)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Image, contentDescription = "Static Image Placeholder", tint = TextSecondary, modifier = Modifier.size(64.dp))
                    }
                }
            }

            // Bottom Reactions Row
            Column(modifier = Modifier.fillMaxWidth()) {
                if (story.musicTitle.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x33FFFFFF))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.MusicNote, contentDescription = "Sound Overlays", tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Music Overlay: ${story.musicTitle} - ${story.musicArtist}",
                            color = Color.White,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val reactions = listOf("🔥", "😍", "😂", "😢", "👏", "😮")
                    reactions.forEach { reaction ->
                        Text(
                            text = reaction,
                            fontSize = 28.sp,
                            modifier = Modifier.clickable {
                                // Reaction trigger
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CommentsDrawer(
    post: FeedPost,
    onDismiss: () -> Unit,
    onSendComment: (String) -> Unit
) {
    var newCommentText by remember { mutableStateOf("") }

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
                .fillMaxHeight(0.6f)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(DarkSurface)
                .clickable(enabled = false) {}
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Drag Handle
                Box(
                    modifier = Modifier
                        .size(40.dp, 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White.copy(alpha = 0.3f))
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Comments (${post.commentsCount})",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close Comments", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Simulated comments list
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val commentItems = listOf(
                        Pair("NeonRider", "Bro that mix is absolutely insane! Replaying this on repeat! 🔥🎧"),
                        Pair("VibeMaster", "Can't wait to catch the live stream. Gifts ready! 🎁💎"),
                        Pair("PixelGeek", "The glassmorphic buttons in the interface look so futuristic. Best social UI of 2026.")
                    )
                    items(commentItems) { item ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(ElectricBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(item.first.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = item.first, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text(text = item.second, color = TextPrimary, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row {
                                    Text("2h ago", color = TextSecondary, fontSize = 10.sp)
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text("Reply", color = TextSecondary, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Input Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlassyInput(
                        value = newCommentText,
                        onValueChange = { newCommentText = it },
                        placeholder = "Add comment as @cyber_vyber...",
                        modifier = Modifier.weight(1f),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    if (newCommentText.isNotEmpty()) {
                                        onSendComment(newCommentText)
                                        newCommentText = ""
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Send Comment", tint = ElectricCyan)
                            }
                        }
                    )
                }
            }
        }
    }
}

fun formatCount(count: Int): String {
    return if (count >= 1000) {
        String.format("%.1fk", count / 1000.0)
    } else {
        count.toString()
    }
}
