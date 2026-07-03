package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.GlassyCard
import com.example.ui.components.GlassyButton
import com.example.ui.components.GlassyInput
import com.example.ui.components.GlowDivider
import com.example.ui.theme.*

@Composable
fun CreatorStudioScreen(viewModel: MainViewModel) {
    val prompt by viewModel.studioPrompt.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    val genCaption by viewModel.generatedCaption.collectAsState()
    val genHashtags by viewModel.generatedHashtags.collectAsState()
    val genIdeas by viewModel.generatedIdeas.collectAsState()

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CosmicBlack)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        
        // Studio Hero Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(NeonPurple.copy(alpha = 0.4f), Color.Transparent),
                        radius = 400f
                    )
                )
                .border(1.dp, NeonPurple.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AutoAwesome",
                        tint = ElectricCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI Creator Studio",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Powered by Gemini 3.5 Flash. Generate viral captions, optimized hashtags, and unique visual storyboards in seconds.",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        // Prompt Input Card
        GlassyCard(borderColor = ElectricBlue.copy(alpha = 0.3f)) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "What is your content about?",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                
                GlassyInput(
                    value = prompt,
                    onValueChange = { viewModel.setStudioPrompt(it) },
                    placeholder = "e.g. Hiking on Mt. Fuji under cyber lights, street style beats...",
                    singleLine = false,
                    modifier = Modifier.height(80.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isAiLoading) {
                        CircularProgressIndicator(
                            color = ElectricCyan,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 8.dp),
                            strokeWidth = 2.dp
                        )
                        Text("Dreaming up vibes...", color = ElectricCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    } else {
                        GlassyButton(
                            text = "Optimize Post",
                            onClick = { viewModel.runAiCreatorStudio() },
                            icon = {
                                Icon(Icons.Default.Bolt, contentDescription = "Optimize", tint = Color.White, modifier = Modifier.size(16.dp))
                            },
                            enabled = prompt.isNotEmpty()
                        )
                    }
                }
            }
        }

        // Output Panel with Animated Visibility
        AnimatedVisibility(
            visible = genCaption.isNotEmpty() || genHashtags.isNotEmpty() || genIdeas.isNotEmpty(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                
                // Caption Box
                if (genCaption.isNotEmpty()) {
                    GlassyCard(borderColor = ElectricCyan) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("✍️ GENERATED CAPTION", color = ElectricCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                IconButton(
                                    onClick = {
                                        copyToClipboard(context, genCaption)
                                        Toast.makeText(context, "Caption copied!", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextSecondary, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(genCaption, color = Color.White, fontSize = 13.sp, lineHeight = 18.sp)
                        }
                    }
                }

                // Hashtags Box
                if (genHashtags.isNotEmpty()) {
                    GlassyCard(borderColor = NeonPurple) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🏷️ OPTIMIZED HASHTAGS", color = NeonPurple, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                IconButton(
                                    onClick = {
                                        copyToClipboard(context, genHashtags)
                                        Toast.makeText(context, "Hashtags copied!", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextSecondary, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(genHashtags, color = ElectricCyan, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // Storyboard Ideas Box
                if (genIdeas.isNotEmpty()) {
                    GlassyCard(borderColor = ElectricBlue) {
                        Column {
                            Text("💡 AI CONTENT STRATEGY & CHALLENGES", color = ElectricBlue, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(genIdeas, color = TextPrimary, fontSize = 13.sp, lineHeight = 20.sp)
                        }
                    }
                }
                
                // Security Key Warning/Setup Helper (Option B requirement from Skill)
                GlassyCard(
                    borderColor = Color(0xFFFFB300).copy(alpha = 0.3f),
                    cornerRadius = 12.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Prototyping Mode Active",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "To use private AI generation in production, configure the GEMINI_API_KEY in your AI Studio Secrets panel.",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("VYBE X Generated", text)
    clipboard.setPrimaryClip(clip)
}
