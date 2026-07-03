package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VybeDao {
    // Smart Vertical Feed
    @Query("SELECT * FROM feed_posts ORDER BY timestamp DESC")
    fun getAllFeedPosts(): Flow<List<FeedPost>>

    @Query("SELECT * FROM feed_posts WHERE category = :category ORDER BY timestamp DESC")
    fun getFeedPostsByCategory(category: String): Flow<List<FeedPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedPost(post: FeedPost)

    @Update
    suspend fun updateFeedPost(post: FeedPost)

    // Stories
    @Query("SELECT * FROM stories ORDER BY timestamp DESC")
    fun getStories(): Flow<List<Story>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: Story)

    @Update
    suspend fun updateStory(story: Story)

    // Private Circles
    @Query("SELECT * FROM private_circles ORDER BY timestamp DESC")
    fun getCircles(): Flow<List<PrivateCircle>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCircle(circle: PrivateCircle)

    @Query("SELECT * FROM circle_messages WHERE circleId = :circleId ORDER BY timestamp ASC")
    fun getCircleMessages(circleId: String): Flow<List<CircleMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCircleMessage(message: CircleMessage)

    @Query("SELECT * FROM circle_albums WHERE circleId = :circleId ORDER BY timestamp DESC")
    fun getCirclePhotos(circleId: String): Flow<List<CircleAlbumPhoto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCirclePhoto(photo: CircleAlbumPhoto)

    // Chat Messages
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllChatMessages(): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages WHERE (senderUsername = :u1 AND receiverUsername = :u2) OR (senderUsername = :u2 AND receiverUsername = :u1) ORDER BY timestamp ASC")
    fun getDirectChatMessages(u1: String, u2: String): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessage)

    @Query("DELETE FROM chat_messages WHERE isVanishMode = 1")
    suspend fun clearVanishMessages()

    // Community Spaces
    @Query("SELECT * FROM communities ORDER BY rankingPoints DESC")
    fun getCommunities(): Flow<List<Community>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommunity(community: Community)

    @Query("SELECT * FROM community_channels WHERE communityId = :communityId ORDER BY orderIndex ASC")
    fun getCommunityChannels(communityId: String): Flow<List<CommunityChannel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommunityChannel(channel: CommunityChannel)

    @Query("SELECT * FROM community_messages WHERE channelId = :channelId ORDER BY timestamp ASC")
    fun getCommunityMessages(channelId: String): Flow<List<CommunityMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommunityMessage(message: CommunityMessage)

    // Social Commerce
    @Query("SELECT * FROM creator_products ORDER BY buyCount DESC")
    fun getProducts(): Flow<List<CreatorProduct>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: CreatorProduct)

    // User Progress
    @Query("SELECT * FROM user_progress WHERE id = 'CURRENT_USER' LIMIT 1")
    fun getUserProgress(): Flow<UserProgress?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProgress(progress: UserProgress)
}

@Database(
    entities = [
        FeedPost::class,
        Story::class,
        PrivateCircle::class,
        CircleMessage::class,
        CircleAlbumPhoto::class,
        ChatMessage::class,
        Community::class,
        CommunityChannel::class,
        CommunityMessage::class,
        CreatorProduct::class,
        UserProgress::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vybeDao(): VybeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vybe_x_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
