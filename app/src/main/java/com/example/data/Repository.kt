package com.example.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

class VybeRepository(private val dao: VybeDao) {

    // Smart Vertical Feed Queries
    val allFeedPosts: Flow<List<FeedPost>> = dao.getAllFeedPosts()
    fun getFeedByCategory(category: String): Flow<List<FeedPost>> = dao.getFeedPostsByCategory(category)

    suspend fun createFeedPost(post: FeedPost) {
        dao.insertFeedPost(post)
    }

    suspend fun toggleLikePost(postId: String) {
        // Safe database transaction
        val posts = dao.getAllFeedPosts().first()
        val post = posts.find { it.id == postId } ?: return
        val isNowLiked = !post.isLiked
        val newLikesCount = if (isNowLiked) post.likesCount + 1 else post.likesCount - 1
        dao.insertFeedPost(post.copy(isLiked = isNowLiked, likesCount = newLikesCount))
    }

    suspend fun toggleFollowCreator(postId: String) {
        val posts = dao.getAllFeedPosts().first()
        val post = posts.find { it.id == postId } ?: return
        val isNowFollowing = !post.isFollowing
        dao.insertFeedPost(post.copy(isFollowing = isNowFollowing))
    }

    // Stories Queries
    val allStories: Flow<List<Story>> = dao.getStories()

    suspend fun addStory(story: Story) {
        dao.insertStory(story)
    }

    suspend fun voteOnStoryPoll(storyId: String, optionIndex: Int) {
        val stories = dao.getStories().first()
        val story = stories.find { it.id == storyId } ?: return
        val updated = if (optionIndex == 0) {
            story.copy(pollVotesOption1 = story.pollVotesOption1 + 1)
        } else {
            story.copy(pollVotesOption2 = story.pollVotesOption2 + 1)
        }
        dao.insertStory(updated)
    }

    suspend fun respondToStoryQuestion(storyId: String, answer: String) {
        val stories = dao.getStories().first()
        val story = stories.find { it.id == storyId } ?: return
        val newComments = if (story.viewerComments.isEmpty()) {
            answer
        } else {
            "${story.viewerComments},$answer"
        }
        dao.insertStory(story.copy(viewerComments = newComments))
    }

    // Private Circles Queries
    val allCircles: Flow<List<PrivateCircle>> = dao.getCircles()

    fun getCircleMessages(circleId: String): Flow<List<CircleMessage>> = dao.getCircleMessages(circleId)
    fun getCirclePhotos(circleId: String): Flow<List<CircleAlbumPhoto>> = dao.getCirclePhotos(circleId)

    suspend fun createCircle(name: String, desc: String) {
        val code = (100000..999999).random().toString()
        val circle = PrivateCircle(
            name = name,
            description = desc,
            coverImage = "",
            inviteCode = code,
            memberCount = 1
        )
        dao.insertCircle(circle)
    }

    suspend fun sendCircleMessage(circleId: String, senderName: String, text: String) {
        val msg = CircleMessage(
            circleId = circleId,
            senderName = senderName,
            senderAvatar = "",
            messageText = text
        )
        dao.insertCircleMessage(msg)
    }

    suspend fun uploadCirclePhoto(circleId: String, uploader: String, photoUrl: String) {
        val photo = CircleAlbumPhoto(
            circleId = circleId,
            uploadedBy = uploader,
            photoUrl = photoUrl
        )
        dao.insertCirclePhoto(photo)
    }

    // Direct Chats Queries
    val allChatMessages: Flow<List<ChatMessage>> = dao.getAllChatMessages()

    fun getDirectMessages(otherUser: String): Flow<List<ChatMessage>> =
        dao.getDirectChatMessages("cyber_vyber", otherUser)

    suspend fun sendChatMessage(receiver: String, text: String, isVanish: Boolean = false, isVoice: Boolean = false, voiceDur: Int = 0) {
        val msg = ChatMessage(
            senderUsername = "cyber_vyber",
            receiverUsername = receiver,
            text = text,
            isVanishMode = isVanish,
            isVoiceMessage = isVoice,
            voiceDurationSec = voiceDur
        )
        dao.insertChatMessage(msg)
    }

    suspend fun clearVanishMessages() {
        dao.clearVanishMessages()
    }

    // Communities Queries
    val allCommunities: Flow<List<Community>> = dao.getCommunities()

    fun getCommunityChannels(communityId: String): Flow<List<CommunityChannel>> =
        dao.getCommunityChannels(communityId)

    fun getCommunityMessages(channelId: String): Flow<List<CommunityMessage>> =
        dao.getCommunityMessages(channelId)

    suspend fun createCommunity(name: String, desc: String) {
        val community = Community(
            name = name,
            iconUrl = "",
            bannerUrl = "",
            memberCount = 1
        )
        dao.insertCommunity(community)
        // Add default general channels
        val textChan = CommunityChannel(communityId = community.id, name = "general", type = "TEXT", orderIndex = 0)
        val voiceChan = CommunityChannel(communityId = community.id, name = "Lounge Room", type = "VOICE", orderIndex = 1)
        dao.insertCommunityChannel(textChan)
        dao.insertCommunityChannel(voiceChan)
    }

    suspend fun sendCommunityMessage(channelId: String, text: String) {
        val msg = CommunityMessage(
            channelId = channelId,
            senderName = "Alex Rivera",
            senderAvatar = "",
            isVerified = true,
            text = text
        )
        dao.insertCommunityMessage(msg)
    }

    // Social Commerce Queries
    val allProducts: Flow<List<CreatorProduct>> = dao.getProducts()

    suspend fun buyProduct(productId: String) {
        val products = dao.getProducts().first()
        val prod = products.find { it.id == postIdToId(productId, products) } ?: return
        dao.insertProduct(prod.copy(buyCount = prod.buyCount + 1))
    }

    private fun postIdToId(id: String, list: List<CreatorProduct>): String {
        return list.find { it.id == id }?.id ?: id
    }

    // User Progress & Gamification
    val userProgress: Flow<UserProgress?> = dao.getUserProgress()

    suspend fun awardXp(amount: Int) {
        val current = dao.getUserProgress().first() ?: UserProgress()
        var newXp = current.xp + amount
        var newLevel = current.level
        val xpNeeded = newLevel * 150
        if (newXp >= xpNeeded) {
            newXp -= xpNeeded
            newLevel += 1
        }
        dao.insertUserProgress(current.copy(xp = newXp, level = newLevel))
    }

    suspend fun setPremiumStatus(isPremium: Boolean) {
        val current = dao.getUserProgress().first() ?: UserProgress()
        dao.insertUserProgress(current.copy(isPremium = isPremium))
    }

    suspend fun addRevenue(amount: Double) {
        val current = dao.getUserProgress().first() ?: UserProgress()
        dao.insertUserProgress(current.copy(totalRevenue = current.totalRevenue + amount))
    }

    // Seeding database with amazing initial assets
    fun seedInitialDataIfNecessary(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            val existingPosts = dao.getAllFeedPosts().first()
            if (existingPosts.isEmpty()) {
                seedData()
            }
        }
    }

    private suspend fun seedData() {
        // Seed User Progress
        dao.insertUserProgress(
            UserProgress(
                username = "cyber_vyber",
                name = "Alex Rivera",
                xp = 240,
                level = 3,
                streakCount = 5,
                isPremium = false,
                totalRevenue = 1580.45
            )
        )

        // Seed Vertical Feed (Realistic TikTok-style metadata)
        val feedData = listOf(
            FeedPost(
                creatorName = "DJ Nebula",
                creatorUsername = "dj_nebula",
                creatorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
                isVerified = true,
                caption = "Dropping my latest cyberpunk deep house mix live in VYBE tonight! Who is ready to rave in the Metaverse? 🌌🎧",
                hashtags = "#metaverse #cyberpunk #deephouse #vybex #rave",
                likesCount = 24500,
                commentsCount = 1420,
                sharesCount = 612,
                soundTitle = "Cyber Rave (Deep Remix)",
                soundArtist = "DJ Nebula",
                category = "For You",
                isLiked = false
            ),
            FeedPost(
                creatorName = "Zara.Art",
                creatorUsername = "zara_design",
                creatorAvatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9",
                isVerified = false,
                caption = "Creating glassmorphic vector banners for creators using the new VYBE AI Studio. Rate this concept 1-10! 👇💎",
                hashtags = "#uidesign #glassmorphism #aiart #creators",
                likesCount = 8240,
                commentsCount = 310,
                sharesCount = 98,
                soundTitle = "Aesthetic Lofi Beats",
                soundArtist = "Chill Hop Collective",
                category = "Trending",
                isLiked = true
            ),
            FeedPost(
                creatorName = "Kaelen Hawk",
                creatorUsername = "kae_hawk",
                creatorAvatar = "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6",
                isVerified = true,
                caption = "Chasing neon lights in downtown Tokyo. Disappearing street stories coming up next. 🇯🇵🏮🏎️",
                hashtags = "#tokyodrift #neonlights #streetphotography #travelvlog",
                likesCount = 12500,
                commentsCount = 560,
                sharesCount = 245,
                soundTitle = "Hyperdrive (Synthwave)",
                soundArtist = "Retro Wave",
                category = "Global",
                isLiked = false
            )
        )
        feedData.forEach { dao.insertFeedPost(it) }

        // Seed Stories (Active 24h)
        val storiesData = listOf(
            Story(
                creatorName = "DJ Nebula",
                creatorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
                mediaType = "IMAGE",
                musicTitle = "Synthesized Dreams",
                musicArtist = "DJ Nebula",
                stickerType = "POLL",
                stickerQuestion = "Join live voice room today?",
                stickerOptions = "YES! 🔥,Maybe 💤",
                pollVotesOption1 = 412,
                pollVotesOption2 = 34
            ),
            Story(
                creatorName = "Kaelen Hawk",
                creatorAvatar = "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6",
                mediaType = "IMAGE",
                stickerType = "QUESTION",
                stickerQuestion = "What city should I travel to next? ✈️"
            )
        )
        storiesData.forEach { dao.insertStory(it) }

        // Seed Private Circles
        val circlesData = listOf(
            PrivateCircle(
                name = "Acoustic Crypto Labs",
                description = "Invite-only workspace for discussing decentralized creative commerce and web3 music.",
                coverImage = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad",
                inviteCode = "889140",
                memberCount = 14
            ),
            PrivateCircle(
                name = "Tokyo Street Runners",
                description = "Curated albums of urban architecture and midnight drift aesthetic.",
                coverImage = "https://images.unsplash.com/photo-1503899036084-c55cdd92da26",
                inviteCode = "240581",
                memberCount = 8
            )
        )
        circlesData.forEach { dao.insertCircle(it) }

        // Seed Circle Messages
        val circleId = "acoustic_labs_seed_id"
        dao.insertCircle(
            PrivateCircle(
                id = circleId,
                name = "Vibe Developers HQ",
                description = "Designing the VYBE X Jetpack Compose framework.",
                coverImage = "",
                inviteCode = "990145",
                memberCount = 5
            )
        )
        dao.insertCircleMessage(CircleMessage(circleId = circleId, senderName = "DJ Nebula", senderAvatar = "", messageText = "Welcome to the official developer circle! Let's build the future."))
        dao.insertCircleMessage(CircleMessage(circleId = circleId, senderName = "Zara.Art", senderAvatar = "", messageText = "Check out our glassmorphism components in the shared album!"))

        // Seed Chat History with mock users
        val mockChats = listOf(
            ChatMessage(senderUsername = "cyber_vyber", receiverUsername = "dj_nebula", text = "Yo, that mix was crazy! When is the next stream?", isRead = true),
            ChatMessage(senderUsername = "dj_nebula", receiverUsername = "cyber_vyber", text = "Appreciate it Alex! Going live tonight around 9PM EST.", isRead = true),
            ChatMessage(senderUsername = "cyber_vyber", receiverUsername = "zara_design", text = "Can you design a premium header for my shop?", isRead = true),
            ChatMessage(senderUsername = "zara_design", receiverUsername = "cyber_vyber", text = "For sure! Let's chat in my Private Circle.", isRead = false)
        )
        mockChats.forEach { dao.insertChatMessage(it) }

        // Seed Discord-style Communities
        val c1Id = "com_hq"
        val c2Id = "com_gaming"
        dao.insertCommunity(Community(id = c1Id, name = "VYBE Official HQ", iconUrl = "🏢", bannerUrl = "", memberCount = 15400, rankingPoints = 990))
        dao.insertCommunity(Community(id = c2Id, name = "Retro Arcade Club", iconUrl = "🕹️", bannerUrl = "", memberCount = 840, rankingPoints = 320))

        // Community Channels
        dao.insertCommunityChannel(CommunityChannel(communityId = c1Id, name = "announcements", type = "TEXT", orderIndex = 0))
        dao.insertCommunityChannel(CommunityChannel(communityId = c1Id, name = "general-chat", type = "TEXT", orderIndex = 1))
        dao.insertCommunityChannel(CommunityChannel(communityId = c1Id, name = "Live Lounge", type = "VOICE", orderIndex = 2))

        dao.insertCommunityChannel(CommunityChannel(communityId = c2Id, name = "high-scores", type = "TEXT", orderIndex = 0))
        dao.insertCommunityChannel(CommunityChannel(communityId = c2Id, name = "Voice Comms", type = "VOICE", orderIndex = 1))

        // Seed some Community Messages
        val announceChanId = "seed_announce_id"
        dao.insertCommunityChannel(CommunityChannel(id = announceChanId, communityId = c1Id, name = "rules", type = "TEXT", orderIndex = -1))
        dao.insertCommunityMessage(CommunityMessage(channelId = announceChanId, senderName = "System Moderator", senderAvatar = "", isVerified = true, text = "Welcome to the VYBE Official Community. Keep the vibes immaculate! ✨"))

        // Seed Products for Social Commerce
        val productsData = listOf(
            CreatorProduct(
                creatorName = "DJ Nebula",
                name = "Cyber Rave VIP Pass",
                price = 29.99,
                description = "Unlocks exclusive premium synth overlays, lifetime access to private raves, and private circle group access.",
                imageUrl = "🎫",
                isDigital = true,
                buyCount = 154
            ),
            CreatorProduct(
                creatorName = "Zara.Art",
                name = "Glassmorphism UI Template Pack",
                price = 9.90,
                description = "Beautiful modern vector components featuring glass background styles, custom neon icons, and luxury glowing frames.",
                imageUrl = "💎",
                isDigital = true,
                buyCount = 89
            ),
            CreatorProduct(
                creatorName = "Kaelen Hawk",
                name = "Limited Edition Tokyo Street Hoodie",
                price = 55.00,
                description = "Cotton black cyberpunk hoodie featuring neon embroidered details. Includes shipping worldwide.",
                imageUrl = "🧥",
                isDigital = false,
                buyCount = 42
            )
        )
        productsData.forEach { dao.insertProduct(it) }
    }
}
