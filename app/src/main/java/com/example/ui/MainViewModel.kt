package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = VybeRepository(database.vybeDao())

    // UI Navigation Route States
    private val _activeTab = MutableStateFlow("feed") // "feed", "chat", "studio", "spaces", "monetize"
    val activeTab: StateFlow<String> = _activeTab.asStateFlow()

    fun setActiveTab(tab: String) {
        _activeTab.value = tab
    }

    // Gamification & User Progress
    val userProgress: StateFlow<UserProgress> = repository.userProgress
        .map { it ?: UserProgress() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProgress())

    fun earnXp(amount: Int) {
        viewModelScope.launch {
            repository.awardXp(amount)
        }
    }

    // Smart Vertical Feed
    private val _feedCategory = MutableStateFlow("For You") // "For You", "Following", "Trending", "Local", "Global"
    val feedCategory: StateFlow<String> = _feedCategory.asStateFlow()

    val feedPosts: StateFlow<List<FeedPost>> = combine(repository.allFeedPosts, _feedCategory) { posts, category ->
        if (category == "For You") posts else posts.filter { it.category == category }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setFeedCategory(category: String) {
        _feedCategory.value = category
        earnXp(5) // Browse bonus XP
    }

    fun likePost(postId: String) {
        viewModelScope.launch {
            repository.toggleLikePost(postId)
            earnXp(10)
        }
    }

    fun followCreator(postId: String) {
        viewModelScope.launch {
            repository.toggleFollowCreator(postId)
            earnXp(15)
        }
    }

    // Stories Flow
    val stories: StateFlow<List<Story>> = repository.allStories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addStory(story: Story) {
        viewModelScope.launch {
            repository.addStory(story)
            earnXp(30) // Content creation bonus
        }
    }

    fun votePoll(storyId: String, optionIndex: Int) {
        viewModelScope.launch {
            repository.voteOnStoryPoll(storyId, optionIndex)
            earnXp(10)
        }
    }

    fun answerStoryQuestion(storyId: String, answer: String) {
        viewModelScope.launch {
            repository.respondToStoryQuestion(storyId, answer)
            earnXp(15)
        }
    }

    // Direct Chats & Vanish Mode
    val allChatMessages: StateFlow<List<ChatMessage>> = repository.allChatMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeChatUser = MutableStateFlow("dj_nebula")
    val activeChatUser: StateFlow<String> = _activeChatUser.asStateFlow()

    val directMessages: StateFlow<List<ChatMessage>> = _activeChatUser.flatMapLatest { user ->
        repository.getDirectMessages(user)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setActiveChatUser(username: String) {
        _activeChatUser.value = username
    }

    fun sendDirectMessage(text: String, isVanish: Boolean = false, isVoice: Boolean = false, voiceDur: Int = 0) {
        viewModelScope.launch {
            repository.sendChatMessage(_activeChatUser.value, text, isVanish, isVoice, voiceDur)
            earnXp(20)
        }
    }

    fun clearVanishMessages() {
        viewModelScope.launch {
            repository.clearVanishMessages()
        }
    }

    // Private Circles
    val circles: StateFlow<List<PrivateCircle>> = repository.allCircles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeCircleId = MutableStateFlow<String?>(null)
    val activeCircleId: StateFlow<String?> = _activeCircleId.asStateFlow()

    val activeCircleMessages: StateFlow<List<CircleMessage>> = _activeCircleId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.getCircleMessages(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeCirclePhotos: StateFlow<List<CircleAlbumPhoto>> = _activeCircleId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.getCirclePhotos(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setActiveCircle(circleId: String?) {
        _activeCircleId.value = circleId
    }

    fun createCircle(name: String, desc: String) {
        viewModelScope.launch {
            repository.createCircle(name, desc)
            earnXp(50)
        }
    }

    fun sendCircleMsg(text: String) {
        val circleId = _activeCircleId.value ?: return
        viewModelScope.launch {
            repository.sendCircleMessage(circleId, userProgress.value.name, text)
            earnXp(15)
        }
    }

    fun uploadCirclePhoto(photoUrl: String) {
        val circleId = _activeCircleId.value ?: return
        viewModelScope.launch {
            repository.uploadCirclePhoto(circleId, userProgress.value.name, photoUrl)
            earnXp(25)
        }
    }

    // Communities
    val communities: StateFlow<List<Community>> = repository.allCommunities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeCommunityId = MutableStateFlow<String?>("com_hq")
    val activeCommunityId: StateFlow<String?> = _activeCommunityId.asStateFlow()

    val communityChannels: StateFlow<List<CommunityChannel>> = _activeCommunityId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.getCommunityChannels(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeChannelId = MutableStateFlow<String?>("seed_announce_id")
    val activeChannelId: StateFlow<String?> = _activeChannelId.asStateFlow()

    val channelMessages: StateFlow<List<CommunityMessage>> = _activeChannelId.flatMapLatest { chanId ->
        if (chanId == null) flowOf(emptyList()) else repository.getCommunityMessages(chanId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectCommunity(communityId: String?) {
        _activeCommunityId.value = communityId
        _activeChannelId.value = null
    }

    fun selectChannel(channelId: String?) {
        _activeChannelId.value = channelId
    }

    fun createCommunity(name: String, desc: String) {
        viewModelScope.launch {
            repository.createCommunity(name, desc)
            earnXp(60)
        }
    }

    fun sendCommunityMessage(text: String) {
        val chanId = _activeChannelId.value ?: return
        viewModelScope.launch {
            repository.sendCommunityMessage(chanId, text)
            earnXp(15)
        }
    }

    // Social Commerce
    val products: StateFlow<List<CreatorProduct>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun purchaseProduct(productId: String, price: Double) {
        viewModelScope.launch {
            repository.buyProduct(productId)
            // If buying from self or simulation, we could reward
            repository.addRevenue(price * 0.9) // 90% creator revenue, 10% platform fee
            earnXp(100)
        }
    }

    // Creator Stats & Live Streaming simulation
    private val _liveGifts = MutableStateFlow<List<String>>(emptyList()) // List of flying gifts
    val liveGifts: StateFlow<List<String>> = _liveGifts.asStateFlow()

    private val _liveViewerCount = MutableStateFlow(1280)
    val liveViewerCount: StateFlow<Int> = _liveViewerCount.asStateFlow()

    fun sendGiftToLiveStream(giftName: String, value: Double) {
        viewModelScope.launch {
            _liveGifts.update { it + giftName }
            repository.addRevenue(value * 0.7) // 70% payout for virtual gifts
            earnXp(40)
        }
    }

    fun clearLiveGifts() {
        _liveGifts.value = emptyList()
    }

    // AI Creator Studio (Gemini Integration)
    private val _studioPrompt = MutableStateFlow("")
    val studioPrompt: StateFlow<String> = _studioPrompt.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _generatedCaption = MutableStateFlow("")
    val generatedCaption: StateFlow<String> = _generatedCaption.asStateFlow()

    private val _generatedHashtags = MutableStateFlow("")
    val generatedHashtags: StateFlow<String> = _generatedHashtags.asStateFlow()

    private val _generatedIdeas = MutableStateFlow("")
    val generatedIdeas: StateFlow<String> = _generatedIdeas.asStateFlow()

    fun setStudioPrompt(prompt: String) {
        _studioPrompt.value = prompt
    }

    fun runAiCreatorStudio() {
        val prompt = _studioPrompt.value
        if (prompt.isEmpty()) return

        viewModelScope.launch {
            _isAiLoading.value = true

            // Prompt engineering for captions
            val captionPrompt = "Write a highly engaging, catchy social media caption about: $prompt. Keep it modern, using relevant emojis. Do not output anything else, just the caption."
            val captionResult = GeminiService.generateText(captionPrompt)
            _generatedCaption.value = captionResult

            // Prompt engineering for hashtags
            val hashtagPrompt = "Generate 10 trending, high-engagement hashtags based on: $prompt. Output only the hashtags separated by spaces."
            val hashtagResult = GeminiService.generateText(hashtagPrompt)
            _generatedHashtags.value = hashtagResult

            // Prompt engineering for ideas
            val ideaPrompt = "Create 3 engaging content ideas or story challenges for a creator posting about: $prompt. Break it down into numbered bullets with modern styling."
            val ideaResult = GeminiService.generateText(ideaPrompt)
            _generatedIdeas.value = ideaResult

            _isAiLoading.value = false
            earnXp(50) // Creative AI bonus
        }
    }

    init {
        // Automatically seed database on first launch
        repository.seedInitialDataIfNecessary(viewModelScope)
    }
}
