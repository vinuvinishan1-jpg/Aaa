package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "feed_posts")
data class FeedPost(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val creatorName: String,
    val creatorUsername: String,
    val creatorAvatar: String,
    val isVerified: Boolean = false,
    val videoUrl: String = "",
    val imageUrl: String = "", // Optional visual display
    val caption: String,
    val hashtags: String, // space-separated
    val likesCount: Int = 1024,
    val commentsCount: Int = 89,
    val sharesCount: Int = 45,
    val soundTitle: String = "Original Sound",
    val soundArtist: String = "Creator",
    val isLiked: Boolean = false,
    val isFollowing: Boolean = false,
    val viewCount: Int = 5400,
    val category: String = "For You", // "For You", "Following", "Trending", "Local", "Global"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "stories")
data class Story(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val creatorName: String,
    val creatorAvatar: String,
    val mediaUrl: String = "",
    val mediaType: String = "IMAGE", // "IMAGE", "VIDEO"
    val musicTitle: String = "",
    val musicArtist: String = "",
    val stickerType: String = "NONE", // "POLL", "QUESTION", "NONE"
    val stickerQuestion: String = "",
    val stickerOptions: String = "", // comma-separated if POLL
    val pollVotesOption1: Int = 0,
    val pollVotesOption2: Int = 0,
    val viewerComments: String = "", // comma-separated feedback
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "private_circles")
data class PrivateCircle(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val coverImage: String,
    val inviteCode: String,
    val memberCount: Int = 1,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "circle_messages")
data class CircleMessage(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val circleId: String,
    val senderName: String,
    val senderAvatar: String,
    val messageText: String,
    val mediaUrl: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "circle_albums")
data class CircleAlbumPhoto(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val circleId: String,
    val uploadedBy: String,
    val photoUrl: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val senderUsername: String,
    val receiverUsername: String,
    val text: String,
    val isVanishMode: Boolean = false,
    val isRead: Boolean = false,
    val isVoiceMessage: Boolean = false,
    val voiceDurationSec: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "communities")
data class Community(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val iconUrl: String,
    val bannerUrl: String = "",
    val memberCount: Int = 120,
    val rankingPoints: Int = 450,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "community_channels")
data class CommunityChannel(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val communityId: String,
    val name: String,
    val type: String = "TEXT", // "TEXT", "VOICE"
    val orderIndex: Int = 0
)

@Entity(tableName = "community_messages")
data class CommunityMessage(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val channelId: String,
    val senderName: String,
    val senderAvatar: String,
    val isVerified: Boolean = false,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "creator_products")
data class CreatorProduct(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val creatorName: String,
    val name: String,
    val price: Double,
    val description: String,
    val imageUrl: String,
    val isDigital: Boolean = true,
    val buyCount: Int = 12
)

@Entity(tableName = "user_progress")
data class UserProgress(
    @PrimaryKey val id: String = "CURRENT_USER",
    val username: String = "cyber_vyber",
    val name: String = "Alex Rivera",
    val xp: Int = 240,
    val level: Int = 3,
    val streakCount: Int = 5,
    val isPremium: Boolean = false,
    val totalRevenue: Double = 0.0,
    val activeThemeName: String = "Cyber Neon"
)
