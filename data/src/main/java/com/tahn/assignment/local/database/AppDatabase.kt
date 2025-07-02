package com.tahn.assignment.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tahn.assignment.local.database.dao.GithubUserDao
import com.tahn.assignment.local.database.dao.GithubUserRemoteKeyDao
import com.tahn.assignment.local.database.entity.GithubUserEntity
import com.tahn.assignment.local.database.entity.GithubUserRemoteKeyEntity

@Database(entities = [GithubUserEntity::class, GithubUserRemoteKeyEntity::class], version = 1)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun githubUserDao(): GithubUserDao

    abstract fun githubUserRemoteKeyDao(): GithubUserRemoteKeyDao

    companion object {
        private const val DATABASE_NAME = "github_user_db"

        fun getInstance(context: Context) = Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()
    }
}
