package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.components.GlowDivider
import com.example.ui.components.XPProgressBar
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        VybeXAppShell()
      }
    }
  }
}

@Composable
fun VybeXAppShell() {
  val viewModel: MainViewModel = viewModel()
  val activeTab by viewModel.activeTab.collectAsState()
  val userProgress by viewModel.userProgress.collectAsState()

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    bottomBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF090A13))
          .navigationBarsPadding() // Mandatory safe drawing margin to prevent system gesture collisions
      ) {
        GlowDivider(color = ElectricCyan)
        
        // Custom Glassmorphism Floating Dock
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
          horizontalArrangement = Arrangement.SpaceAround,
          verticalAlignment = Alignment.CenterVertically
        ) {
          val navItems = listOf(
            Triple("feed", Icons.Default.DynamicFeed, "Feed"),
            Triple("chat", Icons.Default.Forum, "Vyb Chat"),
            Triple("studio", Icons.Default.AutoAwesome, "Studio"),
            Triple("spaces", Icons.Default.Hub, "Spaces"),
            Triple("monetize", Icons.Default.Paid, "Monetize")
          )

          navItems.forEach { item ->
            val isSelected = activeTab == item.first
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { viewModel.setActiveTab(item.first) }
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Icon(
                imageVector = item.second,
                contentDescription = item.third,
                tint = if (isSelected) ElectricCyan else TextSecondary,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = item.third,
                color = if (isSelected) Color.White else TextSecondary,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            }
          }
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(CosmicBlack)
        .padding(
          top = innerPadding.calculateTopPadding(),
          bottom = innerPadding.calculateBottomPadding()
        )
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        
        // Continuous Gamification Level Header
        XPProgressBar(
          xp = userProgress.xp,
          level = userProgress.level,
          streakCount = userProgress.streakCount,
          modifier = Modifier.padding(16.dp)
        )

        // Tab Content Switching with Smooth Crossfade
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
          when (activeTab) {
            "feed" -> SmartFeedScreen(viewModel = viewModel)
            "chat" -> VybChatScreen(viewModel = viewModel)
            "studio" -> CreatorStudioScreen(viewModel = viewModel)
            "spaces" -> CommunitySpacesScreen(viewModel = viewModel)
            "monetize" -> CommerceAndStreamingScreen(viewModel = viewModel)
          }
        }
      }
    }
  }
}
