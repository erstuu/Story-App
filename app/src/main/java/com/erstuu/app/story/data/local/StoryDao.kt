package com.erstuu.app.story.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.erstuu.app.story.models.Stories

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<Stories>)

    @Query("SELECT * FROM stories")
    fun getAllStories(): PagingSource<Int, Stories>

    @Query("DELETE FROM stories")
    suspend fun deleteAll()
}