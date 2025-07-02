package com.tahn.assignment.local.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tahn.assignment.local.database.entity.GithubUserEntity

@Dao
internal interface GithubUserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<GithubUserEntity>)

    @Query("SELECT * FROM github_users ORDER BY id ASC")
    fun getUserPagingSource(): PagingSource<Int, GithubUserEntity>

    @Query("DELETE FROM github_users")
    suspend fun clearAll()
}
