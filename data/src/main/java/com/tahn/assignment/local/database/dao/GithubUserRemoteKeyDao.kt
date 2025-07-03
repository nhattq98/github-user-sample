package com.tahn.assignment.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tahn.assignment.local.database.entity.GithubUserRemoteKeyEntity

@Dao
internal interface GithubUserRemoteKeyDao {
    @Query("SELECT * FROM github_user_remote_key WHERE userId = :userId")
    suspend fun getRemoteKeyByUserId(userId: Int): GithubUserRemoteKeyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<GithubUserRemoteKeyEntity>)

    @Query("DELETE FROM github_user_remote_key")
    suspend fun clearAll()
}
