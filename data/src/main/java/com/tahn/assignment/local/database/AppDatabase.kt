package com.tahn.assignment.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tahn.assignment.local.dao.GithubUserDao
import com.tahn.assignment.model.local.GithubUserEntity

@Database(entities = [GithubUserEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun githubUserDao(): GithubUserDao
}
